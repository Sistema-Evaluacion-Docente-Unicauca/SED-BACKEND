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

import co.edu.unicauca.sed.api.model.Comentario;
import co.edu.unicauca.sed.api.service.ComentarioService;

@Controller
@RequestMapping("comentario")
public class ComentarioController {
    @Autowired
    private ComentarioService comentarioService;

    @GetMapping("all")
    public ResponseEntity<?> findAll() {
        try {
            List<Comentario> list = comentarioService.findAll();
            if (list != null) {
                if (list.size() != 0) {
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
        Comentario resultado = this.comentarioService.findByOid(oid);
        if (resultado != null) {
            return ResponseEntity.ok().body(resultado);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("save")
    public ResponseEntity<?> save(@RequestBody Comentario comentario) {
        try {
            Comentario resultado = comentarioService.save(comentario);

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
        Comentario comentario = null;
        try {
            comentario = this.comentarioService.findByOid(oid);
            if (comentario == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Comentario no encontrado");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Comentario no encontrado");
        }

        try {
            this.comentarioService.delete(oid);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.CONFLICT).body("no se puede borrar por conflictos con otros datos");
        }
        return ResponseEntity.ok().build();
    }
}
