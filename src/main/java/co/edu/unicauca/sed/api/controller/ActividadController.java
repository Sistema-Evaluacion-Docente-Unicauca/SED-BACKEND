package co.edu.unicauca.sed.api.controller;

import co.edu.unicauca.sed.api.dto.ActividadDTO;
import co.edu.unicauca.sed.api.dto.ActividadDTOEvaluador;
import co.edu.unicauca.sed.api.model.Actividad;
import co.edu.unicauca.sed.api.service.actividad.ActividadDTOService;
import co.edu.unicauca.sed.api.service.actividad.ActividadQueryService;
import co.edu.unicauca.sed.api.service.actividad.ActividadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

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

    @Autowired
    private ActividadDTOService actividadDTOService;

    @Autowired
    private ActividadQueryService actividadQueryService;

    /**
     * Obtiene todas las actividades del sistema con paginación.
     *
     * @param page           Número de página.
     * @param size           Tamaño de página.
     * @param ascendingOrder Indica si el orden es ascendente.
     * @return Página de actividades.
     */
    @GetMapping("all")
    public ResponseEntity<?> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "true") boolean ascendingOrder) {
        try {
            Page<ActividadDTO> activities = actividadService.findAll(PageRequest.of(page, size), ascendingOrder);
            if (activities.hasContent()) {
                return ResponseEntity.ok().body(activities);
            } else {
                logger.warn("No se encontraron actividades");
                return ResponseEntity.noContent().build();
            }
        } catch (Exception e) {
            logger.error("Error al obtener todas las actividades: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    /**
     * Obtiene todas las actividades de períodos académicos activos con paginación.
     *
     * @param page           Número de página.
     * @param size           Tamaño de página.
     * @param ascendingOrder Indica si el orden es ascendente.
     * @return Página de actividades o un mensaje de error.
     */
    @GetMapping("buscarActividadesPorPeriodoActivo")
    public ResponseEntity<?> findAllInActivePeriods(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "true") boolean ascendingOrder) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<ActividadDTO> activities = actividadService.findAllInActivePeriods(pageable, ascendingOrder);

            if (activities.hasContent()) {
                return ResponseEntity.ok(activities);
            } else {
                logger.warn("No se encontraron actividades en períodos activos");
                return ResponseEntity.noContent().build();
            }
        } catch (Exception e) {
            logger.error("Error al obtener actividades en períodos activos: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
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
                ActividadDTO actividadDTO = actividadDTOService.convertToDTO(actividad);
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
     * Busca actividades asignadas a un evaluado en períodos activos con paginación.
     *
     * @param page            Número de página.
     * @param size            Tamaño de página.
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
     * @return Página de actividades.
     */
    @GetMapping("/buscarActividadesPorEvaluado")
    public ResponseEntity<?> listActivitiesByEvaluadoInActivePeriod(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
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
            Page<ActividadDTO> activities = actividadQueryService.findActivitiesByEvaluado(
                    idEvaluador, idEvaluado, codigoActividad, tipoActividad, nombreEvaluador,
                    roles, tipoFuente, estadoFuente, orden, estadoPeriodo,
                    PageRequest.of(page, size));
            if (activities.hasContent()) {
                return ResponseEntity.ok(activities);
            } else {
                logger.warn("No se encontraron actividades para los parámetros proporcionados");
                return ResponseEntity.noContent().build();
            }
        } catch (Exception e) {
            logger.error("Error al buscar actividades por evaluado: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    /**
     * Busca actividades asignadas a un evaluador en períodos activos con paginación.
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
     * @param page            Número de la página.
     * @param size            Tamaño de la página.
     * @return Página de actividades o un mensaje de error.
     */
    @GetMapping("/buscarActividadesPorEvaluador")
    public ResponseEntity<?> listActivitiesByEvaluadorInActivePeriod(
            @RequestParam(required = false) Integer idEvaluador,
            @RequestParam(required = false) Integer idEvaluado,
            @RequestParam(required = false) String tipoActividad,
            @RequestParam(required = false) String codigoActividad,
            @RequestParam(required = false) String nombreEvaluador,
            @RequestParam(required = false) List<String> roles,
            @RequestParam(required = false) String tipoFuente,
            @RequestParam(required = false) String estadoFuente,
            @RequestParam(required = false) Boolean orden,
            @RequestParam(required = false) Boolean estadoPeriodo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size, orden != null && orden
                    ? Sort.by("nombre").ascending()
                    : Sort.by("nombre").descending());

            Page<ActividadDTOEvaluador> activities = actividadQueryService.findActivitiesByEvaluador(
                    idEvaluador, idEvaluado, codigoActividad, tipoActividad, nombreEvaluador, roles,
                    tipoFuente, estadoFuente, orden, estadoPeriodo, pageable);

            if (activities.hasContent()) {
                return ResponseEntity.ok(activities);
            } else {
                logger.warn("No se encontraron actividades para los parámetros proporcionados");
                return ResponseEntity.noContent().build();
            }
        } catch (Exception e) {
            logger.error("Error al buscar actividades por evaluador: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
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
        if (actividad.getProceso() == null) {
            return ResponseEntity.badRequest().body("El proceso no puede ser nulo.");
        }
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
     * Actualiza una actividad existente en el sistema.
     *
     * @param idActividad ID de la actividad a actualizar.
     * @param actividad   Datos actualizados de la actividad.
     * @return Actividad actualizada o mensaje de error.
     */
    @PutMapping("update/{idActividad}")
    public ResponseEntity<?> update(@PathVariable Integer idActividad, @RequestBody Actividad actividad) {
        logger.info("Intentando actualizar actividad con ID: {}", idActividad);
        try {
            // Delegar la lógica de actualización al servicio
            Actividad actividadActualizada = actividadService.update(idActividad, actividad);
            logger.info("Actividad actualizada exitosamente con ID: {}", actividadActualizada.getOidActividad());
            return ResponseEntity.ok(actividadActualizada);
        } catch (IllegalArgumentException e) {
            logger.warn("No se encontró la actividad con ID: {}", idActividad);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error al actualizar actividad con ID {}: {}", idActividad, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualizar la actividad.");
        }
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
            actividadService.delete(oid);
        } catch (Exception e) {
            logger.error("Error al eliminar actividad con ID {}: {}", oid, e.getMessage(), e);
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.CONFLICT).body("No se puede borrar por conflictos con otros datos");
        }
        return ResponseEntity.ok().build();
    }
}
