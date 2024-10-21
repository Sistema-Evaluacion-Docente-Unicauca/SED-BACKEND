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

import co.edu.unicauca.sed.api.model.Usuario;
import co.edu.unicauca.sed.api.service.UsuarioService;

@Controller
@RequestMapping("usuario")
public class UsuarioController {
    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("all")
    public ResponseEntity<?> findAll() {
        try {
            List<Usuario> list = usuarioService.findAll();
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
        Usuario resultado = this.usuarioService.findByOid(oid);
        if (resultado != null) {
            return ResponseEntity.ok().body(resultado);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("save")
    public ResponseEntity<?> save(@RequestBody Usuario usuario) {
        try {
            Usuario resultado = usuarioService.save(usuario);

            if (resultado != null) {
                return ResponseEntity.ok().body(resultado);
            }

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error:" + e.getMessage());
        }
        return ResponseEntity.internalServerError().body("Error: Resultado nulo");
    }

    @DeleteMapping("delete/{oid}")
    public ResponseEntity<?> delete(@PathVariable Integer oid) {
        Usuario usuario = null;
        try {
            usuario = this.usuarioService.findByOid(oid);
            if (usuario == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        }

        try {
            this.usuarioService.delete(oid);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.CONFLICT).body("no se puede borrar por conflictos con otros datos");
        }
        return ResponseEntity.ok().build();
    }
}
