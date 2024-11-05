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

import java.util.List;

@Controller
@RequestMapping("actividad")
public class ActividadController {

    @Autowired
    private ActividadService actividadService;

    /**
     * Retrieves all activities along with their associated sources.
     * Returns a list of activities as DTOs.
     */
    @GetMapping("all")
    public ResponseEntity<?> findAll(@RequestParam(defaultValue = "true") boolean ascendingOrder) {
        try {
            List<ActividadDTO> list = actividadService.findAll(ascendingOrder);

            if (list != null && !list.isEmpty()) {
                return ResponseEntity.ok().body(list); // Returns the list of activities (DTOs)
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getStackTrace());
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Endpoint to retrieve activities with optional filters.
     *
     * This endpoint provides a filtered list of activities based on optional
     * parameters.
     * It returns a list of activities in DTO format, including fields for
     * associated sources (fuentes) and evaluator details.
     * 
     * @param tipoActividad   Optional filter for the activity type.
     * @param nombreEvaluador Optional filter for the evaluator's name; allows
     *                        partial matching.
     * @param roles           Optional filter for evaluator roles; allows filtering
     *                        by specific roles.
     * @param tipoFuente      Optional filter for the type of source in associated
     *                        sources.
     * @param estadoFuente    Optional filter for the state of the source in
     *                        associated sources.
     * @param ascendingOrder  Optional parameter to specify sorting order: true for
     *                        ascending, false for descending. Default is true.
     * 
     * @return ResponseEntity containing a list of filtered activities in DTO
     *         format.
     */
    @GetMapping("/actividades")
    public ResponseEntity<List<ActividadDTO>> getFilteredActivities(
            @RequestParam(required = false) Integer idUsuario,
            @RequestParam(required = false) String tipoActividad,
            @RequestParam(required = false) String nombreEvaluador,
            @RequestParam(required = false) List<String> roles,
            @RequestParam(required = false) String tipoFuente,
            @RequestParam(required = false) String estadoFuente,
            @RequestParam(defaultValue = "true") Boolean ascendingOrder) {

        List<ActividadDTO> actividades = actividadService.findActivitiesWithFilters(idUsuario, tipoActividad, nombreEvaluador, roles, tipoFuente, estadoFuente, ascendingOrder);

        return ResponseEntity.ok(actividades);
    }

    /**
     * Retrieves all activities associated with active academic periods.
     * Returns the activities as DTOs.
     */
    @GetMapping("findAllInActivePeriods")
    public ResponseEntity<?> findAllInActivePeriods(@RequestParam(defaultValue = "true") boolean ascendingOrder) {
        try {
            List<ActividadDTO> list = actividadService.findAllInActivePeriods(ascendingOrder);

            if (list != null && !list.isEmpty()) {
                return ResponseEntity.ok().body(list); // Return the list of activities in active periods
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getStackTrace());
        }
        return ResponseEntity.notFound().build(); // Return 404 if no activities found
    }

    /**
     * Finds an activity by its ID and returns it as a DTO.
     * If the activity is found, returns the DTO, otherwise returns 404.
     */
    @GetMapping("find/{oid}")
    public ResponseEntity<?> findById(@PathVariable Integer oid) {
        Actividad actividad = actividadService.findByOid(oid);
        if (actividad != null) {
            ActividadDTO actividadDTO = actividadService.convertToDTO(actividad);
            return ResponseEntity.ok().body(actividadDTO); // Returns the activity as a DTO
        }
        return ResponseEntity.notFound().build(); // Returns 404 if not found
    }

    /**
     * Retrieves activities assigned to a specific evaluator.
     * Filters activities by the evaluator's user ID and returns them as DTOs.
     */
    @GetMapping("/findActivitiesByEvaluado/{oidUsuario}")
    public ResponseEntity<List<ActividadDTO>> listActivitiesByEvaluado(
            @PathVariable Integer oidUsuario,
            @RequestParam(defaultValue = "true") boolean ascendingOrder) {
        
        List<ActividadDTO> activities = actividadService.findActivitiesByEvaluado(oidUsuario, ascendingOrder);
        if (activities.isEmpty()) {
            return ResponseEntity.noContent().build(); // Returns 204 if no activities are found
        }
        return ResponseEntity.ok(activities); // Returns the list of activities as DTOs
    }

    /**
     * Retrieves activities assigned to a specific evaluator in active academic
     * periods.
     * Filters activities by evaluator's user ID and active academic period,
     * returning them as DTOs.
     */
    @GetMapping("/findActivitiesByEvaluadoInActivePeriod/{oidUsuario}")
    public ResponseEntity<List<ActividadDTO>> listActivitiesByEvaluadoInActivePeriod(
            @PathVariable Integer oidUsuario,
            @RequestParam(defaultValue = "true") boolean ascendingOrder) {
        
        List<ActividadDTO> activities = actividadService.findActivitiesByEvaluadoInActivePeriod(oidUsuario, ascendingOrder);
        if (activities.isEmpty()) {
            return ResponseEntity.noContent().build(); // Returns 204 if no activities are found
        }
        return ResponseEntity.ok(activities); // Returns the list of activities as DTOs
    }

        /**
     * Retrieves activities assigned to a specific evaluator in active academic
     * periods.
     * Filters activities by evaluator's user ID and active academic period,
     * returning them as DTOs.
     */
    @GetMapping("/findActivitiesByEvaluadorInActivePeriod/{oidUsuario}")
    public ResponseEntity<List<ActividadDTOEvaluador>> listActivitiesByEvaluadorInActivePeriod(
            @PathVariable Integer oidUsuario,
            @RequestParam(defaultValue = "true") boolean ascendingOrder) {
        
        List<ActividadDTOEvaluador> activities = actividadService.findActivitiesByEvaluadorInActivePeriod(oidUsuario, ascendingOrder);
        if (activities.isEmpty()) {
            return ResponseEntity.noContent().build(); // Returns 204 if no activities are found
        }
        return ResponseEntity.ok(activities); // Returns the list of activities as DTOs
    }

    /**
     * Saves a new activity to the database.
     * Returns the saved activity if successful, or an error if saving fails.
     */
    @PostMapping("save")
    public ResponseEntity<?> save(@RequestBody Actividad actividad) {
        try {
            Actividad resultado = actividadService.save(actividad);
            if (resultado != null) {
                return ResponseEntity.ok().body(resultado); // Returns the saved activity
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getStackTrace());
        }
        return ResponseEntity.internalServerError().body("Error: Resultado nulo");
    }

    /**
     * Deletes an activity by its ID.
     * Returns 404 if the activity is not found, or 409 if there is a conflict while deleting.
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
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Actividad no encontrada");
        }

        try {
            actividadService.delete(oid); // Tries to delete the activity
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.CONFLICT).body("No se puede borrar por conflictos con otros datos");
        }
        return ResponseEntity.ok().build(); // Returns 200 if successfully deleted
    }
}
