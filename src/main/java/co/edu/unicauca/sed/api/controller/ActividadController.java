package co.edu.unicauca.sed.api.controller;

import co.edu.unicauca.sed.api.dto.ActividadDTO;
import co.edu.unicauca.sed.api.dto.ActividadDTOEvaluador;
import co.edu.unicauca.sed.api.model.Actividad;
import co.edu.unicauca.sed.api.service.ActividadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Controlador para gestionar las actividades del sistema.
 */
@Controller
@RequestMapping("actividad")
public class ActividadController {

    private static final Logger logger = LoggerFactory.getLogger(ActividadController.class);

    @Autowired
    private ActividadService actividadService;

    /**
     * Obtiene todas las actividades del sistema.
     *
     * @param ascendingOrder Indica si el orden es ascendente.
     * @return Lista de actividades o un mensaje de error.
     */
    @GetMapping("all")
    public ResponseEntity<?> findAll(@RequestParam(defaultValue = "true") boolean ascendingOrder) {
        try {
            List<ActividadDTO> list = actividadService.findAll(ascendingOrder);

            if (list != null && !list.isEmpty()) {
                return ResponseEntity.ok().body(list);
            }
        } catch (Exception e) {
            logger.error("Error al obtener todas las actividades: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
        logger.warn("No se encontraron actividades");
        return ResponseEntity.notFound().build();
    }

    /**
     * Obtiene todas las actividades de períodos académicos activos.
     *
     * @param ascendingOrder Indica si el orden es ascendente.
     * @return Lista de actividades o un mensaje de error.
     */
    @GetMapping("findAllInActivePeriods")
    public ResponseEntity<?> findAllInActivePeriods(@RequestParam(defaultValue = "true") boolean ascendingOrder) {
        try {
            List<ActividadDTO> list = actividadService.findAllInActivePeriods(ascendingOrder);

            if (list != null && !list.isEmpty()) {
                return ResponseEntity.ok().body(list);
            }
        } catch (Exception e) {
            logger.error("Error al obtener actividades en períodos activos: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
        logger.warn("No se encontraron actividades en períodos activos");
        return ResponseEntity.notFound().build();
    }

    /**
     * Busca una actividad por su ID.
     *
     * @param oid ID de la actividad.
     * @return Actividad encontrada o un mensaje de error.
     */
    @GetMapping("find/{oid}")
    public ResponseEntity<?> findById(@PathVariable Integer oid) {
        try {
            Actividad actividad = actividadService.findByOid(oid);
            if (actividad != null) {
                ActividadDTO actividadDTO = actividadService.convertToDTO(actividad);
                return ResponseEntity.ok().body(actividadDTO);
            }
        } catch (Exception e) {
            logger.error("Error al buscar actividad con ID {}: {}", oid, e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
        logger.warn("Actividad con ID: {} no encontrada", oid);
        return ResponseEntity.notFound().build();
    }

    /**
     * Busca actividades asignadas a un evaluado en períodos activos.
     *
     * @param idEvaluador     ID del evaluador (opcional).
     * @param idEvaluado      ID del evaluado (opcional).
     * @param tipoActividad   Tipo de actividad (opcional).
     * @param codigoActividad Código de actividad (opcional).
     * @param nombreEvaluador Nombre del evaluador (opcional).
     * @param roles           Roles asociados (opcional).
     * @param tipoFuente      Tipo de fuente (opcional).
     * @param estadoFuente    Estado de la fuente (opcional).
     * @param orden           Orden ascendente o descendente (opcional).
     * @param estadoPeriodo   Estado del período (opcional).
     * @return Lista de actividades o un mensaje de error.
     */
    @GetMapping("/findActivitiesByEvaluado")
    public ResponseEntity<List<ActividadDTO>> listActivitiesByEvaluadoInActivePeriod(
            @RequestParam(required = false) Integer idEvaluador,
            @RequestParam(required = false) Integer idEvaluado,
            @RequestParam(required = false) String tipoActividad,
            @RequestParam(required = false) String codigoActividad,
            @RequestParam(required = false) String nombreEvaluador,
            @RequestParam(required = false) List<String> roles,
            @RequestParam(required = false) String tipoFuente,
            @RequestParam(required = false) String estadoFuente,
            @RequestParam(required = false) Boolean orden,
            @RequestParam(required = false) Boolean estadoPeriodo) {
        try {
            List<ActividadDTO> activities = actividadService.findActivitiesByEvaluado(idEvaluador, idEvaluado,
                    codigoActividad, tipoActividad, nombreEvaluador, roles, tipoFuente, estadoFuente, orden,
                    estadoPeriodo);
            if (activities.isEmpty()) {
                logger.warn("No se encontraron actividades para los parámetros proporcionados");
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(activities);
        } catch (Exception e) {
            logger.error("Error al buscar actividades por evaluado: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(null);
        }
    }

    /**
     * Busca actividades asignadas a un evaluador en períodos activos.
     *
     * @param idEvaluador     ID del evaluador (opcional).
     * @param idEvaluado      ID del evaluado (opcional).
     * @param tipoActividad   Tipo de actividad (opcional).
     * @param codigoActividad Código de actividad (opcional).
     * @param nombreEvaluador Nombre del evaluador (opcional).
     * @param roles           Roles asociados (opcional).
     * @param tipoFuente      Tipo de fuente (opcional).
     * @param estadoFuente    Estado de la fuente (opcional).
     * @param orden           Orden ascendente o descendente (opcional).
     * @param estadoPeriodo   Estado del período (opcional).
     * @return Lista de actividades o un mensaje de error.
     */
    @GetMapping("/findActivitiesByEvaluador")
    public ResponseEntity<List<ActividadDTOEvaluador>> listActivitiesByEvaluadorInActivePeriod(
            @RequestParam(required = false) Integer idEvaluador,
            @RequestParam(required = false) Integer idEvaluado,
            @RequestParam(required = false) String tipoActividad,
            @RequestParam(required = false) String codigoActividad,
            @RequestParam(required = false) String nombreEvaluador,
            @RequestParam(required = false) List<String> roles,
            @RequestParam(required = false) String tipoFuente,
            @RequestParam(required = false) String estadoFuente,
            @RequestParam(required = false) Boolean orden,
            @RequestParam(required = false) Boolean estadoPeriodo) {
        try {
            List<ActividadDTOEvaluador> activities = actividadService.findActivitiesByEvaluador(idEvaluador, idEvaluado, codigoActividad, tipoActividad, nombreEvaluador, roles, tipoFuente, estadoFuente, orden, estadoPeriodo);
            if (activities.isEmpty()) {
                return ResponseEntity.noContent().build(); // Returns 204 if no activities are found
            }
            return ResponseEntity.ok(activities);
        } catch (Exception e) {
            logger.error("Error al buscar actividades por evaluador: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(null);
        }
    }

    /**
     * Guarda una nueva actividad en el sistema.
     *
     * @param actividad Datos de la actividad.
     * @return Actividad guardada o un mensaje de error.
     */
    @PostMapping("save")
    public ResponseEntity<?> save(@RequestBody Actividad actividad) {
        logger.info("Intentando guardar actividad: {}", actividad);
        try {
            Actividad resultado = actividadService.save(actividad);
            if (resultado != null) {
                logger.info("Actividad guardada exitosamente con ID: {}", resultado.getOidActividad());
                return ResponseEntity.ok().body(resultado);
            }
        } catch (Exception e) {
            logger.error("Error al guardar actividad: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
        logger.warn("Resultado nulo al guardar actividad");
        return ResponseEntity.internalServerError().body("Error: Resultado nulo");
    }

    /**
     * Elimina una actividad por su ID.
     *
     * @param oid ID de la actividad a eliminar.
     * @return Respuesta de éxito o mensaje de error.
     */
    @DeleteMapping("delete/{oid}")
    public ResponseEntity<?> delete(@PathVariable Integer oid) {
        Actividad actividad = null;
        try {
            actividad = actividadService.findByOid(oid);
            if (actividad == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Actividad no encontrada"); // 404 if not found
            }
        } catch (Exception e) {
            logger.error("Error al eliminar actividad con ID {}: {}", oid, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Actividad no encontrada");
        }

        try {
            actividadService.delete(oid); // Tries to delete the activity
        } catch (Exception e) {
            logger.error("Error al eliminar actividad con ID {}: {}", oid, e.getMessage(), e);
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.CONFLICT).body("No se puede borrar por conflictos con otros datos");
        }
        return ResponseEntity.ok().build();
    }
}
