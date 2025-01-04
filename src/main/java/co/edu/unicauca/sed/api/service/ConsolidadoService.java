package co.edu.unicauca.sed.api.service;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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

    /**
     * Encuentra todos los consolidados con soporte de paginación y ordenamiento.
     *
     * @param pageable       Objeto para definir la paginación (tamaño de página y
     *                       número de página).
     * @param ascendingOrder Define si el orden es ascendente (true) o descendente
     *                       (false).
     * @return Página de consolidados que coinciden con los criterios especificados.
     * @throws Exception En caso de error al realizar la consulta.
     */
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
        Consolidado consolidadoBase = consolidadoRepository.findById(oidConsolidado)
                .orElseThrow(() -> {
                    return new IllegalArgumentException("Consolidado no encontrado");
                });

        // Obtener el evaluado a través del proceso del consolidado base
        Proceso procesoBase = consolidadoBase.getProceso();
        if (procesoBase == null) {
            throw new IllegalStateException("Proceso no asociado al consolidado base.");
        }

        Usuario evaluado = procesoBase.getEvaluado();
        if (evaluado == null) {
            throw new IllegalStateException("Evaluado no asociado al proceso base.");
        }

        // Obtener todos los procesos asociados al evaluado
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
                consolidado.setNota(datosActualizar.getNota());
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

    // Lógica de negocio específica
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
            // Cálculo de totales
            float totalHoras = calculoService.calcularTotalHoras(actividades);

            // Agrupación y transformación de actividades
            Map<String, List<Map<String, Object>>> actividadesPorTipo = actividades.stream()
                    .sorted(Comparator.comparing(a -> a.getTipoActividad().getNombre())) // Ordenar por tipo de actividad
                    .collect(Collectors.groupingBy(
                            actividad -> actividad.getTipoActividad().getNombre(),
                            Collectors.mapping(
                                    actividad -> transformacionService.transformarActividad(actividad, totalHoras),
                                    Collectors.toList())));
            double totalPorcentaje = actividadesPorTipo.values().stream()
                    .flatMap(List::stream)
                    .mapToDouble(actividad -> ((Number) actividad.get("porcentaje")).doubleValue())
                    .sum();

            double totalAcumulado = actividadesPorTipo.values().stream()
                    .flatMap(List::stream)
                    .mapToDouble(actividad -> ((Number) actividad.get("acumulado")).doubleValue())
                    .sum();

            // Construcción del consolidado
            return construirConsolidado(evaluado, detalleUsuario, periodoAcademico, actividadesPorTipo, totalHoras,
                    totalPorcentaje, totalAcumulado);
        } catch (Exception e) {
            logger.error("Error al generar consolidado para evaluado ID: {}", idEvaluado, e);
            throw e;
        }
    }

    /**
     * Obtiene un usuario evaluado por su ID.
     */
    private Usuario obtenerEvaluado(Integer idEvaluado) {
        return usuarioRepository.findById(idEvaluado)
                .orElseThrow(() -> new IllegalArgumentException("Usuario con ID " + idEvaluado + " no encontrado."));
    }

    /**
     * Obtiene los procesos evaluados de un usuario en un período académico.
     */
    private List<Proceso> obtenerProcesosEvaluados(Usuario evaluado, Integer idPeriodoAcademico) {
        List<Proceso> procesos = procesoRepository.findByEvaluadoAndOidPeriodoAcademico_OidPeriodoAcademico(evaluado,
                idPeriodoAcademico);
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
        return procesosEvaluados.stream()
                .flatMap(proceso -> proceso.getActividades().stream())
                .collect(Collectors.toList());
    }

    /**
     * Construye el DTO del consolidado.
     */
    public ConsolidadoDTO construirConsolidado(Usuario evaluado, UsuarioDetalle detalleUsuario,
            PeriodoAcademico periodoAcademico, Map<String, List<Map<String, Object>>> actividadesPorTipo,
            float totalHoras, double totalPorcentaje, double totalAcumulado) {
        ConsolidadoDTO consolidado = new ConsolidadoDTO();

        consolidado.setNombreDocente(evaluado.getNombres() + " " + evaluado.getApellidos());
        consolidado.setNumeroIdentificacion(detalleUsuario.getIdentificacion());
        consolidado.setPeriodoAcademico(periodoAcademico.getIdPeriodo());
        consolidado.setFacultad(detalleUsuario.getFacultad());
        consolidado.setDepartamento(detalleUsuario.getDepartamento());
        consolidado.setCategoria(detalleUsuario.getCategoria());
        consolidado.setTipoContratacion(detalleUsuario.getContratacion());
        consolidado.setDedicacion(detalleUsuario.getDedicacion());
        consolidado.setActividades(actividadesPorTipo);
        consolidado.setTotalHoras(totalHoras);
        consolidado.setTotalPorcentaje(totalPorcentaje);
        consolidado.setTotalAcumulado(totalAcumulado);

        // Calcular `totalFuentes` y `fuentesCompletadas`
        int totalFuentes = actividadesPorTipo.values().stream().flatMap(List::stream)
                .mapToInt(actividad -> (int) actividad.get("totalFuentes")).sum();

        int fuentesCompletadas = actividadesPorTipo.values().stream().flatMap(List::stream).flatMap(map -> {
            Object fuentes = map.get("fuentes"); // Obtener el valor asociado a "fuentes"
            if (fuentes instanceof List<?>) { // Validar que sea una lista
                @SuppressWarnings("unchecked")
                List<FuenteDTO> fuentesList = (List<FuenteDTO>) fuentes; // Cambiar a FuenteDTO
                return fuentesList.stream(); // Stream<FuenteDTO>
            }
            return Stream.empty(); // Si no es una lista, devuelve un stream vacío
        }).mapToInt(fuente -> {
            // Validar y contar las fuentes con estado "Diligenciado"
            if ("Diligenciado".equalsIgnoreCase(fuente.getEstadoFuente())) {
                return 1;
            }
            return 0;
        }).sum();

        // Calcular porcentaje completado
        float porcentajeCompletado = MathUtils.calcularPorcentajeCompletado(totalFuentes, fuentesCompletadas);
        consolidado.setPorcentajeEvaluacionCompletado(porcentajeCompletado);

        return consolidado;
    }

    /**
     * Aprueba y guarda el consolidado en la base de datos.
     *
     * @param idEvaluado         ID del evaluado.
     * @param idPeriodoAcademico ID del período académico.
     * @param nota               Nota opcional.
     * @throws IOException Si ocurre un error en el guardado del Excel.
     */
    public void aprobarConsolidado(Integer idEvaluado, Integer idPeriodoAcademico, String nota) throws IOException {
        ConsolidadoDTO consolidadoDTO = generarConsolidado(idEvaluado, idPeriodoAcademico);

        if (idPeriodoAcademico == null) {
            idPeriodoAcademico = periodoAcademicoService.obtenerPeriodoAcademicoActivo();
        }

        String nombreDocumento = "Consolidado-" + consolidadoDTO.getPeriodoAcademico() + "-"
                + consolidadoDTO.getNombreDocente().replace(" ", "_");

        Path excelPath = excelService.generarExcelConsolidado(consolidadoDTO, nombreDocumento, nota);

        List<Proceso> procesos = procesoRepository
                .findByEvaluado_OidUsuarioAndOidPeriodoAcademico_OidPeriodoAcademico(idEvaluado, idPeriodoAcademico);

        if (procesos.isEmpty()) {
            throw new IllegalArgumentException(
                    "No se encontraron procesos para el evaluado en el período académico especificado.");
        }

        for (Proceso proceso : procesos) {
            // Buscar si existe un consolidado asociado al proceso
            Optional<Consolidado> consolidadoExistente = consolidadoRepository.findByProceso(proceso);

            Consolidado consolidado;

            if (consolidadoExistente.isPresent()) {
                consolidado = consolidadoExistente.get();
            } else {
                consolidado = new Consolidado();
                consolidado.setProceso(proceso);
            }

            consolidado.setNombredocumento(nombreDocumento + ".xlsx");
            consolidado.setRutaDocumento(excelPath.toString());
            consolidado.setNota(nota);
            consolidado.setFechaActualizacion(LocalDateTime.now());

            consolidadoRepository.save(consolidado);
        }
    }

}
