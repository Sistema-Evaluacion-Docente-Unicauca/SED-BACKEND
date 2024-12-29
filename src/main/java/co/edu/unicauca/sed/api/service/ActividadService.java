package co.edu.unicauca.sed.api.service;

import co.edu.unicauca.sed.api.dto.ActividadDTO;
import co.edu.unicauca.sed.api.model.Actividad;
import co.edu.unicauca.sed.api.repository.ActividadRepository;
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
    private ActividadDTOService actividadDTOService;

    @Autowired
    private ActividadSortService actividadSortService;

    // Constante para el estado de períodos activos
    private static final int ACTIVE_PERIOD_STATUS = 1;

    // Orden de clasificación predeterminado
    private static final boolean DEFAULT_ASCENDING_ORDER = true;

    /**
     * Recupera todas las actividades junto con sus fuentes asociadas con
     * paginación.
     *
     * @param pageable       Parámetros de paginación.
     * @param ascendingOrder Indica si las actividades deben ordenarse de forma
     *                       ascendente.
     * @return Página de actividades en formato DTO.
     */
    public Page<ActividadDTO> findAll(Pageable pageable, Boolean ascendingOrder) {
        boolean order = (ascendingOrder != null) ? ascendingOrder : DEFAULT_ASCENDING_ORDER;

        // Obtener actividades paginadas desde el repositorio
        Page<Actividad> actividades = actividadRepository.findAll(pageable);

        // Convertir las actividades a DTOs
        List<ActividadDTO> actividadDTOs = actividades.getContent().stream()
                .map(actividadDTOService::convertToDTO)
                .collect(Collectors.toList());

        // Ordenar actividades
        List<ActividadDTO> sortedDTOs = actividadSortService.sortActivities(actividadDTOs, order);

        // Crear y retornar un nuevo objeto Page
        return new PageImpl<>(sortedDTOs, pageable, actividades.getTotalElements());
    }

    /**
     * Recupera todas las actividades que forman parte de períodos académicos
     * activos con paginación.
     *
     * @param pageable       Parámetros de paginación.
     * @param ascendingOrder Indica si las actividades deben ordenarse de forma
     *                       ascendente.
     * @return Página de actividades en formato DTO en períodos activos, ordenadas
     *         según el parámetro.
     */
    public Page<ActividadDTO> findAllInActivePeriods(Pageable pageable, Boolean ascendingOrder) {
        boolean order = (ascendingOrder != null) ? ascendingOrder : DEFAULT_ASCENDING_ORDER;

        // Consultar todas las actividades en períodos académicos activos
        List<Actividad> actividades = actividadRepository
                .findByProceso_OidPeriodoAcademico_Estado(ACTIVE_PERIOD_STATUS);

        // Convertir las actividades en DTOs
        List<ActividadDTO> actividadDTOs = actividades.stream()
                .map(actividad -> actividadDTOService.convertToDTO(actividad))
                .collect(Collectors.toList());

        // Ordenar las actividades
        List<ActividadDTO> sortedDTOs = actividadSortService.sortActivities(actividadDTOs, order);

        // Paginar manualmente la lista de actividades ordenadas
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), sortedDTOs.size());

        List<ActividadDTO> paginatedDTOs = (start > end) ? List.of() : sortedDTOs.subList(start, end);

        // Crear y retornar un objeto Page con los datos paginados
        return new PageImpl<>(paginatedDTOs, pageable, sortedDTOs.size());
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
     * Guarda una nueva actividad en la base de datos.
     *
     * @param actividad La actividad a guardar.
     * @return La actividad guardada.
     */
    public Actividad save(Actividad actividad) {
        return actividadRepository.save(actividad);
    }

    /**
     * Actualiza una actividad existente en la base de datos.
     *
     * @param idActividad ID de la actividad a actualizar.
     * @param actividad   Datos actualizados de la actividad.
     * @return La actividad actualizada.
     * @throws IllegalArgumentException Si no se encuentra la actividad con el ID
     *                                  proporcionado.
     */
    public Actividad update(Integer idActividad, Actividad actividad) {
        Actividad actividadExistente = actividadRepository.findById(idActividad)
                .orElseThrow(() -> new IllegalArgumentException("Actividad con ID " + idActividad + " no encontrada."));

        // Actualizar los campos de la actividad existente
        actividadExistente.setCodigoActividad(actividad.getCodigoActividad());
        actividadExistente.setNombre(actividad.getNombre());
        actividadExistente.setHoras(actividad.getHoras());
        actividadExistente.setInformeEjecutivo(actividad.getInformeEjecutivo());
        actividadExistente.setTipoActividad(actividad.getTipoActividad());
        actividadExistente.setProceso(actividad.getProceso());

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
}
