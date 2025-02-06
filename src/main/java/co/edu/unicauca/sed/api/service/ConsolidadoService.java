package co.edu.unicauca.sed.api.service;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import co.edu.unicauca.sed.api.dto.ConsolidadoDTO;
import co.edu.unicauca.sed.api.dto.FuenteDTO;
import co.edu.unicauca.sed.api.model.*;
import co.edu.unicauca.sed.api.repository.ConsolidadoRepository;
import co.edu.unicauca.sed.api.repository.ProcesoRepository;
import co.edu.unicauca.sed.api.repository.UsuarioRepository;
import co.edu.unicauca.sed.api.service.actividad.ActividadCalculoService;
import co.edu.unicauca.sed.api.service.actividad.ActividadTransformacionService;
import co.edu.unicauca.sed.api.utils.MathUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servicio para la generación y manejo de consolidados.
 */
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
    private ActividadCalculoService calculoService;

    @Autowired
    private ActividadTransformacionService transformacionService;

    @Autowired
    private ExcelService excelService;

    public Page<Consolidado> findAll(Pageable pageable, Boolean ascendingOrder) {
        try {
            boolean order = (ascendingOrder != null) ? ascendingOrder : true;
            Sort sort = order ? Sort.by("fechaCreacion").ascending() : Sort.by("fechaCreacion").descending();
            Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
            return consolidadoRepository.findAll(sortedPageable);
        } catch (Exception e) {
            logger.error("Error al realizar la consulta paginada de consolidado: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Encuentra un consolidado por su ID.
     *
     * @param oid ID del consolidado.
     * @return Consolidado encontrado o null si no existe.
     */
    public Consolidado findByOid(Integer oid) {
        return consolidadoRepository.findById(oid).orElse(null);
    }

    /**
     * Guarda un consolidado.
     *
     * @param consolidado Consolidado a guardar.
     * @return Consolidado guardado.
     */
    public Consolidado save(Consolidado consolidado) {
        return consolidadoRepository.save(consolidado);
    }

    /**
     * Actualiza todos los consolidados asociados al evaluado de un consolidado
     * específico.
     *
     * @param oidConsolidado ID del consolidado base para identificar los procesos
     *                       asociados.
     * @param consolidado    Datos actualizados para los consolidados.
     */
    @Transactional
    public void updateAllFromConsolidado(Integer oidConsolidado, Consolidado datosActualizar) {
        // Obtener el consolidado base
        Consolidado consolidadoBase = consolidadoRepository.findById(oidConsolidado).orElseThrow(() -> new IllegalArgumentException("Consolidado no encontrado"));

        Proceso procesoBase = Optional.ofNullable(consolidadoBase.getProceso()).orElseThrow(() -> new IllegalStateException("Proceso no asociado al consolidado base."));

        Usuario evaluado = Optional.ofNullable(procesoBase.getEvaluado()).orElseThrow(() -> new IllegalStateException("Evaluado no asociado al proceso base."));

        // Obtener los consolidados asociados al evaluado en una sola consulta
        List<Proceso> procesosEvaluado = procesoRepository.findByEvaluado(evaluado);
        if (procesosEvaluado.isEmpty()) {
            throw new IllegalArgumentException("No hay procesos asociados al evaluado.");
        }

        // Iterar sobre los procesos y actualizar los consolidados asociados
        for (Proceso proceso : procesosEvaluado) {
            Optional<Consolidado> consolidadoOpt = consolidadoRepository.findByProceso(proceso);
            if (consolidadoOpt.isPresent()) {
                Consolidado consolidado = consolidadoOpt.get();
                consolidado.setNombredocumento(datosActualizar.getNombredocumento());
                consolidado.setRutaDocumento(datosActualizar.getRutaDocumento());
                consolidado.setNota(datosActualizar.getNota().toUpperCase());
                consolidadoRepository.save(consolidado);
            }
        }
    }

    /**
     * Elimina un consolidado por su ID.
     *
     * @param oid ID del consolidado.
     */
    public void delete(Integer oid) {
        consolidadoRepository.deleteById(oid);
    }
    
    /**
     * Genera un listado de consolidado para un usuario evaluado en un período
     * académico.
     *
     * @param idEvaluado         ID del usuario evaluado.
     * @param idPeriodoAcademico ID del período académico.
     * @return Consolidado generado.
     */
    public ConsolidadoDTO generarConsolidado(Integer idEvaluado, Integer idPeriodoAcademico) {
        try {
            Usuario evaluado = obtenerEvaluado(idEvaluado);
            if (idPeriodoAcademico == null) {
                idPeriodoAcademico = periodoAcademicoService.obtenerPeriodoAcademicoActivo();
            }
            List<Proceso> procesosEvaluados = obtenerProcesosEvaluados(evaluado, idPeriodoAcademico);
            PeriodoAcademico periodoAcademico = obtenerPeriodoAcademico(procesosEvaluados);
            UsuarioDetalle detalleUsuario = evaluado.getUsuarioDetalle();
            List<Actividad> actividades = obtenerActividades(procesosEvaluados);

            float totalHoras = calculoService.calcularTotalHoras(actividades);

            Map<String, List<Map<String, Object>>> actividadesPorTipo = actividades.stream()
                    .sorted(Comparator.comparing(a -> a.getTipoActividad().getNombre()))
                    .collect(Collectors.groupingBy(actividad -> actividad.getTipoActividad().getNombre(),
                            Collectors.mapping(actividad -> transformacionService.transformarActividad(actividad, totalHoras),
                                    Collectors.toList())));

            double totalPorcentaje = actividadesPorTipo.values().stream()
                    .flatMap(List::stream).mapToDouble(actividad -> ((Number) actividad.get("porcentaje")).doubleValue())
                    .sum();

            double totalAcumulado = actividadesPorTipo.values().stream()
                    .flatMap(List::stream).mapToDouble(actividad -> ((Number) actividad.get("acumulado")).doubleValue())
                    .sum();

            totalAcumulado = Math.round(totalAcumulado);

            // Construcción del consolidado
            return construirConsolidado(evaluado, detalleUsuario, periodoAcademico, actividadesPorTipo, totalHoras, totalPorcentaje, totalAcumulado);
        } catch (Exception e) {
            logger.error("Error al generar consolidado para evaluado ID: {}", idEvaluado, e);
            throw e;
        }
    }

    /**
     * Obtiene un usuario evaluado por su ID.
     */
    private Usuario obtenerEvaluado(Integer idEvaluado) {
        return usuarioRepository.findById(idEvaluado).orElseThrow(() -> new IllegalArgumentException("Usuario con ID " + idEvaluado + " no encontrado."));
    }

    /**
     * Obtiene los procesos evaluados de un usuario en un período académico.
     */
    private List<Proceso> obtenerProcesosEvaluados(Usuario evaluado, Integer idPeriodoAcademico) {
        List<Proceso> procesos = procesoRepository.findByEvaluadoAndOidPeriodoAcademico_OidPeriodoAcademico(evaluado, idPeriodoAcademico);
        if (procesos.isEmpty()) {
            throw new IllegalArgumentException("No hay procesos para el evaluado en el período académico.");
        }
        return procesos;
    }

    /**
     * Obtiene el período académico de los procesos evaluados.
     */
    private PeriodoAcademico obtenerPeriodoAcademico(List<Proceso> procesosEvaluados) {
        return procesosEvaluados.get(0).getOidPeriodoAcademico();
    }

    /**
     * Obtiene todas las actividades de los procesos evaluados.
     */
    private List<Actividad> obtenerActividades(List<Proceso> procesosEvaluados) {
        return procesosEvaluados.stream().flatMap(proceso -> proceso.getActividades().stream()).collect(Collectors.toList());
    }
    
    /**
     * Builds the DTO for the consolidated report.
     */
    public ConsolidadoDTO construirConsolidado(Usuario evaluado, UsuarioDetalle detalleUsuario,
            PeriodoAcademico periodoAcademico, Map<String, List<Map<String, Object>>> actividadesPorTipo,
            float totalHoras, double totalPorcentaje, double totalAcumulado) {

        ConsolidadoDTO consolidado = new ConsolidadoDTO();

        consolidado.setNombreDocente(String.format("%s %s", evaluado.getNombres(), evaluado.getApellidos()));
        consolidado.setNumeroIdentificacion(evaluado.getIdentificacion());
        consolidado.setPeriodoAcademico(periodoAcademico.getIdPeriodo());
        consolidado.setFacultad(detalleUsuario.getFacultad());
        consolidado.setDepartamento(detalleUsuario.getDepartamento());
        consolidado.setCategoria(detalleUsuario.getCategoria());
        consolidado.setTipoContratacion(detalleUsuario.getContratacion());
        consolidado.setDedicacion(detalleUsuario.getDedicacion());
        consolidado.setActividades(actividadesPorTipo);
        consolidado.setHorasTotales(totalHoras);
        consolidado.setTotalPorcentaje(totalPorcentaje);
        consolidado.setTotalAcumulado(totalAcumulado);

        // Calculate total sources and completed sources
        int totalFuentes = calcularTotalFuentes(actividadesPorTipo);
        int fuentesCompletadas = calcularFuentesCompletadas(actividadesPorTipo);

        float porcentajeCompletado = MathUtils.calcularPorcentajeCompletado(totalFuentes, fuentesCompletadas);
        consolidado.setPorcentajeEvaluacionCompletado(porcentajeCompletado);

        return consolidado;
    }

    /**
     * Calculates the total number of sources from the activities map.
     */
    private int calcularTotalFuentes(Map<String, List<Map<String, Object>>> actividadesPorTipo) {
        return actividadesPorTipo.values().stream()
                .flatMap(List::stream)
                .mapToInt(actividad -> (int) actividad.getOrDefault("totalFuentes", 0))
                .sum();
    }

    /**
     * Calculates the number of completed sources from the activities map.
     */
    private int calcularFuentesCompletadas(Map<String, List<Map<String, Object>>> actividadesPorTipo) {
        return (int) actividadesPorTipo.values().stream()
                .flatMap(List::stream)
                .map(map -> map.get("fuentes")) // Extraer el objeto "fuentes"
                .filter(Objects::nonNull) // Evitar nulos
                .filter(List.class::isInstance) // Asegurar que es una lista
                .map(list -> (List<?>) list) // Convertir a lista sin suprimir warnings
                .flatMap(Collection::stream) // Convertir la lista en Stream
                .filter(FuenteDTO.class::isInstance) // Filtrar solo elementos FuenteDTO
                .map(fuente -> (FuenteDTO) fuente) // Convertirlos a FuenteDTO
                .filter(fuente -> "Diligenciado".equalsIgnoreCase(fuente.getEstadoFuente())) // Filtrar los completados
                .count();
    }

    public void aprobarConsolidado(Integer idEvaluado, Integer idPeriodoAcademico, String nota) throws IOException {
        ConsolidadoDTO consolidadoDTO = generarConsolidado(idEvaluado, idPeriodoAcademico);
    
        if (idPeriodoAcademico == null) {
            idPeriodoAcademico = periodoAcademicoService.obtenerPeriodoAcademicoActivo();
        }
    
        if (nota != null) {
            nota = nota.toUpperCase();
        }
    
        String nombreDocumento = generarNombreDocumento(consolidadoDTO);
        Path excelPath = excelService.generarExcelConsolidado(consolidadoDTO, nombreDocumento, nota);
    
        List<Proceso> procesos = obtenerProcesosDelEvaluado(idEvaluado, idPeriodoAcademico);
        actualizarConsolidados(procesos, nombreDocumento, excelPath.toString(), nota);
    }
    
    /**
     * Generates the document name for the consolidated report.
     */
    private String generarNombreDocumento(ConsolidadoDTO consolidadoDTO) {
        return "Consolidado-" + consolidadoDTO.getPeriodoAcademico() + "-" +
                consolidadoDTO.getNombreDocente().replace(" ", "_") + ".xlsx";
    }
    
    /**
     * Retrieves the list of processes associated with the evaluated user and academic period.
     */
    private List<Proceso> obtenerProcesosDelEvaluado(Integer idEvaluado, Integer idPeriodoAcademico) {
        List<Proceso> procesos = procesoRepository.findByEvaluado_OidUsuarioAndOidPeriodoAcademico_OidPeriodoAcademico(
                idEvaluado, idPeriodoAcademico);
    
        if (procesos.isEmpty()) {
            throw new IllegalArgumentException("No se encontraron procesos para el evaluado en el período académico especificado.");
        }
        return procesos;
    }
    
    /**
     * Updates or creates consolidated records for the given processes.
     */
    private void actualizarConsolidados(List<Proceso> procesos, String nombreDocumento, String rutaDocumento, String nota) {
        for (Proceso proceso : procesos) {
            Consolidado consolidado = consolidadoRepository.findByProceso(proceso)
                    .orElseGet(() -> crearNuevoConsolidado(proceso));
    
            consolidado.setNombredocumento(nombreDocumento);
            consolidado.setRutaDocumento(rutaDocumento);
            consolidado.setNota(nota);
            consolidado.setFechaActualizacion(LocalDateTime.now());
    
            consolidadoRepository.save(consolidado);
        }
    }
    
    /**
     * Creates a new consolidated entity for a given process.
     */
    private Consolidado crearNuevoConsolidado(Proceso proceso) {
        Consolidado consolidado = new Consolidado();
        consolidado.setProceso(proceso);
        return consolidado;
    }    
}
