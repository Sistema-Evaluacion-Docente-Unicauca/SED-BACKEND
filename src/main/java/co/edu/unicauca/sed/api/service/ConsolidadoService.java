package co.edu.unicauca.sed.api.service;

import co.edu.unicauca.sed.api.dto.ConsolidadoDTO;
import co.edu.unicauca.sed.api.dto.FuenteDTO;
import co.edu.unicauca.sed.api.dto.actividad.ActividadPaginadaDTO;
import co.edu.unicauca.sed.api.model.*;
import co.edu.unicauca.sed.api.repository.*;
import co.edu.unicauca.sed.api.service.actividad.ActividadCalculoService;
import co.edu.unicauca.sed.api.service.actividad.ActividadTransformacionService;
import co.edu.unicauca.sed.api.utils.MathUtils;
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
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ConsolidadoService {

    private static final Logger logger = LoggerFactory.getLogger(ConsolidadoService.class);

    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private ProcesoRepository procesoRepository;
    @Autowired private ActividadRepository actividadRepository;
    @Autowired private ActividadCalculoService calculoService;
    @Autowired private ActividadTransformacionService transformacionService;
    @Autowired private PeriodoAcademicoService periodoAcademicoService;
    @Autowired private ExcelService excelService;
    @Autowired private ConsolidadoRepository consolidadoRepository;

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
                .orElseThrow(() -> new IllegalArgumentException("Consolidado no encontrado"));

        Proceso procesoBase = Optional.ofNullable(consolidadoBase.getProceso())
                .orElseThrow(() -> new IllegalStateException("Proceso no asociado al consolidado base."));

        Usuario evaluado = Optional.ofNullable(procesoBase.getEvaluado())
                .orElseThrow(() -> new IllegalStateException("Evaluado no asociado al proceso base."));

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
        Usuario evaluado = usuarioRepository.findById(idEvaluado)
                .orElseThrow(() -> new IllegalArgumentException("Usuario con ID " + idEvaluado + " no encontrado."));

        idPeriodoAcademico = (idPeriodoAcademico != null) 
                ? idPeriodoAcademico 
                : periodoAcademicoService.obtenerPeriodoAcademicoActivo();

        List<Proceso> procesos = procesoRepository.findByEvaluadoAndOidPeriodoAcademico_OidPeriodoAcademico(
                evaluado, idPeriodoAcademico);

        if (procesos.isEmpty()) {
            throw new IllegalArgumentException("No hay procesos para el evaluado en el período académico.");
        }

        return new BaseConsolidadoData(
                evaluado,
                evaluado.getUsuarioDetalle(),
                procesos.get(0).getOidPeriodoAcademico(),
                procesos
        );
    }

    /**
     * Obtiene los procesos del evaluado en un período académico.
     */
    private List<Proceso> obtenerProcesosDelEvaluado(Integer idEvaluado, Integer idPeriodoAcademico) {
        List<Proceso> procesos = procesoRepository.findByEvaluado_OidUsuarioAndOidPeriodoAcademico_OidPeriodoAcademico(
                idEvaluado, idPeriodoAcademico);

        if (procesos.isEmpty()) {
            throw new IllegalArgumentException("No se encontraron procesos para el evaluado en el período académico especificado.");
        }
        return procesos;
    }

    public ConsolidadoDTO generarInformacionGeneral(Integer idEvaluado, Integer idPeriodoAcademico) {
        BaseConsolidadoData baseData = obtenerBaseConsolidado(idEvaluado, idPeriodoAcademico);
        return construirConsolidado(baseData.getEvaluado(), baseData.getDetalleUsuario(), baseData.getPeriodoAcademico(), null, 0, 0, 0);
    }

    public ConsolidadoDTO generarConsolidadoConActividades(Integer idEvaluado, Integer idPeriodoAcademico, Pageable pageable) {
        BaseConsolidadoData baseData = obtenerBaseConsolidado(idEvaluado, idPeriodoAcademico);
        Page<Actividad> actividadPage = obtenerActividadesPaginadas(baseData.getProcesos(), pageable);
        return construirConsolidadoDesdeActividades(baseData, actividadPage);
    }

    public ActividadPaginadaDTO obtenerActividadesPaginadas(Integer idEvaluado, Integer idPeriodoAcademico, Pageable pageable) {
        BaseConsolidadoData baseData = obtenerBaseConsolidado(idEvaluado, idPeriodoAcademico);
        Page<Actividad> actividadPage = obtenerActividadesPaginadas(baseData.getProcesos(), pageable);
        return construirActividadPaginadaDTO(actividadPage);
    }

    public void aprobarConsolidado(Integer idEvaluado, Integer idPeriodoAcademico, String nota) throws IOException {

        if (idPeriodoAcademico == null) {
            idPeriodoAcademico = periodoAcademicoService.obtenerPeriodoAcademicoActivo();
        }
        BaseConsolidadoData baseData = obtenerBaseConsolidado(idEvaluado, idPeriodoAcademico);
        ConsolidadoDTO consolidadoDTO = generarConsolidadoConActividades(idEvaluado, idPeriodoAcademico, Pageable.unpaged());

        if (nota != null) {
            nota = nota.toUpperCase();
        }

        String nombreDocumento = generarNombreDocumento(consolidadoDTO);
        Path excelPath = excelService.generarExcelConsolidado(consolidadoDTO, nombreDocumento, nota);

        List<Proceso> procesos = obtenerProcesosDelEvaluado(idEvaluado, idPeriodoAcademico);
        actualizarConsolidados(procesos, nombreDocumento, excelPath.toString(), nota);
    }

    private void actualizarConsolidados(List<Proceso> procesos, String nombreDocumento, String rutaDocumento, String nota) {
        for (Proceso proceso : procesos) {
            Consolidado consolidado = consolidadoRepository.findByProceso(proceso)
                    .orElseGet(() -> new Consolidado(proceso));

            consolidado.setNombredocumento(nombreDocumento);
            consolidado.setRutaDocumento(rutaDocumento);
            consolidado.setNota(nota);
            consolidado.setFechaActualizacion(LocalDateTime.now());

            consolidadoRepository.save(consolidado);
        }
    }

    private Page<Actividad> obtenerActividadesPaginadas(List<Proceso> procesos, Pageable pageable) {
        List<Integer> procesoIds = procesos.stream().map(Proceso::getOidProceso).collect(Collectors.toList());
        return actividadRepository.findByProcesos(procesoIds, pageable);
    }

    private ActividadPaginadaDTO construirActividadPaginadaDTO(Page<Actividad> actividadPage) {
        List<Actividad> actividades = actividadPage.getContent();
        float totalHoras = calculoService.calcularTotalHoras(actividades);

        Map<String, List<Map<String, Object>>> actividadesPorTipo = actividades.stream()
                .collect(Collectors.groupingBy(
                        actividad -> actividad.getTipoActividad().getNombre(),
                        Collectors.mapping(
                                actividad -> transformacionService.transformarActividad(actividad, totalHoras),
                                Collectors.toList())));

        ActividadPaginadaDTO actividadPaginadaDTO = new ActividadPaginadaDTO();
        actividadPaginadaDTO.setActividades(actividadesPorTipo);
        actividadPaginadaDTO.setCurrentPage(actividadPage.getNumber());
        actividadPaginadaDTO.setPageSize(actividadPage.getSize());
        actividadPaginadaDTO.setTotalItems((int) actividadPage.getTotalElements());
        actividadPaginadaDTO.setTotalPages(actividadPage.getTotalPages());

        return actividadPaginadaDTO;
    }

    private String generarNombreDocumento(ConsolidadoDTO consolidadoDTO) {
        return "Consolidado-" + consolidadoDTO.getPeriodoAcademico() + "-" +
                consolidadoDTO.getNombreDocente().replace(" ", "_") + ".xlsx";
    }


    /**
     * Construye el ConsolidadoDTO con actividades paginadas.
     */
    private ConsolidadoDTO construirConsolidadoDesdeActividades(BaseConsolidadoData baseData, Page<Actividad> actividadPage) {
        List<Actividad> actividades = actividadPage.getContent();
        float totalHoras = calculoService.calcularTotalHoras(actividades);

        Map<String, List<Map<String, Object>>> actividadesPorTipo = actividades.stream()
                .collect(Collectors.groupingBy(
                        actividad -> actividad.getTipoActividad().getNombre(),
                        Collectors.mapping(
                                actividad -> transformacionService.transformarActividad(actividad, totalHoras),
                                Collectors.toList())));

        ConsolidadoDTO consolidado = construirConsolidado(
                baseData.getEvaluado(), baseData.getDetalleUsuario(), baseData.getPeriodoAcademico(),
                actividadesPorTipo, totalHoras, calcularTotalPorcentaje(actividadesPorTipo),
                calcularTotalAcumulado(actividadesPorTipo));

        consolidado.setCurrentPage(actividadPage.getNumber());
        consolidado.setPageSize(actividadPage.getSize());
        consolidado.setTotalItems((int) actividadPage.getTotalElements());
        consolidado.setTotalPages(actividadPage.getTotalPages());

        return consolidado;
    }

    /**
     * Construye el DTO del consolidado.
     */
    private ConsolidadoDTO construirConsolidado(
            Usuario evaluado, UsuarioDetalle detalleUsuario, PeriodoAcademico periodoAcademico,
            Map<String, List<Map<String, Object>>> actividadesPorTipo, float totalHoras,
            double totalPorcentaje, double totalAcumulado) {

        ConsolidadoDTO consolidado = new ConsolidadoDTO();
        consolidado.setNombreDocente(evaluado.getNombres() + " " + evaluado.getApellidos());
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



    /**
     * Calcula el total de porcentaje de todas las actividades.
     */
    private double calcularTotalPorcentaje(Map<String, List<Map<String, Object>>> actividadesPorTipo) {
        return actividadesPorTipo.values().stream()
                .flatMap(List::stream) // Convertir todas las actividades a un solo flujo
                .mapToDouble(actividad -> ((Number) actividad.getOrDefault("porcentaje", 0)).doubleValue())
                .sum();
    }

    /**
     * Calcula el total acumulado de todas las actividades.
     */
    private double calcularTotalAcumulado(Map<String, List<Map<String, Object>>> actividadesPorTipo) {
        return actividadesPorTipo.values().stream()
                .flatMap(List::stream) // Convertir todas las actividades a un solo flujo
                .mapToDouble(actividad -> ((Number) actividad.getOrDefault("acumulado", 0)).doubleValue())
                .sum();
    }

}