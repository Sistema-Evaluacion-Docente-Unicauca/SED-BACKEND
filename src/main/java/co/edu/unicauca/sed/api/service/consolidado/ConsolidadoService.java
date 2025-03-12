package co.edu.unicauca.sed.api.service.consolidado;

import co.edu.unicauca.sed.api.domain.Consolidado;
import co.edu.unicauca.sed.api.dto.ApiResponse;
import co.edu.unicauca.sed.api.dto.ConsolidadoArchivoDTO;
import co.edu.unicauca.sed.api.dto.ConsolidadoDTO;
import co.edu.unicauca.sed.api.dto.InformacionConsolidadoDTO;
import co.edu.unicauca.sed.api.dto.actividad.ActividadPaginadaDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Interfaz para definir los métodos del servicio de Consolidado.
 */
public interface ConsolidadoService {

    /**
     * Obtiene una lista paginada de consolidados según los filtros aplicados.
     *
     * @param pageable           Configuración de paginación.
     * @param ascendingOrder     Orden ascendente o descendente.
     * @param idPeriodoAcademico ID del periodo académico.
     * @param idUsuario          ID del usuario evaluado.
     * @param nombre             Nombre del evaluado.
     * @param identificacion     Identificación del evaluado.
     * @param facultad           Facultad del evaluado.
     * @param departamento       Departamento del evaluado.
     * @param categoria          Categoría del evaluado.
     * @return ApiResponse con la lista de consolidados filtrados.
     */
    ApiResponse<Page<InformacionConsolidadoDTO>> findAll(Pageable pageable, Boolean ascendingOrder,
            Integer idPeriodoAcademico, Integer idUsuario, String nombre, String identificacion,
            String facultad, String departamento, String categoria);

    /**
     * Busca un consolidado por su identificador único (OID).
     *
     * @param oid Identificador del consolidado.
     * @return ApiResponse con el consolidado encontrado.
     */
    ApiResponse<Consolidado> findByOid(Integer oid);

    /**
     * Actualiza los datos de un consolidado y todos los procesos asociados.
     *
     * @param oidConsolidado  ID del consolidado base.
     * @param datosActualizar Datos a actualizar en todos los procesos relacionados.
     * @return ApiResponse indicando el resultado de la operación.
     */
    ApiResponse<Void> updateAllFromConsolidado(Integer oidConsolidado, Consolidado datosActualizar);

    public ApiResponse<ConsolidadoDTO> generarInformacionGeneral(Integer idEvaluado, Integer idPeriodoAcademico);

    public ApiResponse<ActividadPaginadaDTO> filtrarActividadesPaginadas(Integer idEvaluado, Integer idPeriodoAcademico,
            String nombreActividad, String idTipoActividad,
            String idTipoFuente, String idEstadoFuente,
            Pageable pageable);

    public ApiResponse<ConsolidadoArchivoDTO> aprobarConsolidado(Integer idEvaluado, Integer idEvaluador, Integer idPeriodoAcademico, String nota);
    /**
     * Elimina un consolidado por su ID.
     *
     * @param oid Identificador del consolidado a eliminar.
     * @return ApiResponse indicando el resultado de la eliminación.
     */
    ApiResponse<Void> delete(Integer oid);
}
