package co.edu.unicauca.sed.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import co.edu.unicauca.sed.api.model.Proceso;
import co.edu.unicauca.sed.api.service.ProcesoService;

@Controller
@RequestMapping("proceso")
public class ProcesoController {
    @Autowired
    private ProcesoService procesoService;

    @GetMapping("all")
    public ResponseEntity<?> findAll() {
        try {
            List<Proceso> list = procesoService.findAll();
            if (list != null && !list.isEmpty()) {
                return ResponseEntity.ok().body(list);
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error:" + e.getStackTrace());
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("find/{oid}")
    public ResponseEntity<?> findById(@PathVariable Integer oid) {
        Proceso resultado = this.procesoService.findByOid(oid);
        if (resultado != null) {
            return ResponseEntity.ok().body(resultado);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("findByEvaluado/{oidUsuario}")
    public ResponseEntity<List<Proceso>> findByEvaluado(@PathVariable Integer oidUsuario) {
        List<Proceso> procesos = procesoService.getProcessesByEvaluated(oidUsuario);
        if (procesos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(procesos);
    }

    @GetMapping("findByEvaluadoInActivePeriod/{oidUsuario}")
    public ResponseEntity<List<Proceso>> findByEvaluadoInActivePeriod(@PathVariable Integer oidUsuario) {
        List<Proceso> procesos = procesoService.getProcessesByEvaluatedAndActivePeriod(oidUsuario);
        if (procesos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(procesos);
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
