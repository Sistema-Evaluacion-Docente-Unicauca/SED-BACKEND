package co.edu.unicauca.sed.api.controller;

import co.edu.unicauca.sed.api.dto.ApiResponse;
import co.edu.unicauca.sed.api.dto.actividad.*;
import co.edu.unicauca.sed.api.model.Actividad;
import co.edu.unicauca.sed.api.service.actividad.ActividadQueryService;
import co.edu.unicauca.sed.api.service.actividad.ActividadService;
import jakarta.persistence.EntityNotFoundException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import java.util.List;

/**
 * Controlador para gestionar las actividades del sistema.
 */
@RestController
@RequestMapping("api/actividades")
public class ActividadController {

    private static final Logger logger = LoggerFactory.getLogger(ActividadController.class);

    private final ActividadService actividadService;
    private final ActividadQueryService actividadQueryService;

    public ActividadController(ActividadService actividadService, ActividadQueryService actividadQueryService) {
        this.actividadService = actividadService;
        this.actividadQueryService = actividadQueryService;
    }

    /**
     * Obtiene todas las actividades con paginación.
     */
    @GetMapping
    public ResponseEntity<Page<?>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "true") boolean ascendingOrder) {
        logger.info("🔵 [FIND_ALL] Buscando actividades con paginación: page={}, size={}", page, size);
        Page<ActividadBaseDTO> activities = actividadService.findAll(PageRequest.of(page, size), ascendingOrder);
        logger.info("✅ [FIND_ALL] Se encontraron {} actividades.", activities.getTotalElements());
        return ResponseEntity.ok(activities);
    }

    /**
     * Busca una actividad por su ID.
     */
    @GetMapping("/{oid}")
    public ResponseEntity<ApiResponse<ActividadBaseDTO>> findById(@PathVariable Integer oid) {
        logger.info("🔵 [FIND_BY_ID] Buscando actividad con ID: {}", oid);
        try {
            ActividadBaseDTO actividadDTO = actividadService.findDTOByOid(oid);
            logger.info("✅ [FIND_BY_ID] Actividad encontrada con ID: {}", oid);
            ApiResponse<ActividadBaseDTO> response = new ApiResponse<>(200, "Actividad encontrada.", actividadDTO);
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            logger.warn("⚠️ [FIND_BY_ID] Actividad con ID {} no encontrada.", oid);
            ApiResponse<ActividadBaseDTO> errorResponse = new ApiResponse<>(404,
                    "Actividad con ID " + oid + " no encontrada.", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception e) {
            logger.error("❌ [ERROR] Error al buscar actividad con ID {}: {}", oid, e.getMessage(), e);
            ApiResponse<ActividadBaseDTO> errorResponse = new ApiResponse<>(500,
                    "Error interno al buscar la actividad.", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Busca actividades asignadas a un evaluado en períodos activos con paginación.
     */
    @GetMapping("/buscarActividadesPorEvaluado")
    public ResponseEntity<Page<?>> buscarActividadesPorEvaluado(
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
            @RequestParam(required = false) Integer idPeriodo) {
        Page<ActividadBaseDTO> activities = actividadQueryService.findActivitiesByEvaluado(
                idEvaluador, idEvaluado, codigoActividad, tipoActividad, nombreEvaluador,
                roles, tipoFuente, estadoFuente, orden, idPeriodo, PageRequest.of(page, size));
        return ResponseEntity.ok(activities);
    }

    /**
     * Busca actividades asignadas a un evaluador en períodos activos con
     * paginación.
     */
    @GetMapping("/buscarActividadesPorEvaluador")
    public ResponseEntity<Page<?>> buscarActividadesPorEvaluador(
            @RequestParam(required = false) Integer idEvaluador,
            @RequestParam(required = false) Integer idEvaluado,
            @RequestParam(required = false) String tipoActividad,
            @RequestParam(required = false) String codigoActividad,
            @RequestParam(required = false) String nombreEvaluador,
            @RequestParam(required = false) List<String> roles,
            @RequestParam(required = false) String tipoFuente,
            @RequestParam(required = false) String estadoFuente,
            @RequestParam(required = false) Boolean orden,
            @RequestParam(required = false) Integer idPeriodo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<ActividadDTOEvaluador> activities = actividadQueryService.findActivitiesByEvaluador(
                idEvaluador, idEvaluado, codigoActividad, tipoActividad, nombreEvaluador, roles,
                tipoFuente, estadoFuente, orden, idPeriodo, PageRequest.of(page, size));
        return ResponseEntity.ok(activities);
    }

    /**
     * Guarda una nueva actividad.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<?>> save(@RequestBody ActividadBaseDTO actividadDTO) {
        try {
            Actividad resultado = actividadService.save(actividadDTO);
            logger.info("✅ [SAVE] Actividad guardada exitosamente con ID: {}", resultado.getOidActividad());

            ApiResponse<Actividad> response = new ApiResponse<>(201, "Actividad guardada exitosamente.", resultado);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            logger.error("❌ [ERROR] Error al guardar actividad: {}", e.getMessage(), e);

            ApiResponse<Void> errorResponse = new ApiResponse<>(500, "Error al guardar la actividad.", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Actualiza una actividad existente.
     */
    @PutMapping("/{idActividad}")
    public ResponseEntity<?> update(@PathVariable Integer idActividad, @RequestBody ActividadBaseDTO actividadDTO) {
        logger.info("🔵 [UPDATE] Iniciando actualización de actividad con ID: {}", idActividad);

        try {
            Object updatedActividad = actividadService.update(idActividad, actividadDTO);
            logger.info("✅ [UPDATE] Actividad actualizada correctamente con ID: {}", idActividad);

            ApiResponse<Object> response = new ApiResponse<>(200, "Actividad actualizada correctamente.",
                    updatedActividad);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("❌ [ERROR] Error al actualizar actividad con ID {}: {}", idActividad, e.getMessage(), e);

            ApiResponse<Object> errorResponse = new ApiResponse<>(500, "Error al actualizar la actividad.", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Elimina una actividad por su ID.
     */
    @DeleteMapping("/{oid}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Integer oid) {
        actividadService.delete(oid);
        ApiResponse<Void> response = new ApiResponse<>(200, "Actividad eliminada correctamente.", null);
        return ResponseEntity.ok(response);
    }
}
