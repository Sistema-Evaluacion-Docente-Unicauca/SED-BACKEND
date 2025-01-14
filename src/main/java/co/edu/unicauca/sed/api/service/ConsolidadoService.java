package co.edu.unicauca.sed.api.service;

import co.edu.unicauca.sed.api.dto.ConsolidadoDTO;
import co.edu.unicauca.sed.api.dto.FuenteDTO;
import co.edu.unicauca.sed.api.model.*;
import co.edu.unicauca.sed.api.repository.*;
import co.edu.unicauca.sed.api.utils.MathUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ConsolidadoService {

    private static final Logger logger = LoggerFactory.getLogger(ConsolidadoService.class);

    @Autowired
    private ConsolidadoRepository consolidadoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ProcesoRepository procesoRepository;

    @Autowired
    private PeriodoAcademicoService periodoAcademicoService;

    @Autowired
    private ExcelService excelService;

    @Autowired
    private PeriodoAcademicoRepository periodoAcademicoRepository;

    @Autowired
    private FileService fileService;

    // Encuentra todos los consolidados con paginación y ordenamiento
    public Page<Consolidado> findAll(Pageable pageable, Boolean ascendingOrder) {
        try {
            Sort sort = (ascendingOrder != null && ascendingOrder) ? Sort.by("fechaCreacion").ascending()
                    : Sort.by("fechaCreacion").descending();
            Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
            return consolidadoRepository.findAll(sortedPageable);
        } catch (Exception e) {
            logger.error("Error al realizar la consulta paginada de consolidado: {}", e.getMessage(), e);
            throw e;
        }
    }

    // Encuentra un consolidado por su ID
    public Consolidado findByOid(Integer oid) {
        return consolidadoRepository.findById(oid).orElse(null);
    }

    // Guarda un consolidado
    public Consolidado save(Consolidado consolidado) {
        return consolidadoRepository.save(consolidado);
    }

    // Elimina un consolidado por su ID
    public void delete(Integer oid) {
        consolidadoRepository.deleteById(oid);
    }
    
    // Lógica de aprobación y generación de consolidado
    @Transactional
    public void aprobarConsolidado(Integer idEvaluado, Integer idEvaluador, Integer idPeriodoAcademico, String nota)
            throws IOException {
        // Obtener el periodo académico activo si no se proporciona
        idPeriodoAcademico = (idPeriodoAcademico != null) ? idPeriodoAcademico : periodoAcademicoService.obtenerPeriodoAcademicoActivo();
        nota = (nota != null) ? nota.toUpperCase() : null;

        // Buscar los procesos de tipo "ACTIVIDAD"
        List<Proceso> procesosEvaluados = procesoRepository
                .findByEvaluadoAndOidPeriodoAcademico_OidPeriodoAcademicoAndNombreProceso(
                        usuarioRepository.findById(idEvaluado)
                                .orElseThrow(() -> new IllegalArgumentException("Evaluado no encontrado")),
                        idPeriodoAcademico,
                        "ACTIVIDAD");

        if (procesosEvaluados.isEmpty()) {
            throw new IllegalArgumentException("No se encontraron procesos de actividades para el evaluado.");
        }

        // Crear el proceso "CONSOLIDADO" si no existe
        Proceso procesoConsolidado = crearProcesoConsolidado(idEvaluado, idEvaluador, idPeriodoAcademico);
        if (procesoConsolidado == null) {
            throw new IllegalArgumentException("Error al crear el proceso de consolidado.");
        }

        logger.info("Proceso de consolidado creado con ID {}", procesoConsolidado.getOidProceso());

        // Iterar sobre los procesos de actividades
        for (Proceso proceso : procesosEvaluados) {
            // Generar consolidado para cada proceso de actividad
            ConsolidadoDTO consolidadoDTO = generarConsolidado(proceso.getEvaluado().getOidUsuario(), idPeriodoAcademico);

            String nombreDocumento = "Consolidado-" + consolidadoDTO.getPeriodoAcademico() + "-" + consolidadoDTO.getNombreDocente().replace(" ", "_");

            Path excelPath = excelService.generarExcelConsolidado(consolidadoDTO, nombreDocumento, nota);

            // Guardar el consolidado
            guardarConsolidado(proceso, nombreDocumento, excelPath, nota);
        }
    }

    private Proceso crearProcesoConsolidado(Integer idEvaluado, Integer idEvaluador, Integer idPeriodoAcademico) {
        // Verificar si ya existe un proceso "CONSOLIDADO"
        Optional<Proceso> procesoExistente = procesoRepository.findByEvaluadoAndEvaluadorAndOidPeriodoAcademicoAndNombreProceso(
                usuarioRepository.findById(idEvaluado).orElseThrow(() -> new IllegalArgumentException("Evaluado no encontrado")),
                usuarioRepository.findById(idEvaluador).orElseThrow(() -> new IllegalArgumentException("Evaluador no encontrado")),
                periodoAcademicoRepository.findById(idPeriodoAcademico).orElseThrow(() -> new IllegalArgumentException("Período académico no encontrado")),
                "CONSOLIDADO"
        );
    
        // Si existe, retornar el proceso existente
        if (procesoExistente.isPresent()) {
            logger.info("Proceso 'CONSOLIDADO' ya existente con ID: {}", procesoExistente.get().getOidProceso());
            return procesoExistente.get();
        }
    
        // Si no existe, crear un nuevo proceso "CONSOLIDADO"
        Proceso procesoConsolidado = new Proceso();
        procesoConsolidado.setEvaluado(usuarioRepository.findById(idEvaluado).orElseThrow());
        procesoConsolidado.setEvaluador(usuarioRepository.findById(idEvaluador).orElseThrow());
        procesoConsolidado.setOidPeriodoAcademico(periodoAcademicoRepository.findById(idPeriodoAcademico).orElseThrow());
        procesoConsolidado.setNombreProceso("CONSOLIDADO");
    
        return procesoRepository.save(procesoConsolidado);
    }
    
    private void guardarConsolidado(Proceso proceso, String nombreDocumento, Path excelPath, String nota) {
        Optional<Consolidado> consolidadoExistente = consolidadoRepository.findByProceso(proceso);
    
        if (consolidadoExistente.isPresent()) {
            // Actualizar consolidado existente
            Consolidado consolidado = consolidadoExistente.get();
            consolidado.setNombredocumento(nombreDocumento + ".xlsx");
            consolidado.setRutaDocumento(excelPath.toString());
            consolidado.setNota(nota);
            consolidado.setFechaActualizacion(LocalDateTime.now());
    
            logger.info("Actualizando consolidado para el proceso con ID {}", proceso.getOidProceso());
            consolidadoRepository.save(consolidado);
        } else {
            // Crear nuevo consolidado
            Consolidado nuevoConsolidado = new Consolidado();
            nuevoConsolidado.setProceso(proceso);
            nuevoConsolidado.setNombredocumento(nombreDocumento + ".xlsx");
            nuevoConsolidado.setRutaDocumento(excelPath.toString());
            nuevoConsolidado.setNota(nota);
            nuevoConsolidado.setFechaCreacion(LocalDateTime.now());
    
            logger.info("Guardando nuevo consolidado para el proceso con ID {}", proceso.getOidProceso());
            consolidadoRepository.save(nuevoConsolidado);
        }
    }

    // Método para generar el DTO de consolidado
    public ConsolidadoDTO generarConsolidado(Integer idEvaluado, Integer idPeriodoAcademico) {
        Usuario evaluado = usuarioRepository.findById(idEvaluado)
                .orElseThrow(() -> new IllegalArgumentException("Usuario con ID " + idEvaluado + " no encontrado."));
        idPeriodoAcademico = (idPeriodoAcademico != null) ? idPeriodoAcademico : periodoAcademicoService.obtenerPeriodoAcademicoActivo();

        List<Proceso> procesosEvaluados = procesoRepository
                .findByEvaluadoAndOidPeriodoAcademico_OidPeriodoAcademico(evaluado, idPeriodoAcademico);
        if (procesosEvaluados.isEmpty()) {
            throw new IllegalArgumentException("No hay procesos para el evaluado en el período académico.");
        }

        PeriodoAcademico periodoAcademico = procesosEvaluados.get(0).getOidPeriodoAcademico();

        // Extraer actividades de los procesos y calcular el total de horas
        float totalHoras = procesosEvaluados.stream()
            .flatMap(proceso -> Optional.ofNullable(proceso.getActividades()).orElse(Collections.emptyList()).stream())
            .map(Actividad::getHorasTotales)
            .reduce(0f, Float::sum);

        // Obtener las actividades agrupadas por tipo
        Map<String, List<Map<String, Object>>> actividadesPorTipo = obtenerActividadesAgrupadas(procesosEvaluados,
                totalHoras);

        // Calcular porcentaje completado basándose en fuentes diligenciadas
        int totalFuentesCompletadas = actividadesPorTipo.values().stream()
                .flatMap(List::stream)
                .flatMap(map -> {
                    Object fuentes = map.get("fuentes");
                    if (fuentes instanceof List<?>) {
                        @SuppressWarnings("unchecked")
                        List<FuenteDTO> fuentesList = (List<FuenteDTO>) fuentes;
                        return fuentesList.stream();
                    }
                    return Stream.empty();
                }).filter(fuente -> "DILIGENCIADO".equalsIgnoreCase(fuente.getEstadoFuente()))
                .mapToInt(fuente -> 1).sum();

        int totalFuentes = actividadesPorTipo.values().stream().flatMap(List::stream).mapToInt(map -> {
            Object value = map.get("totalFuentes");
            return value instanceof Number ? ((Number) value).intValue() : 0;
        }).sum();

        // Calcular porcentaje completado
        float porcentajeCompletado = MathUtils.calcularPorcentajeCompletado(totalFuentes, totalFuentesCompletadas);

        return construirConsolidado(evaluado, periodoAcademico, actividadesPorTipo, totalHoras, porcentajeCompletado);
    }

    private Map<String, List<Map<String, Object>>> obtenerActividadesAgrupadas(List<Proceso> procesos, float totalHoras) {
        return procesos.stream()
                .flatMap(p -> Optional.ofNullable(p.getActividades()).orElse(Collections.emptyList()).stream())
                .filter(a -> a.getTipoActividad() != null && a.getTipoActividad().getNombre() != null)
                .collect(Collectors.groupingBy(
                        a -> a.getTipoActividad().getNombre(),
                        Collectors.mapping(this::convertirActividadAMap, Collectors.toList())
                ));
    }
    
    // Método de conversión de Actividad a Map<String, Object>
    private Map<String, Object> convertirActividadAMap(Actividad actividad) {
        Map<String, Object> actividadMap = new HashMap<>();
    
        actividadMap.put("oidActividad", actividad.getOidActividad());
        actividadMap.put("codigoActividad", actividad.getCodigoActividad() != null ? actividad.getCodigoActividad() : "Sin Código");
        actividadMap.put("nombre", actividad.getNombre() != null ? actividad.getNombre() : "Sin Nombre");
        actividadMap.put("horasTotales", actividad.getHorasTotales() != null ? actividad.getHorasTotales() : 0f);
        actividadMap.put("informeEjecutivo", actividad.getInformeEjecutivo() != null ? actividad.getInformeEjecutivo() : false);
        actividadMap.put("codVRI", actividad.getCodVRI());
        actividadMap.put("estadoActividad", actividad.getEstadoActividad());
        actividadMap.put("fechaCreacion", actividad.getFechaCreacion());
        actividadMap.put("fechaActualizacion", actividad.getFechaActualizacion());
    
        // Convertir las fuentes a DTO y agregarlas al mapa
        List<FuenteDTO> fuentesDTO = Optional.ofNullable(actividad.getFuentes())
                .orElse(Collections.emptyList())
                .stream()
                .map(fuente -> new FuenteDTO(
                        fuente.getOidFuente(),
                        fuente.getEstadoFuente() != null ? fuente.getEstadoFuente().getNombreEstado() : "Sin Estado",
                        fuente.getCalificacion(),
                        fuente.getTipoFuente() != null ? fuente.getTipoFuente() : "Sin Tipo"))
                .collect(Collectors.toList());
    
        actividadMap.put("fuentes", fuentesDTO);
    
        // Validar y asignar totalFuentes
        actividadMap.put("totalFuentes", fuentesDTO.size());
    
        return actividadMap;
    }

    private ConsolidadoDTO construirConsolidado(Usuario evaluado, PeriodoAcademico periodoAcademico,
            Map<String, List<Map<String, Object>>> actividadesPorTipo, float totalHoras, double totalPorcentaje) {
        ConsolidadoDTO consolidado = new ConsolidadoDTO();
        consolidado.setNombreDocente(evaluado.getNombres() + " " + evaluado.getApellidos());
        consolidado.setPeriodoAcademico(periodoAcademico.getIdPeriodo());
        consolidado.setFacultad(evaluado.getUsuarioDetalle().getFacultad());
        consolidado.setDepartamento(evaluado.getUsuarioDetalle().getDepartamento());
        consolidado.setCategoria(evaluado.getUsuarioDetalle().getCategoria());
        consolidado.setTipoContratacion(evaluado.getUsuarioDetalle().getContratacion());
        consolidado.setDedicacion(evaluado.getUsuarioDetalle().getDedicacion());
        consolidado.setNumeroIdentificacion(evaluado.getUsuarioDetalle().getIdentificacion());
        consolidado.setHorasTotales(totalHoras);
        consolidado.setTotalPorcentaje(totalPorcentaje);
        consolidado.setActividades(actividadesPorTipo);
        return consolidado;
    }

    /**
     * Recupera un archivo asociado al consolidado.
     *
     * @param id       ID de la fuente.
     * @return Respuesta con el archivo como recurso descargable.
     */
    public ResponseEntity<?> getFile(Integer id) {
        try {
            // Busca la fuente por ID
            Consolidado consolidado = consolidadoRepository.findById(id).orElseThrow(() -> new RuntimeException("Consolidado con ID " + id + " no encontrada."));

            // Determina el archivo y la ruta según el flag
            String filePath = consolidado.getRutaDocumento();
            String fileName = consolidado.getNombredocumento();

            // Validar que la ruta no sea nula ni vacía
            if (filePath == null || filePath.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("El archivo solicitado no está disponible para este consolidado.");
            }

            // Recupera el recurso utilizando FileService
            Resource resource = fileService.getFileResource(filePath);

            // Retorna el archivo como respuesta
            return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"").body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ocurrió un error al procesar la solicitud. Error: " + e.getMessage());
        }
    }
}
