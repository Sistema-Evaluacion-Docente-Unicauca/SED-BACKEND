package co.edu.unicauca.sed.api.service;

import co.edu.unicauca.sed.api.dto.ApiResponse;
import co.edu.unicauca.sed.api.dto.ConsolidadoArchivoDTO;
import co.edu.unicauca.sed.api.dto.ConsolidadoDTO;
import co.edu.unicauca.sed.api.dto.InformacionConsolidadoDTO;
import co.edu.unicauca.sed.api.dto.actividad.ActividadPaginadaDTO;
import co.edu.unicauca.sed.api.model.*;
import co.edu.unicauca.sed.api.repository.*;
import co.edu.unicauca.sed.api.service.actividad.ActividadCalculoService;
import co.edu.unicauca.sed.api.service.actividad.ActividadTransformacionService;
import co.edu.unicauca.sed.api.service.notificacion.NotificacionDocumentoService;
import co.edu.unicauca.sed.api.specification.ConsolidadoSpecification;
import co.edu.unicauca.sed.api.service.actividad.ActividadQueryService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ConsolidadoService {

    private static final Logger logger = LoggerFactory.getLogger(ConsolidadoService.class);

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private ProcesoRepository procesoRepository;
    @Autowired
    private ActividadRepository actividadRepository;
    @Autowired
    private ActividadCalculoService calculoService;
    @Autowired
    private ActividadTransformacionService transformacionService;
    @Autowired
    private PeriodoAcademicoService periodoAcademicoService;
    @Autowired
    private ActividadQueryService actividadQueryService;
    @Autowired
    private ExcelService excelService;
    @Autowired
    private ConsolidadoRepository consolidadoRepository;
    @Autowired
    private ProcesoService procesoService;
    @Autowired
    private NotificacionDocumentoService notificacionDocumentoService;

    public ApiResponse<Page<InformacionConsolidadoDTO>> findAll(Pageable pageable, Boolean ascendingOrder,
            Integer idPeriodoAcademico, Integer idUsuario, String nombre, String identificacion,
            String facultad, String departamento, String categoria) {

        try {
            boolean order = (ascendingOrder != null) ? ascendingOrder : true;
            Sort sort = order ? Sort.by("fechaCreacion").ascending() : Sort.by("fechaCreacion").descending();
            Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

            if (idPeriodoAcademico == null) {
                idPeriodoAcademico = periodoAcademicoService.obtenerIdPeriodoAcademicoActivo();
            }

            ConsolidadoSpecification specBuilder = new ConsolidadoSpecification(periodoAcademicoService);
            Specification<Consolidado> specification = specBuilder.byFilters(idUsuario, nombre, identificacion,
                    facultad, departamento, categoria, idPeriodoAcademico);

            // Ejecutar consulta
            Page<Consolidado> consolidadoPage = consolidadoRepository.findAll(specification, sortedPageable);

            if (consolidadoPage.isEmpty()) {
                return new ApiResponse<>(204, "No se encontraron consolidados con los filtros aplicados.",
                        Page.empty());
            }

            // Convertir Page<Consolidado> a Page<InformacionConsolidadoDTO>
            Page<InformacionConsolidadoDTO> dtoPage = consolidadoPage.map(this::convertirAInformacionDTO);

            return new ApiResponse<>(200, "Consolidados obtenidos correctamente.", dtoPage);

        } catch (Exception e) {
            logger.error("❌ [ERROR] Error al obtener la lista de consolidados: {}", e.getMessage(), e);
            return new ApiResponse<>(500, "Error inesperado al obtener los consolidados.", Page.empty());
        }
    }

    public ApiResponse<Consolidado> findByOid(Integer oid) {
        try {
            Optional<Consolidado> consolidado = consolidadoRepository.findById(oid);
            if (consolidado.isEmpty()) {
                return new ApiResponse<>(404, "Consolidado con ID " + oid + " no encontrado.", null);
            }
            return new ApiResponse<>(200, "Consolidado encontrado correctamente.", consolidado.get());

        } catch (Exception e) {
            logger.error("❌ [ERROR] Error al buscar consolidado por ID: {}", e.getMessage(), e);
            return new ApiResponse<>(500, "Error inesperado al buscar el consolidado.", null);
        }
    }

    @Transactional
    public ApiResponse<Void> updateAllFromConsolidado(Integer oidConsolidado, Consolidado datosActualizar) {
        try {
            Consolidado consolidadoBase = consolidadoRepository.findById(oidConsolidado)
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Consolidado con ID " + oidConsolidado + " no encontrado."));

            Proceso procesoBase = Optional.ofNullable(consolidadoBase.getProceso())
                    .orElseThrow(() -> new IllegalStateException("Proceso no asociado al consolidado base."));

            Usuario evaluado = Optional.ofNullable(procesoBase.getEvaluado())
                    .orElseThrow(() -> new IllegalStateException("Evaluado no asociado al proceso base."));

            List<Proceso> procesosEvaluado = procesoRepository.findByEvaluado(evaluado);
            if (procesosEvaluado.isEmpty()) {
                return new ApiResponse<>(404, "No hay procesos asociados al evaluado.", null);
            }

            int actualizados = 0;
            for (Proceso proceso : procesosEvaluado) {
                Optional<Consolidado> consolidadoOpt = consolidadoRepository.findByProceso(proceso);
                if (consolidadoOpt.isPresent()) {
                    Consolidado consolidado = consolidadoOpt.get();
                    consolidado.setNombredocumento(datosActualizar.getNombredocumento());
                    consolidado.setRutaDocumento(datosActualizar.getRutaDocumento());
                    consolidado.setNota(datosActualizar.getNota().toUpperCase());
                    consolidadoRepository.save(consolidado);
                    actualizados++;
                }
            }

            if (actualizados == 0) {
                return new ApiResponse<>(404, "No se actualizaron consolidado(s) porque no se encontraron registros.",
                        null);
            }

            return new ApiResponse<>(200, "Se actualizaron " + actualizados + " consolidado(s) correctamente.", null);

        } catch (EntityNotFoundException e) {
            logger.warn("⚠️ [ERROR] {}", e.getMessage());
            return new ApiResponse<>(404, e.getMessage(), null);
        } catch (IllegalStateException e) {
            logger.warn("⚠️ [ERROR] {}", e.getMessage());
            return new ApiResponse<>(400, e.getMessage(), null);
        } catch (Exception e) {
            logger.error("❌ [ERROR] Error inesperado al actualizar consolidado(s): {}", e.getMessage(), e);
            return new ApiResponse<>(500, "Error inesperado al actualizar consolidado(s).", null);
        }
    }

    /**
     * Elimina un consolidado por su ID.
     *
     * @param oid ID del consolidado.
     */
    public ApiResponse<Void> delete(Integer oid) {
        try {
            if (!consolidadoRepository.existsById(oid)) {
                return new ApiResponse<>(404, "Consolidado con ID " + oid + " no encontrado.", null);
            }

            consolidadoRepository.deleteById(oid);
            logger.info("✅ [DELETE] Consolidado eliminado con ID: {}", oid);
            return new ApiResponse<>(200, "Consolidado eliminado correctamente.", null);

        } catch (Exception e) {
            logger.error("❌ [ERROR] Error al eliminar consolidado con ID {}: {}", oid, e.getMessage(), e);
            return new ApiResponse<>(500, "Error inesperado al eliminar el consolidado.", null);
        }
    }

    /**
     * Contenedor de datos comunes para el consolidado.
     */
    @Getter
    @AllArgsConstructor
    private static class BaseConsolidadoData {
        private Usuario evaluado;
        private UsuarioDetalle detalleUsuario;
        private PeriodoAcademico periodoAcademico;
        private List<Proceso> procesos;
    }

    /**
     * Obtiene los datos base del consolidado sin actividades.
     */
    private BaseConsolidadoData obtenerBaseConsolidado(Integer idEvaluado, Integer idPeriodoAcademico) {
        try {
            Usuario evaluado = usuarioRepository.findById(idEvaluado)
                    .orElseThrow(() -> new EntityNotFoundException("Usuario con ID " + idEvaluado + " no encontrado."));

            idPeriodoAcademico = (idPeriodoAcademico != null)
                    ? idPeriodoAcademico
                    : periodoAcademicoService.obtenerIdPeriodoAcademicoActivo();

            List<Proceso> procesos = procesoRepository.findByEvaluadoAndOidPeriodoAcademico_OidPeriodoAcademico(
                    evaluado, idPeriodoAcademico);

            if (procesos.isEmpty()) {
                throw new EntityNotFoundException("No hay procesos para el evaluado en el período académico.");
            }

            return new BaseConsolidadoData(
                    evaluado,
                    evaluado.getUsuarioDetalle(),
                    procesos.get(0).getOidPeriodoAcademico(),
                    procesos);
        } catch (EntityNotFoundException e) {
            logger.warn("⚠️ [ERROR] {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("❌ [ERROR] Error en obtenerBaseConsolidado: {}", e.getMessage(), e);
            throw new RuntimeException("Error al obtener los datos base del consolidado.", e);
        }
    }

    public ApiResponse<ConsolidadoDTO> generarInformacionGeneral(Integer idEvaluado, Integer idPeriodoAcademico) {
        try {
            BaseConsolidadoData baseData = obtenerBaseConsolidado(idEvaluado, idPeriodoAcademico);

            List<Actividad> actividades = baseData.getProcesos().stream()
                    .flatMap(proceso -> proceso.getActividades().stream()).collect(Collectors.toList());

            float totalHoras = calculoService.calcularTotalHoras(actividades);

            Map<String, List<Map<String, Object>>> actividadesPorTipo = transformacionService
                    .agruparActividadesPorTipo(actividades, totalHoras);

            double totalPorcentaje = calculoService.calcularTotalPorcentaje(actividadesPorTipo);
            double totalAcumulado = calculoService.calcularTotalAcumulado(actividadesPorTipo);
            ConsolidadoDTO consolidadoDTO = construirConsolidado(
                    baseData.getEvaluado(),
                    baseData.getDetalleUsuario(),
                    baseData.getPeriodoAcademico(),
                    null,
                    totalHoras,
                    totalPorcentaje,
                    totalAcumulado);
            return new ApiResponse<>(200, "Información general obtenida correctamente.", consolidadoDTO);
        } catch (EntityNotFoundException e) {
            logger.warn("⚠️ [ERROR] {}", e.getMessage());
            return new ApiResponse<>(404, e.getMessage(), null);
        } catch (Exception e) {
            logger.error("❌ [ERROR] Error inesperado al obtener información general: {}", e.getMessage(), e);
            return new ApiResponse<>(500, "Error inesperado al obtener la información general del consolidado.", null);
        }
    }

    public ConsolidadoDTO generarConsolidadoConActividades(Integer idEvaluado, Integer idPeriodoAcademico,
            Pageable pageable) {
        BaseConsolidadoData baseData = obtenerBaseConsolidado(idEvaluado, idPeriodoAcademico);
        Page<Actividad> actividadPage = actividadQueryService
                .obtenerActividadesPorProcesosPaginadas(baseData.getProcesos(), pageable);
        return construirConsolidadoDesdeActividades(baseData, actividadPage);
    }

    /**
     * Obtiene actividades paginadas para el consolidado.
     */
    public ApiResponse<ActividadPaginadaDTO> filtrarActividadesPaginadas(Integer idEvaluado, Integer idPeriodoAcademico,
            String nombreActividad, String idTipoActividad,
            String idTipoFuente, String idEstadoFuente,
            Pageable pageable) {
        try {
            Specification<Actividad> spec = actividadQueryService.filtrarActividades(
                    null, idEvaluado, nombreActividad, idTipoActividad, null, null,
                    idTipoFuente, idEstadoFuente, true, idPeriodoAcademico);

            Page<Actividad> actividadPage = actividadRepository.findAll(spec, pageable);
            ActividadPaginadaDTO actividadPaginadaDTO = transformacionService
                    .construirActividadPaginadaDTO(actividadPage);
            return new ApiResponse<>(200, "Actividades obtenidas correctamente.", actividadPaginadaDTO);

        } catch (Exception e) {
            logger.error("❌ [ERROR] Error inesperado al obtener actividades paginadas: {}", e.getMessage(), e);
            return new ApiResponse<>(500, "Error inesperado al obtener actividades paginadas.", null);
        }
    }

    /**
     * Aprobar un consolidado y generar el documento.
     */
    public ApiResponse<ConsolidadoArchivoDTO> aprobarConsolidado(Integer idEvaluado, Integer idEvaluador,
            Integer idPeriodoAcademico, String nota) {
        try {
            if (idPeriodoAcademico == null) {
                idPeriodoAcademico = periodoAcademicoService.obtenerIdPeriodoAcademicoActivo();
            }

            ConsolidadoDTO consolidadoDTO = generarConsolidadoConActividades(idEvaluado, idPeriodoAcademico,
                    Pageable.unpaged());

            if (nota != null) {
                nota = nota.toUpperCase();
            }

            String nombreDocumento = generarNombreDocumento(consolidadoDTO);
            Path excelPath = excelService.generarExcelConsolidado(consolidadoDTO, nombreDocumento, nota);

            Usuario evaluador = usuarioRepository.findById(idEvaluador)
                    .orElseThrow(
                            () -> new EntityNotFoundException("No se encontró el evaluador con ID: " + idEvaluador));

            Usuario evaluado = usuarioRepository.findById(idEvaluado)
                    .orElseThrow(() -> new EntityNotFoundException("No se encontró el evaluado con ID: " + idEvaluado));

            Proceso procesoExistente = procesoService.buscarProcesoExistente(idEvaluador, idEvaluado,
                    idPeriodoAcademico, procesoService.TIPO_CONSOLIDADO);

            if (procesoExistente == null) {
                procesoExistente = procesoService.crearNuevoProceso(idEvaluador, idEvaluado, idPeriodoAcademico);
                logger.info("✅ [PROCESO] Se ha creado un nuevo proceso de consolidado con ID: {}",
                        procesoExistente.getOidProceso());
            }

            Consolidado consolidadoExistente = consolidadoRepository.findByProceso(procesoExistente).orElse(null);
            if (consolidadoExistente == null) {
                consolidadoExistente = new Consolidado(procesoExistente);
                logger.info("✅ [CONSOLIDADO] Creando un nuevo consolidado para el proceso ID: {}",
                        procesoExistente.getOidProceso());
            }

            Integer oidConsolidado = guardarConsolidado(consolidadoExistente, procesoExistente, nombreDocumento,
                    excelPath.toString(), nota);
            notificacionDocumentoService.notificarJefeDepartamento("consolidado", evaluador, evaluado);
            ConsolidadoArchivoDTO archivoDTO = new ConsolidadoArchivoDTO(nombreDocumento, oidConsolidado);
            return new ApiResponse<>(200, "Consolidado aprobado correctamente.", archivoDTO);
        } catch (IOException e) {
            logger.error("❌ [ERROR] Error al generar el archivo de consolidado: {}", e.getMessage(), e);
            return new ApiResponse<>(500, "Error al generar el archivo de consolidado.", null);
        } catch (EntityNotFoundException e) {
            logger.warn("⚠️ [ERROR] {}", e.getMessage());
            return new ApiResponse<>(404, e.getMessage(), null);
        } catch (Exception e) {
            logger.error("❌ [ERROR] Error inesperado en aprobarConsolidado: {}", e.getMessage(), e);
            return new ApiResponse<>(500, "Error inesperado al aprobar el consolidado.", null);
        }
    }

    private Integer guardarConsolidado(Consolidado consolidadoExistente, Proceso nuevoProceso,
            String nombreDocumento, String rutaDocumento, String nota) {
        consolidadoExistente.setNombredocumento(nombreDocumento);
        consolidadoExistente.setRutaDocumento(rutaDocumento);
        consolidadoExistente.setNota(nota);
        consolidadoExistente.setFechaActualizacion(LocalDateTime.now());

        Consolidado consolidadoGuardado = consolidadoRepository.save(consolidadoExistente);

        return consolidadoGuardado.getOidConsolidado();
    }

    private String generarNombreDocumento(ConsolidadoDTO consolidadoDTO) {
        return "Consolidado-" + consolidadoDTO.getPeriodoAcademico() + "-"
                + consolidadoDTO.getNombreDocente().replace(" ", "_");
    }

    private ConsolidadoDTO construirConsolidadoDesdeActividades(BaseConsolidadoData baseData,
            Page<Actividad> actividadPage) {
        List<Actividad> actividades = actividadPage.getContent();
        float totalHoras = calculoService.calcularTotalHoras(actividades);

        Map<String, List<Map<String, Object>>> actividadesPorTipo = transformacionService
                .agruparActividadesPorTipo(actividades, totalHoras);
        double totalPorcentaje = calculoService.calcularTotalPorcentaje(actividadesPorTipo);
        double totalAcumulado = calculoService.calcularTotalAcumulado(actividadesPorTipo);

        ConsolidadoDTO consolidado = construirConsolidado(baseData.getEvaluado(), baseData.getDetalleUsuario(),
                baseData.getPeriodoAcademico(),
                actividadesPorTipo, totalHoras, totalPorcentaje, totalAcumulado);

        consolidado.setCurrentPage(actividadPage.getNumber());
        consolidado.setPageSize(actividadPage.getSize());
        consolidado.setTotalItems((int) actividadPage.getTotalElements());
        consolidado.setTotalPages(actividadPage.getTotalPages());

        return consolidado;
    }

    private ConsolidadoDTO construirConsolidado(
            Usuario evaluado, UsuarioDetalle detalleUsuario, PeriodoAcademico periodoAcademico,
            Map<String, List<Map<String, Object>>> actividadesPorTipo, float totalHoras,
            double totalPorcentaje, double totalAcumulado) {

        ConsolidadoDTO consolidado = new ConsolidadoDTO();
        consolidado.setNombreDocente(evaluado.getNombres() + " " + evaluado.getApellidos());
        consolidado.setCorreoElectronico(evaluado.getCorreo());
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

        return consolidado;
    }

    private InformacionConsolidadoDTO convertirAInformacionDTO(Consolidado consolidado) {
        InformacionConsolidadoDTO dto = new InformacionConsolidadoDTO();
        
        Usuario evaluado = consolidado.getProceso().getEvaluado();
        UsuarioDetalle detalle = evaluado.getUsuarioDetalle();
    
        dto.setNombreDocente(evaluado.getNombres() + " " + evaluado.getApellidos());
        dto.setNumeroIdentificacion(evaluado.getIdentificacion());
        dto.setFacultad(detalle.getFacultad());
        dto.setDepartamento(detalle.getDepartamento());
        dto.setCategoria(detalle.getCategoria());
        dto.setTipoContratacion(detalle.getContratacion());
        dto.setDedicacion(detalle.getDedicacion());
        dto.setNombreArchivo(consolidado.getNombredocumento());
        dto.setRutaArchivo(consolidado.getRutaDocumento());
    
        return dto;
    }
    
}