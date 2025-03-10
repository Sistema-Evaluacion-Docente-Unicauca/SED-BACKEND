package co.edu.unicauca.sed.api.service.actividad;

import co.edu.unicauca.sed.api.domain.Actividad;
import co.edu.unicauca.sed.api.dto.ApiResponse;
import co.edu.unicauca.sed.api.dto.actividad.ActividadBaseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Interface para definir los métodos del servicio de actividad.
 */
public interface ActividadService {

    /**
     * Obtiene todas las actividades paginadas con opción de orden ascendente o descendente.
     *
     * @param pageable       Paginación y ordenamiento.
     * @param ascendingOrder Define si el orden es ascendente o descendente.
     * @return ApiResponse con la lista paginada de actividades.
     */
    ApiResponse<Page<ActividadBaseDTO>> findAll(Pageable pageable, Boolean ascendingOrder);

    /**
     * Busca una actividad por su ID.
     *
     * @param oid Identificador único de la actividad.
     * @return La actividad encontrada o null si no existe.
     */
    Actividad findByOid(Integer oid);

    /**
     * Busca una actividad y devuelve su DTO.
     *
     * @param oid Identificador de la actividad.
     * @return ApiResponse con la actividad en formato DTO.
     */
    ApiResponse<ActividadBaseDTO> findDTOByOid(Integer oid);

    /**
     * Guarda una nueva actividad en la base de datos.
     *
     * @param actividadDTO Datos de la actividad a guardar.
     * @return ApiResponse con la actividad guardada.
     */
    ApiResponse<Actividad> save(ActividadBaseDTO actividadDTO);

    /**
     * Actualiza una actividad existente en la base de datos.
     *
     * @param idActividad  ID de la actividad a actualizar.
     * @param actividadDTO Datos actualizados de la actividad.
     * @return ApiResponse con la actividad actualizada.
     */
    ApiResponse<Actividad> update(Integer idActividad, ActividadBaseDTO actividadDTO);

    /**
     * Elimina una actividad por su ID.
     *
     * @param oid ID de la actividad a eliminar.
     * @return ApiResponse con el resultado de la eliminación.
     */
    ApiResponse<Void> delete(Integer oid);
}
