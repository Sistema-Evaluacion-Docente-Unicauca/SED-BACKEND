package co.edu.unicauca.sed.api.controller;

import co.edu.unicauca.sed.api.model.DocenciaDetalle;
import co.edu.unicauca.sed.api.service.DocenciaDetalleService;
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
@RequestMapping("/api/docencia-detalle")
public class DocenciaDetalleController {

    private static final Logger logger = LoggerFactory.getLogger(DocenciaDetalleController.class);

    @Autowired
    private DocenciaDetalleService service;

    @PostMapping
    public ResponseEntity<DocenciaDetalle> create(@RequestBody DocenciaDetalle detalle) {
        logger.info("Solicitud recibida para crear DocenciaDetalle: {}", detalle);
        return new ResponseEntity<>(service.create(detalle), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DocenciaDetalle> findById(@PathVariable Integer id) {
        logger.info("Solicitud recibida para buscar DocenciaDetalle con id: {}", id);
        Optional<DocenciaDetalle> detalle = service.findById(id);
        return detalle.map(ResponseEntity::ok)
                .orElseThrow(() -> {
                    logger.error("DocenciaDetalle no encontrado con id: {}", id);
                    return new RuntimeException("DocenciaDetalle no encontrado con id: " + id);
                });
    }

    @GetMapping
    public ResponseEntity<Page<DocenciaDetalle>> findAll(Pageable pageable) {
        logger.info("Solicitud recibida para listar DocenciaDetalle con paginación");
        return ResponseEntity.ok(service.findAll(pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DocenciaDetalle> update(@PathVariable Integer id, @RequestBody DocenciaDetalle detalle) {
        logger.info("Solicitud recibida para actualizar DocenciaDetalle con id: {}", id);
        return ResponseEntity.ok(service.update(id, detalle));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        logger.info("Solicitud recibida para eliminar DocenciaDetalle con id: {}", id);
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        logger.error("Manejando excepción: {}", ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }
}
