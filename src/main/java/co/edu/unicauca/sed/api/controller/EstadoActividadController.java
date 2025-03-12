package co.edu.unicauca.sed.api.controller;

import co.edu.unicauca.sed.api.domain.EstadoActividad;
import co.edu.unicauca.sed.api.service.actividad.EstadoActividadService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("api/estado-actividad")
public class EstadoActividadController {

    private static final Logger logger = LoggerFactory.getLogger(EstadoActividadController.class);

    @Autowired
    private EstadoActividadService service;

    @PostMapping
    public ResponseEntity<EstadoActividad> create(@RequestBody EstadoActividad estadoActividad) {
        logger.info("Solicitud recibida para crear EstadoActividad: {}", estadoActividad);
        return new ResponseEntity<>(service.create(estadoActividad), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EstadoActividad> findById(@PathVariable Integer id) {
        logger.info("Solicitud recibida para buscar EstadoActividad con id: {}", id);
        Optional<EstadoActividad> estadoActividad = service.findById(id);
        return estadoActividad.map(ResponseEntity::ok)
                .orElseThrow(() -> {
                    logger.error("EstadoActividad no encontrado con id: {}", id);
                    return new RuntimeException("EstadoActividad no encontrado con id: " + id);
                });
    }

    @GetMapping
    public ResponseEntity<Page<EstadoActividad>> findAll(Pageable pageable) {
        logger.info("Solicitud recibida para listar EstadoActividad con paginación");
        return ResponseEntity.ok(service.findAll(pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EstadoActividad> update(@PathVariable Integer id, @RequestBody EstadoActividad estadoActividad) {
        logger.info("Solicitud recibida para actualizar EstadoActividad con id: {}", id);
        return ResponseEntity.ok(service.update(id, estadoActividad));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        logger.info("Solicitud recibida para eliminar EstadoActividad con id: {}", id);
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        logger.error("Error manejado: {}", ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }
}
