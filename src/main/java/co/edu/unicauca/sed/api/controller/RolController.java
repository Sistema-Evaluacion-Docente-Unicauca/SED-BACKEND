package co.edu.unicauca.sed.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import co.edu.unicauca.sed.api.domain.Rol;
import co.edu.unicauca.sed.api.service.RolService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Controlador para la gestión de roles.
 * Proporciona endpoints para realizar operaciones CRUD sobre los roles.
 */
@Controller
@RestController
@RequestMapping("api/roles")
public class RolController {

    private static final Logger logger = LoggerFactory.getLogger(RolController.class);

    @Autowired
    private RolService rolService;

    /**
     * Crea un nuevo rol en el sistema.
     *
     * @param rol Datos del rol a crear.
     * @return El rol creado con el estado HTTP 201 (CREATED).
     */
    @PostMapping
    public ResponseEntity<Rol> create(@RequestBody Rol rol) {
        return new ResponseEntity<>(rolService.save(rol), HttpStatus.CREATED);
    }

    /**
     * Recupera un rol por su ID.
     *
     * @param id ID del rol a buscar.
     * @return El rol encontrado o un error 404 si no se encuentra.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Rol> findById(@PathVariable Integer id) {
        return rolService.findByOid(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> {
                    logger.error("Rol no encontrado con id: {}", id);
                    return new RuntimeException("Rol no encontrado con id: " + id);
                });
    }

    /**
     * Recupera todos los roles con paginación.
     *
     * @param pageable Parámetros de paginación.
     * @return Página de roles.
     */
    @GetMapping
    public ResponseEntity<Page<Rol>> findAll(@PageableDefault(size = 10, page = 0) Pageable pageable) {
        return ResponseEntity.ok(rolService.findAll(pageable));
    }

    /**
     * Actualiza un rol existente.
     *
     * @param id  ID del rol a actualizar.
     * @param rol Datos actualizados del rol.
     * @return El rol actualizado.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Rol> update(@PathVariable Integer id, @RequestBody Rol rol) {
        logger.info("Solicitud recibida para actualizar un rol con id: {}", id);
        return ResponseEntity.ok(rolService.update(id, rol));
    }

    /**
     * Elimina un rol por su ID.
     *
     * @param id ID del rol a eliminar.
     * @return Respuesta con estado HTTP 204 (No Content) si se elimina
     *         exitosamente.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        logger.info("Solicitud recibida para eliminar un rol con id: {}", id);
        rolService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Manejador de excepciones para errores en el controlador.
     *
     * @param ex Excepción ocurrida.
     * @return Respuesta con el mensaje de error y estado HTTP 404 (Not Found).
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        logger.error("Manejando excepción: {}", ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }
}
