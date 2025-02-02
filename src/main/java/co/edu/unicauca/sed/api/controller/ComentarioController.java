package co.edu.unicauca.sed.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.data.domain.Page;
import co.edu.unicauca.sed.api.model.Comentario;
import co.edu.unicauca.sed.api.service.ComentarioService;

/**
 * Controlador para gestionar las operaciones relacionadas con los comentarios.
 * Proporciona endpoints para operaciones CRUD y consulta de comentarios con paginación.
 */
@Controller
@RequestMapping("api/comentario")
public class ComentarioController {

    @Autowired
    private ComentarioService comentarioService;

    @GetMapping
    public ResponseEntity<?> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "true") Boolean ascendingOrder) {
        try {
            Page<Comentario> comentarios = comentarioService.findAll(PageRequest.of(page, size), ascendingOrder);
            if (comentarios.hasContent()) {
                return ResponseEntity.ok().body(comentarios);
            } else {
                return ResponseEntity.noContent().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error:" + e.getMessage());
        }
    }

    @GetMapping("/{oid}")
    public ResponseEntity<?> find(@PathVariable Integer oid) {
        Comentario resultado = this.comentarioService.findByOid(oid);
        if (resultado != null) {
            return ResponseEntity.ok().body(resultado);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
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

    @DeleteMapping("/{oid}")
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
