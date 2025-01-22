package co.edu.unicauca.sed.api.controller;

import co.edu.unicauca.sed.api.model.TipoActividad;
import co.edu.unicauca.sed.api.service.TipoActividadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para la gestión de tipos de actividad.
 * Proporciona endpoints para operaciones CRUD sobre los tipos de actividad.
 */
@RestController
@RequestMapping("/api/tipo-actividad")
public class TipoActividadController {

    private static final Logger logger = LoggerFactory.getLogger(TipoActividadController.class);

    @Autowired
    private TipoActividadService service;

    /**
     * Crea un nuevo tipo de actividad en el sistema.
     *
     * @param tipoActividad Datos del tipo de actividad a crear.
     * @return El tipo de actividad creado con estado HTTP 201 (CREATED).
     */
    @PostMapping
    public ResponseEntity<TipoActividad> create(@RequestBody TipoActividad tipoActividad) {
        logger.info("Solicitud para crear TipoActividad: {}", tipoActividad);
        return new ResponseEntity<>(service.save(tipoActividad), HttpStatus.CREATED);
    }

    /**
     * Recupera un tipo de actividad por su ID.
     *
     * @param id ID del tipo de actividad a buscar.
     * @return El tipo de actividad encontrado o un error 404 si no existe.
     */
    @GetMapping("/{id}")
    public ResponseEntity<TipoActividad> findById(@PathVariable Integer id) {
        logger.info("Solicitud para buscar TipoActividad con ID: {}", id);
        TipoActividad tipoActividad = service.findByOid(id);
        if (tipoActividad != null) {
            return ResponseEntity.ok(tipoActividad);
        } else {
            logger.error("TipoActividad no encontrado con ID: {}", id);
            throw new RuntimeException("TipoActividad no encontrado con ID: " + id);
        }
    }

    /**
     * Recupera todos los tipos de actividad con soporte de paginación.
     *
     * @param pageable Parámetros de paginación.
     * @return Página de tipos de actividad.
     */
    @GetMapping
    public ResponseEntity<Page<TipoActividad>> findAll(Pageable pageable) {
        logger.info("Solicitud para listar TipoActividad con paginación");
        return ResponseEntity.ok(service.findAll(pageable));
    }

    /**
     * Actualiza un tipo de actividad existente.
     *
     * @param id            ID del tipo de actividad a actualizar.
     * @param tipoActividad Datos actualizados del tipo de actividad.
     * @return El tipo de actividad actualizado.
     */
    @PutMapping("/{id}")
    public ResponseEntity<TipoActividad> update(@PathVariable Integer id, @RequestBody TipoActividad tipoActividad) {
        logger.info("Solicitud para actualizar TipoActividad con ID: {}", id);
        TipoActividad updatedTipoActividad = service.update(id, tipoActividad);
        return ResponseEntity.ok(updatedTipoActividad);
    }

    /**
     * Elimina un tipo de actividad por su ID.
     *
     * @param id ID del tipo de actividad a eliminar.
     * @return Respuesta con estado HTTP 204 (No Content) si se elimina
     *         correctamente.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        logger.info("Solicitud para eliminar TipoActividad con ID: {}", id);
        service.delete(id);
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
