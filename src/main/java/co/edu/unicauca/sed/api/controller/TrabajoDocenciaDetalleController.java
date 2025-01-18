package co.edu.unicauca.sed.api.controller;

import co.edu.unicauca.sed.api.model.TrabajoDocenciaDetalle;
import co.edu.unicauca.sed.api.service.TrabajoDocenciaDetalleService;
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
@RequestMapping("/api/trabajo-docencia-detalle")
public class TrabajoDocenciaDetalleController {

    private static final Logger logger = LoggerFactory.getLogger(TrabajoDocenciaDetalleController.class);

    @Autowired
    private TrabajoDocenciaDetalleService service;

    @PostMapping
    public ResponseEntity<TrabajoDocenciaDetalle> create(@RequestBody TrabajoDocenciaDetalle detalle) {
        logger.info("Solicitud recibida para crear TrabajoDocenciaDetalle: {}", detalle);
        return new ResponseEntity<>(service.create(detalle), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TrabajoDocenciaDetalle> findById(@PathVariable Integer id) {
        logger.info("Solicitud recibida para buscar TrabajoDocenciaDetalle con id: {}", id);
        Optional<TrabajoDocenciaDetalle> detalle = service.findById(id);
        return detalle.map(ResponseEntity::ok)
                .orElseThrow(() -> {
                    logger.error("TrabajoDocenciaDetalle no encontrado con id: {}", id);
                    return new RuntimeException("TrabajoDocenciaDetalle no encontrado con id: " + id);
                });
    }

    @GetMapping
    public ResponseEntity<Page<TrabajoDocenciaDetalle>> findAll(Pageable pageable) {
        logger.info("Solicitud recibida para listar TrabajoDocenciaDetalle con paginación");
        return ResponseEntity.ok(service.findAll(pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TrabajoDocenciaDetalle> update(@PathVariable Integer id, @RequestBody TrabajoDocenciaDetalle detalle) {
        logger.info("Solicitud recibida para actualizar TrabajoDocenciaDetalle con id: {}", id);
        return ResponseEntity.ok(service.update(id, detalle));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        logger.info("Solicitud recibida para eliminar TrabajoDocenciaDetalle con id: {}", id);
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        logger.error("Manejando excepción: {}", ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }
}
