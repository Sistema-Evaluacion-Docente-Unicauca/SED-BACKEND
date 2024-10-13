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

import co.edu.unicauca.sed.api.model.PeriodoAcademico;
import co.edu.unicauca.sed.api.service.PeriodoAcademicoService;

@Controller
@RequestMapping("periodoAcademico")
public class PeriodoAcademicoController {
    @Autowired
    private PeriodoAcademicoService periodoAcademicoService;

    @GetMapping("all")
    public ResponseEntity<?> findAll() {
        try {
            List<PeriodoAcademico> list = periodoAcademicoService.findAll();
            if (list != null && !list.isEmpty()) {
                return ResponseEntity.ok().body(list);
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error:" + e.getStackTrace());
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("find/{oid}")
    public ResponseEntity<?> find(@PathVariable Integer oid) {
        PeriodoAcademico resultado = this.periodoAcademicoService.findByOid(oid);
        if (resultado != null) {
            return ResponseEntity.ok().body(resultado);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("save")
    public ResponseEntity<?> save(@RequestBody PeriodoAcademico periodoAcademico) {
        try {
            PeriodoAcademico resultado = periodoAcademicoService.save(periodoAcademico);

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
        PeriodoAcademico periodoAcademico = null;
        try {
            periodoAcademico = this.periodoAcademicoService.findByOid(oid);
            if (periodoAcademico == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("PeriodoAcademico no encontrado");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("PeriodoAcademico no encontrado");
        }

        try {
            this.periodoAcademicoService.delete(oid);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.CONFLICT).body("no se puede borrar por conflictos con otros datos");
        }
        return ResponseEntity.ok().build();
    }
}
