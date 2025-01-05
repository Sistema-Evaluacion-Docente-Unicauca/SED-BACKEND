package co.edu.unicauca.sed.api.controller;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.data.domain.Page;
import co.edu.unicauca.sed.api.model.Proceso;
import co.edu.unicauca.sed.api.service.ProcesoService;

@Controller
@RequestMapping("proceso")
public class ProcesoController {
    @Autowired
    private ProcesoService procesoService;


    @GetMapping("all")
    public ResponseEntity<Page<Proceso>> findAll(
            @RequestParam(required = false) Integer evaluadorId,
            @RequestParam(required = false) Integer evaluadoId,
            @RequestParam(required = false) Integer idPeriodo,
            @RequestParam(required = false) String nombreProceso,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaCreacion,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaActualizacion,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<Proceso> procesos = procesoService.findAll(
                evaluadorId, evaluadoId, idPeriodo,
                nombreProceso, fechaCreacion, fechaActualizacion,
                page, size);

        if (procesos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(procesos);
    }

    @GetMapping("find/{oid}")
    public ResponseEntity<?> findById(@PathVariable Integer oid) {
        Proceso resultado = this.procesoService.findByOid(oid);
        if (resultado != null) {
            return ResponseEntity.ok().body(resultado);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("save")
    public ResponseEntity<?> save(@RequestBody Proceso proceso) {
        try {
            Proceso resultado = procesoService.save(proceso);

            if (resultado != null) {
                return ResponseEntity.ok().body(resultado);
            }

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error:" + e.getStackTrace());
        }
        return ResponseEntity.internalServerError().body("Error: Resultado nulo");
    }

    @PutMapping("update/{oid}")
    public ResponseEntity<?> update(@PathVariable Integer oid, @RequestBody Proceso proceso) {
        try {
            Proceso updatedProceso = procesoService.update(oid, proceso);

            if (updatedProceso != null) {
                return ResponseEntity.ok(updatedProceso);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Proceso no encontrado");
            }

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    @DeleteMapping("delete/{oid}")
    public ResponseEntity<?> delete(@PathVariable Integer oid) {
        Proceso proceso = null;
        try {
            proceso = this.procesoService.findByOid(oid);
            if (proceso == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Proceso no encontrado");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Proceso no encontrado");
        }

        try {
            this.procesoService.delete(oid);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.CONFLICT).body("No se puede borrar por conflictos con otros datos");
        }
        return ResponseEntity.ok().build();
    }
}
