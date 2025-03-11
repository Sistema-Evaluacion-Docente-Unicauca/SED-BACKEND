package co.edu.unicauca.sed.api.service.fuente;

import co.edu.unicauca.sed.api.domain.Actividad;
import co.edu.unicauca.sed.api.domain.Fuente;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * Interfaz para el manejo de fuentes y sus archivos asociados.
 */
public interface FuenteService {

    /**
     * Recupera todas las fuentes desde el repositorio con soporte de paginación.
     *
     * @param pageable Parámetro para definir la paginación (número de página y
     *                 tamaño de página).
     * @return Página de entidades Fuente.
     */
    Page<Fuente> obtenerTodos(Pageable pageable);

    /**
     * Busca una fuente por su identificador único.
     *
     * @param oid El ID de la fuente a buscar.
     * @return La entidad Fuente si se encuentra, null en caso contrario.
     */
    Fuente buscarPorId(Integer oid);

    /**
     * Busca todas las fuentes asociadas a una actividad específica.
     *
     * @param oidActividad El ID de la actividad.
     * @return Lista de entidades Fuente vinculadas a la actividad.
     */
    List<Fuente> buscarPorActividadId(Integer oidActividad);

    /**
     * Guarda una fuente.
     *
     * @param fuente La entidad Fuente a guardar.
     * @return La entidad Fuente guardada.
     */
    Fuente guardar(Fuente fuente);

    /**
     * Elimina una fuente por su identificador único.
     *
     * @param oid El ID de la fuente a eliminar.
     */
    void eliminar(Integer oid);

    /**
     * Guarda múltiples fuentes junto con sus archivos asociados.
     *
     * @param fuentesJson   JSON que contiene los datos de las fuentes.
     * @param informeFuente Archivo común asociado a las fuentes.
     * @param observacion   Observación general.
     * @param archivos      Archivos adicionales para manejar.
     */
    void guardarFuente(String fuentesJson, MultipartFile informeFuente, String observacion,
            Map<String, MultipartFile> archivos);

    /**
     * Recupera un archivo asociado a una fuente.
     *
     * @param id        ID de la fuente.
     * @param esInforme Indica si se debe recuperar el informe (true) o la fuente.
     * @return Respuesta con el archivo como recurso descargable.
     */
    ResponseEntity<?> obtenerArchivo(Integer id, boolean esInforme);

    /**
     * Crea y guarda fuentes para una actividad específica con estado pendiente.
     *
     * @param actividad Actividad para la cual se crearán las fuentes.
     */
    void guardarFuente(Actividad actividad);
}
