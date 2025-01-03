package co.edu.unicauca.sed.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import co.edu.unicauca.sed.api.model.Rol;
import co.edu.unicauca.sed.api.service.RolService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;

/**
 * Controlador para la gestión de roles.
 * Proporciona endpoints para realizar operaciones CRUD sobre los roles.
 */
@Controller
@RequestMapping("rol")
public class RolController {

    private static final Logger logger = LoggerFactory.getLogger(RolController.class);

    @Autowired
    private RolService rolService;

    /**
     * Recupera todos los roles disponibles en el sistema.
     *
     * @return Lista de roles o un error si ocurre algún problema.
     */
    @GetMapping("all")
    public ResponseEntity<?> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<Rol> roles = rolService.findAll(PageRequest.of(page, size));
            if (roles.hasContent()) {
                return ResponseEntity.ok().body(roles);
            } else {
                return ResponseEntity.noContent().build();
            }
        } catch (Exception e) {
            logger.error("Error al obtener los roles: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    /**
     * Busca un rol específico por su ID.
     *
     * @param oid El ID del rol.
     * @return El rol encontrado o un error 404 si no se encuentra.
     */
    @GetMapping("find/{oid}")
    public ResponseEntity<?> find(@PathVariable Integer oid) {
        Rol resultado = rolService.findByOid(oid);
        if (resultado != null) {
            logger.info("Rol con ID {} encontrado.", oid);
            return ResponseEntity.ok().body(resultado);
        } else {
            logger.warn("Rol con ID {} no encontrado.", oid);
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Guarda un nuevo rol en el sistema.
     *
     * @param rol El objeto Rol a guardar.
     * @return El rol guardado o un mensaje de error.
     */
    @PostMapping("save")
    public ResponseEntity<?> save(@RequestBody Rol rol) {
        try {
            Rol resultado = rolService.save(rol);
            if (resultado != null) {
                return ResponseEntity.ok().body(resultado);
            } else {
                logger.error("Error al guardar el rol. Resultado nulo.");
                return ResponseEntity.internalServerError().body("Error: Resultado nulo");
            }
        } catch (Exception e) {
            logger.error("Error al guardar el rol: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    /**
     * Actualiza un rol existente.
     *
     * @param oid El ID del rol a actualizar.
     * @param rol Datos actualizados del rol.
     * @return Mensaje de éxito o error.
     */
    @PutMapping("update/{oid}")
    public ResponseEntity<?> update(@PathVariable Integer oid, @RequestBody Rol rol) {
        try {
            boolean resultado = rolService.update(oid, rol);
            if (resultado) {
                logger.info("Rol con ID {} actualizado exitosamente.", oid);
                return ResponseEntity.ok("Rol actualizado correctamente.");
            } else {
                logger.warn("Rol con ID {} no encontrado.", oid);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Rol no encontrado.");
            }
        } catch (Exception e) {
            logger.error("Error al actualizar el rol con ID {}: {}", oid, e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    /**
     * Elimina un rol por su ID.
     *
     * @param oid El ID del rol a eliminar.
     * @return Mensaje de confirmación si se elimina, o un error si ocurre un problema.
     */
    @DeleteMapping("delete/{oid}")
    public ResponseEntity<?> delete(@PathVariable Integer oid) {
        try {
            Rol rol = rolService.findByOid(oid);
            if (rol == null) {
                logger.warn("Rol con ID {} no encontrado.", oid);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Rol no encontrado");
            }
            rolService.delete(oid);
            logger.info("Rol con ID {} eliminado exitosamente.", oid);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Error al eliminar el rol con ID {}: {}", oid, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.CONFLICT).body("No se puede borrar por conflictos con otros datos");
        }
    }
}
