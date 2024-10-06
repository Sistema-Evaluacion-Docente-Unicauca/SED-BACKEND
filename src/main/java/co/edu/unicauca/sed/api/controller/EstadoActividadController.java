package co.edu.unicauca.sed.api.controller;

import co.edu.unicauca.sed.api.model.EstadoActividad;
import co.edu.unicauca.sed.api.service.EstadoActividadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("estadoactividad")
public class EstadoActividadController {

    @Autowired
    private EstadoActividadService service;

    @GetMapping("all")
    public ResponseEntity<?> findAll() {
        try {
            List<EstadoActividad> list = service.findAll();
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
        EstadoActividad estadoActividad = service.findByOid(oid);
        if (estadoActividad != null) {
            return ResponseEntity.ok().body(estadoActividad);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("save")
    public ResponseEntity<?> save(@RequestBody EstadoActividad estadoActividad) {
        try {
            EstadoActividad resultado = service.save(estadoActividad);
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
        EstadoActividad estadoActividad = null;
        try {
            estadoActividad = service.findByOid(oid);
            if (estadoActividad == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("EstadoActividad no encontrado");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("EstadoActividad no encontrado");
        }

        try {
            service.delete(oid);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.CONFLICT).body("No se puede borrar por conflictos con otros datos");
        }
        return ResponseEntity.ok().build();
    }
}
