package co.edu.unicauca.sed.api.controller;

import co.edu.unicauca.sed.api.domain.Actividad;
import co.edu.unicauca.sed.api.dto.ApiResponse;
import co.edu.unicauca.sed.api.dto.actividad.*;
import co.edu.unicauca.sed.api.service.actividad.ActividadQueryService;
import co.edu.unicauca.sed.api.service.actividad.ActividadService;
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
    public ResponseEntity<ApiResponse<Page<ActividadBaseDTO>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "true") boolean ascendingOrder) {
        logger.info("🔵 [FIND_ALL] Buscando actividades con paginación: page={}, size={}", page, size);
        
        ApiResponse<Page<ActividadBaseDTO>> response = actividadService.findAll(PageRequest.of(page, size), ascendingOrder);
        return ResponseEntity.status(response.getCodigo()).body(response);
    }

    /**
     * Busca una actividad por su ID.
     */
    @GetMapping("/{oid}")
    public ResponseEntity<ApiResponse<ActividadBaseDTO>> findById(@PathVariable Integer oid) {
        logger.info("🔵 [FIND_BY_ID] Buscando actividad con ID: {}", oid);
        ApiResponse<ActividadBaseDTO> response = actividadService.findDTOByOid(oid);
        return ResponseEntity.status(response.getCodigo()).body(response);
    }

    /**
     * Busca actividades asignadas a un evaluado en períodos activos con paginación.
     */
    @GetMapping("/buscarActividadesPorEvaluado")
    public ResponseEntity<ApiResponse<Page<ActividadBaseDTO>>> buscarActividadesPorEvaluado(
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

        ApiResponse<Page<ActividadBaseDTO>> response = actividadQueryService.buscarActividadesPorEvaluado(
                idEvaluador, idEvaluado, codigoActividad, tipoActividad, nombreEvaluador,
                roles, tipoFuente, estadoFuente, orden, idPeriodo, PageRequest.of(page, size));

        return ResponseEntity.status(response.getCodigo()).body(response);
    }

    /**
     * Busca actividades asignadas a un evaluador en períodos activos con
     * paginación.
     */
    @GetMapping("/buscarActividadesPorEvaluador")
    public ResponseEntity<ApiResponse<Page<ActividadDTOEvaluador>>> buscarActividadesPorEvaluador(
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
        ApiResponse<Page<ActividadDTOEvaluador>> response = actividadQueryService.buscarActividadesPorEvaluador(
                idEvaluador, idEvaluado, codigoActividad, tipoActividad, nombreEvaluador, roles,
                tipoFuente, estadoFuente, orden, idPeriodo, PageRequest.of(page, size));

        return ResponseEntity.status(response.getCodigo()).body(response);
    }

    /**
     * Guarda una nueva actividad.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Actividad>> save(@RequestBody ActividadBaseDTO actividadDTO) {
        ApiResponse<Actividad> response = actividadService.save(actividadDTO);
        return ResponseEntity.status(response.getCodigo()).body(response);
    }
    
    

    /**
     * Actualiza una actividad existente.
     */
    @PutMapping("/{idActividad}")
    public ResponseEntity<ApiResponse<Actividad>> update(@PathVariable Integer idActividad, @RequestBody ActividadBaseDTO actividadDTO) {
        logger.info("🔵 [UPDATE] Iniciando actualización de actividad con ID: {}", idActividad);
        ApiResponse<Actividad> response = actividadService.update(idActividad, actividadDTO);
        return ResponseEntity.status(response.getCodigo()).body(response);
    }

    /**
     * Elimina una actividad por su ID.
     */
    @DeleteMapping("/{oid}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Integer oid) {
        ApiResponse<Void> response = actividadService.delete(oid);
        return ResponseEntity.status(response.getCodigo()).body(response);
    }
}
