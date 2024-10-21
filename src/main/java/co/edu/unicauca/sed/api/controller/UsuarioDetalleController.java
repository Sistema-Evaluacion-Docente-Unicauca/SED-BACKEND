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

import co.edu.unicauca.sed.api.model.UsuarioDetalle;
import co.edu.unicauca.sed.api.service.UsuarioDetalleService;

@Controller
@RequestMapping("usuarioDetalle")
public class UsuarioDetalleController {

    @Autowired
    private UsuarioDetalleService usuarioDetalleService;

    /**
     * Retrieves all user details.
     *
     * @return List of all user details or 404 if none found.
     */
    @GetMapping("all")
    public ResponseEntity<?> findAll() {
        try {
            List<UsuarioDetalle> list = usuarioDetalleService.findAll();
            if (list != null && !list.isEmpty()) {
                return ResponseEntity.ok().body(list);
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getStackTrace());
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Finds a specific user detail by its ID.
     *
     * @param oid The ID of the user detail.
     * @return The user detail if found, or 404 if not.
     */
    @GetMapping("find/{oid}")
    public ResponseEntity<?> find(@PathVariable Integer oid) {
        UsuarioDetalle resultado = usuarioDetalleService.findByOid(oid);
        if (resultado != null) {
            return ResponseEntity.ok().body(resultado);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Saves a new user detail.
     *
     * @param usuarioDetalle The user detail to save.
     * @return The saved user detail, or an error if something goes wrong.
     */
    @PostMapping("save")
    public ResponseEntity<?> save(@RequestBody UsuarioDetalle usuarioDetalle) {
        try {
            UsuarioDetalle resultado = usuarioDetalleService.save(usuarioDetalle);
            if (resultado != null) {
                return ResponseEntity.ok().body(resultado);
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
        return ResponseEntity.internalServerError().body("Error: Resultado nulo");
    }

    /**
     * Deletes a user detail by its ID.
     *
     * @param oid The ID of the user detail to delete.
     * @return Confirmation if deleted, or an error if conflicts exist.
     */
    @DeleteMapping("delete/{oid}")
    public ResponseEntity<?> delete(@PathVariable Integer oid) {
        UsuarioDetalle usuarioDetalle = null;
        try {
            usuarioDetalle = usuarioDetalleService.findByOid(oid);
            if (usuarioDetalle == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("UsuarioDetalle no encontrado");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("UsuarioDetalle no encontrado");
        }

        try {
            usuarioDetalleService.delete(oid);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.CONFLICT).body("No se puede borrar por conflictos con otros datos");
        }
        return ResponseEntity.ok().build();
    }
}
