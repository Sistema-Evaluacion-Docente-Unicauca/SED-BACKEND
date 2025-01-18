package co.edu.unicauca.sed.api.controller;

import co.edu.unicauca.sed.api.model.AdministracionDetalle;
import co.edu.unicauca.sed.api.service.AdministracionDetalleService;
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
@RequestMapping("/api/administracion-detalle")
public class AdministracionDetalleController {

    private static final Logger logger = LoggerFactory.getLogger(AdministracionDetalleController.class);

    @Autowired
    private AdministracionDetalleService service;

    @PostMapping
    public ResponseEntity<AdministracionDetalle> create(@RequestBody AdministracionDetalle detalle) {
        logger.info("Solicitud recibida para crear AdministracionDetalle: {}", detalle);
        return new ResponseEntity<>(service.create(detalle), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdministracionDetalle> findById(@PathVariable Integer id) {
        logger.info("Solicitud recibida para buscar AdministracionDetalle con id: {}", id);
        Optional<AdministracionDetalle> detalle = service.findById(id);
        return detalle.map(ResponseEntity::ok)
                .orElseThrow(() -> {
                    logger.error("AdministracionDetalle no encontrado con id: {}", id);
                    return new RuntimeException("AdministracionDetalle no encontrado con id: " + id);
                });
    }

    @GetMapping
    public ResponseEntity<Page<AdministracionDetalle>> findAll(Pageable pageable) {
        logger.info("Solicitud recibida para listar AdministracionDetalle con paginación");
        return ResponseEntity.ok(service.findAll(pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AdministracionDetalle> update(@PathVariable Integer id, @RequestBody AdministracionDetalle detalle) {
        logger.info("Solicitud recibida para actualizar AdministracionDetalle con id: {}", id);
        return ResponseEntity.ok(service.update(id, detalle));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        logger.info("Solicitud recibida para eliminar AdministracionDetalle con id: {}", id);
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        logger.error("Manejando excepción: {}", ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }
}
