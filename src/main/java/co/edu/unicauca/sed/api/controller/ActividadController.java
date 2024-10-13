package co.edu.unicauca.sed.api.controller;

import co.edu.unicauca.sed.api.model.Actividad;
import co.edu.unicauca.sed.api.model.Fuente;
import co.edu.unicauca.sed.api.service.ActividadService;
import co.edu.unicauca.sed.api.service.FuenteService;
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

    @Autowired
    private FuenteService fuenteService;

    @GetMapping("all")
    public ResponseEntity<?> findAll() {
        try {
            List<Actividad> list = actividadService.findAll();
            if (list != null && !list.isEmpty()) {
                return ResponseEntity.ok().body(list);
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getStackTrace());
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("find/{oid}")
    public ResponseEntity<?> findById(@PathVariable Integer oid) {
        Actividad actividad = actividadService.findByOid(oid);
        if (actividad != null) {
            return ResponseEntity.ok().body(actividad);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("find/{oid}/fuentes")
    public ResponseEntity<?> findActividadWithFuentes(@PathVariable Integer oid) {
        Actividad actividad = actividadService.findByOid(oid);
        if (actividad != null) {
            return ResponseEntity.ok().body(actividad);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Actividad no encontrada");
    }

    @PostMapping("save")
    public ResponseEntity<?> save(@RequestBody Actividad actividad) {
        try {
            Actividad resultado = actividadService.save(actividad);
            if (resultado != null) {
                return ResponseEntity.ok().body(resultado);
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getStackTrace());
        }
        return ResponseEntity.internalServerError().body("Error: Resultado nulo");
    }

    @DeleteMapping("delete/{oid}")
    public ResponseEntity<?> delete(@PathVariable Integer oid) {
        Actividad actividad = null;
        try {
            actividad = actividadService.findByOid(oid);
            if (actividad == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Actividad no encontrada");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Actividad no encontrada");
        }

        try {
            actividadService.delete(oid);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.CONFLICT).body("No se puede borrar por conflictos con otros datos");
        }
        return ResponseEntity.ok().build();
    }
}
