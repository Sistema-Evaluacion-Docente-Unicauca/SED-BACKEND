package co.edu.unicauca.sed.api.controller;

import co.edu.unicauca.sed.api.dto.ActividadDTO;
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
    public ResponseEntity<?> findAll() {
        try {
            List<ActividadDTO> list = actividadService.findAll();

            if (list != null && !list.isEmpty()) {
                return ResponseEntity.ok().body(list); // Returns the list of activities (DTOs)
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getStackTrace());
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Retrieves all activities associated with active academic periods.
     * Returns the activities as DTOs.
     */
    @GetMapping("findAllInActivePeriods")
    public ResponseEntity<?> findAllInActivePeriods() {
        try {
            // Call the service to get all activities in active periods
            List<ActividadDTO> list = actividadService.findAllInActivePeriods();

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
            // Convertir la actividad a DTO antes de devolverla
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
    public ResponseEntity<List<ActividadDTO>> listActivitiesByEvaluado(@PathVariable Integer oidUsuario) {
        List<ActividadDTO> activities = actividadService.findActivitiesByEvaluado(oidUsuario);
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
    public ResponseEntity<List<ActividadDTO>> listActivitiesByEvaluadoInActivePeriod(@PathVariable Integer oidUsuario) {
        List<ActividadDTO> activities = actividadService.findActivitiesByEvaluadoInActivePeriod(oidUsuario);
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
     * Returns 404 if the activity is not found, or 409 if there is a conflict while
     * deleting.
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
