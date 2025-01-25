package co.edu.unicauca.sed.api.service.actividad;

import co.edu.unicauca.sed.api.dto.actividad.ActividadBaseDTO;
import co.edu.unicauca.sed.api.mapper.ActividadMapper;
import co.edu.unicauca.sed.api.model.*;
import co.edu.unicauca.sed.api.repository.*;
import co.edu.unicauca.sed.api.service.PeriodoAcademicoService;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

/**
 * Servicio para gestionar las actividades, incluyendo operaciones de consulta,
 * creación, actualización, y eliminación.
 */
@Service
public class ActividadService {

    @Autowired
    private ActividadRepository actividadRepository;

    @Autowired
    private ProcesoRepository procesoRepository;

    @Autowired
    private ActividadDTOService actividadDTOService;

    @Autowired
    private ActividadSortService actividadSortService;

    @Autowired
    private ActividadMapper actividadMapper;

    @Autowired
    private PeriodoAcademicoService periodoAcademicoService;

    @Autowired
    private ActividadDetalleService actividadDetalleService;

    @Autowired
    private EstadoActividadRepository estadoActividadRepository;

    @Autowired
    private FuenteRepository fuenteRepository;

    @Autowired
    private EstadoFuenteRepository estadoFuenteRepository;

    /**
     * Recupera todas las actividades junto con sus fuentes asociadas con
     * paginación.
     *
     * @param pageable       Parámetros de paginación.
     * @param ascendingOrder Indica si las actividades deben ordenarse de forma
     *                       ascendente.
     * @return Página de actividades en formato DTO.
     */
    public Page<ActividadBaseDTO> findAll(Pageable pageable, Boolean ascendingOrder) {
        boolean order = (ascendingOrder != null) ? ascendingOrder : ActividadSortService.DEFAULT_ASCENDING_ORDER;

        // Obtener actividades paginadas desde el repositorio
        Page<Actividad> actividades = actividadRepository.findAll(pageable);

        // Convertir actividades a DTOs según el tipo de detalle
        List<ActividadBaseDTO> actividadDTOs = actividades.getContent().stream()
                .map(actividad -> actividadDTOService.convertActividadToDTO(actividad))
                .collect(Collectors.toList());

        // Ordenar las actividades si es necesario
        List<ActividadBaseDTO> sortedDTOs = actividadSortService.sortActivitiesByType(actividadDTOs, order);

        // Crear y retornar un nuevo objeto Page
        return new PageImpl<>(sortedDTOs, pageable, actividades.getTotalElements());
    }

    /**
     * Encuentra una actividad por su ID.
     *
     * @param oid ID de la actividad.
     * @return La actividad encontrada, o null si no existe.
     */
    public Actividad findByOid(Integer oid) {
        return actividadRepository.findById(oid).orElse(null);
    }

    /**
     * Busca una actividad por su ID y la convierte en su correspondiente DTO.
     *
     * @param oid ID de la actividad.
     * @return El DTO correspondiente a la actividad.
     */
    public ActividadBaseDTO findDTOByOid(Integer oid) {
        Actividad actividad = actividadRepository.findById(oid)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró una actividad con el ID: " + oid));
        return actividadDTOService.convertActividadToDTO(actividad);
    }

    /**
     * Guarda una nueva actividad junto con el detalle específico y el proceso
     * asociado en la base de datos.
     *
     * @param actividadDTO DTO que contiene la actividad y su detalle específico.
     * @return La actividad guardada.
     */
    @Transactional
    public Actividad save(ActividadBaseDTO actividadDTO) {
        try {
            Actividad actividad = actividadMapper.convertToEntity(actividadDTO);
            asignarPeriodoAcademicoActivo(actividad);
            if (actividad.getProceso().getNombreProceso() == null || actividad.getProceso().getNombreProceso().isEmpty()) {
                String nombreProceso = "ACTIVIDAD";
                actividad.getProceso().setNombreProceso(nombreProceso);
            }
            actividad.getProceso().setEvaluador(new Usuario(actividadDTO.getOidEvaluador()));
            actividad.getProceso().setEvaluado(new Usuario(actividadDTO.getOidEvaluado()));
            guardarProceso(actividad);

            if (actividad.getNombreActividad() == null || actividad.getNombreActividad().isEmpty()) {
                String nombreActividad = actividadDetalleService.generarNombreActividad(actividadDTO);
                actividad.setNombreActividad(nombreActividad);
            }
            Actividad savedActividad = actividadRepository.save(actividad);

            if (actividadDTO.getDetalle() != null) {
                actividadDetalleService.saveActivityDetail(savedActividad, actividadDTO.getDetalle());
            }
            saveSource(savedActividad);
            return savedActividad;
        } catch (Exception e) {
            throw new RuntimeException("Error al guardar la actividad: " + e.getMessage(), e);
        }
    }

    /**
     * Actualiza una actividad existente en la base de datos.
     *
     * @param idActividad  ID de la actividad a actualizar.
     * @param actividadDTO DTO con los datos actualizados de la actividad.
     * @return La actividad actualizada.
     */
    public Actividad update(Integer idActividad, ActividadBaseDTO actividadDTO) {
        Actividad actividadExistente = actividadRepository.findById(idActividad)
                .orElseThrow(() -> new IllegalArgumentException("Actividad con ID " + idActividad + " no encontrada."));

        actualizarCamposBasicos(actividadExistente, actividadDTO);

        if (actividadDTO.getOidEstadoActividad() != null) {
            asignarEstadoActividad(actividadExistente, actividadDTO.getOidEstadoActividad());
        }

        if (actividadDTO.getDetalle() != null) {
            actividadDetalleService.updateActivityDetail(actividadExistente, actividadDTO.getDetalle());
        }

        return actividadRepository.save(actividadExistente);
    }

    /**
     * Elimina una actividad por su ID.
     *
     * @param oid ID de la actividad a eliminar.
     */
    public void delete(Integer oid) {
        actividadRepository.deleteById(oid);
    }

    /**
     * Asigna el período académico activo a la actividad.
     *
     * @param actividad La actividad a la que se asignará el período.
     */
    private void asignarPeriodoAcademicoActivo(Actividad actividad) {
        Integer idPeriodoAcademico = periodoAcademicoService.obtenerPeriodoAcademicoActivo();
        if (idPeriodoAcademico == null) {
            throw new IllegalStateException("No se encontró un período académico activo.");
        }

        if (actividad.getProceso() == null) {
            actividad.setProceso(new Proceso());
        }

        PeriodoAcademico periodoAcademico = new PeriodoAcademico();
        periodoAcademico.setOidPeriodoAcademico(idPeriodoAcademico);
        actividad.getProceso().setOidPeriodoAcademico(periodoAcademico);
    }

    /**
     * Guarda el proceso de la actividad si es necesario.
     *
     * @param actividad La actividad con su proceso asociado.
     */
    private void guardarProceso(Actividad actividad) {
        if (actividad.getProceso() != null) {
            Proceso savedProceso = procesoRepository.save(actividad.getProceso());
            actividad.setProceso(savedProceso);
        }
    }

    /**
     * Asigna el estado de la actividad si es válido.
     *
     * @param actividad          La actividad a actualizar.
     * @param oidEstadoActividad El ID del estado de actividad.
     */
    private void asignarEstadoActividad(Actividad actividad, Integer oidEstadoActividad) {
        EstadoActividad estadoExistente = estadoActividadRepository.findById(oidEstadoActividad)
                .orElseThrow(() -> new IllegalArgumentException("Estado de actividad no válido."));
        actividad.setEstadoActividad(estadoExistente);
    }

    /**
     * Actualiza los campos básicos de la actividad.
     *
     * @param actividadExistente La actividad existente.
     * @param actividadDTO       DTO con los datos actualizados.
     */
    private void actualizarCamposBasicos(Actividad actividadExistente, ActividadBaseDTO actividadDTO) {
        actividadExistente.setNombreActividad(actividadDTO.getNombreActividad());
        actividadExistente.setHoras(actividadDTO.getHoras());
        actividadExistente.setSemanas(actividadDTO.getSemanas());
        actividadExistente.setInformeEjecutivo(actividadDTO.getInformeEjecutivo());
    }

    private void saveSource(Actividad actividad) {
        // Buscar el estado fuente "PENDIENTE"
        EstadoFuente estadoFuente = estadoFuenteRepository.findByNombreEstado("PENDIENTE")
                .orElseThrow(() -> new IllegalArgumentException("Estado de fuente no válido."));
    
        // Crear y guardar las fuentes
        createAndSaveFuente(actividad, "1", estadoFuente);
        createAndSaveFuente(actividad, "2", estadoFuente);
    }

    /**
     * Método auxiliar para crear y guardar una fuente.
     *
     * @param actividad    La actividad asociada.
     * @param tipoFuente   El tipo de la fuente (1 o 2).
     * @param estadoFuente El estado de la fuente.
     */
    private void createAndSaveFuente(Actividad actividad, String tipoFuente, EstadoFuente estadoFuente) {
        Fuente fuente = new Fuente();
        fuente.setActividad(actividad);
        fuente.setTipoFuente(tipoFuente);
        fuente.setEstadoFuente(estadoFuente);
        fuente.setCalificacion(null);
        fuenteRepository.save(fuente);

    }
}
