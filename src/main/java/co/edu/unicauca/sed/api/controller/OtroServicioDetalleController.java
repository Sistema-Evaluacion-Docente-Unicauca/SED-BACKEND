package co.edu.unicauca.sed.api.controller;

import co.edu.unicauca.sed.api.model.OtroServicioDetalle;
import co.edu.unicauca.sed.api.service.OtroServicioDetalleService;
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
@RequestMapping("api/otro-servicio-detalle")
public class OtroServicioDetalleController {

    private static final Logger logger = LoggerFactory.getLogger(OtroServicioDetalleController.class);

    @Autowired
    private OtroServicioDetalleService service;

    @PostMapping
    public ResponseEntity<OtroServicioDetalle> create(@RequestBody OtroServicioDetalle detalle) {
        logger.info("Solicitud recibida para crear OtroServicioDetalle: {}", detalle);
        return new ResponseEntity<>(service.create(detalle), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OtroServicioDetalle> findById(@PathVariable Integer id) {
        logger.info("Solicitud recibida para buscar OtroServicioDetalle con id: {}", id);
        Optional<OtroServicioDetalle> detalle = service.findById(id);
        return detalle.map(ResponseEntity::ok)
                .orElseThrow(() -> {
                    logger.error("OtroServicioDetalle no encontrado con id: {}", id);
                    return new RuntimeException("OtroServicioDetalle no encontrado con id: " + id);
                });
    }

    @GetMapping
    public ResponseEntity<Page<OtroServicioDetalle>> findAll(Pageable pageable) {
        logger.info("Solicitud recibida para listar OtroServicioDetalle con paginación");
        return ResponseEntity.ok(service.findAll(pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OtroServicioDetalle> update(@PathVariable Integer id, @RequestBody OtroServicioDetalle detalle) {
        logger.info("Solicitud recibida para actualizar OtroServicioDetalle con id: {}", id);
        return ResponseEntity.ok(service.update(id, detalle));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        logger.info("Solicitud recibida para eliminar OtroServicioDetalle con id: {}", id);
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        logger.error("Manejando excepción: {}", ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }
}
