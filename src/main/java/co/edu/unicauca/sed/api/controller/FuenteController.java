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

import co.edu.unicauca.sed.api.model.Fuente;
import co.edu.unicauca.sed.api.service.FuenteService;

@Controller
@RequestMapping("fuente")
public class FuenteController {

    @Autowired
    private FuenteService fuenteService;

    @GetMapping("all")
    public ResponseEntity<?> findAll() {
        try {
            List<Fuente> list = (List<Fuente>) fuenteService.findAll();
            if (list != null) {
                if (!list.isEmpty()) {
                    return ResponseEntity.ok().body(list);
                }
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error:" + e.getStackTrace());
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("find/{oid}")
    public ResponseEntity<?> find(@PathVariable Integer oid) {
        Fuente resultado = fuenteService.findById(oid).orElse(null);
        if (resultado != null) {
            return ResponseEntity.ok().body(resultado);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("save")
    public ResponseEntity<?> save(@RequestBody Fuente fuente) {
        try {
            Fuente resultado = fuenteService.save(fuente);

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
        Fuente fuente = null;
        try {
            fuente = fuenteService.findById(oid).orElse(null);
            if (fuente == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Fuente no encontrada");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Fuente no encontrada");
        }

        try {
            fuenteService.deleteById(oid);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.CONFLICT).body("No se puede borrar por conflictos con otros datos");
        }
        return ResponseEntity.ok().build();
    }
}
