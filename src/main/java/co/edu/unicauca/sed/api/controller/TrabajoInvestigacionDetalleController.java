package co.edu.unicauca.sed.api.controller;

import co.edu.unicauca.sed.api.model.TrabajoInvestigacionDetalle;
import co.edu.unicauca.sed.api.service.TrabajoInvestigacionDetalleService;
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
@RequestMapping("api/trabajo-investigacion-detalle")
public class TrabajoInvestigacionDetalleController {

    private static final Logger logger = LoggerFactory.getLogger(TrabajoInvestigacionDetalleController.class);

    @Autowired
    private TrabajoInvestigacionDetalleService service;

    @PostMapping
    public ResponseEntity<TrabajoInvestigacionDetalle> create(@RequestBody TrabajoInvestigacionDetalle detalle) {
        logger.info("Solicitud recibida para crear TrabajoInvestigacionDetalle: {}", detalle);
        return new ResponseEntity<>(service.create(detalle), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TrabajoInvestigacionDetalle> findById(@PathVariable Integer id) {
        logger.info("Solicitud recibida para buscar TrabajoInvestigacionDetalle con id: {}", id);
        Optional<TrabajoInvestigacionDetalle> detalle = service.findById(id);
        return detalle.map(ResponseEntity::ok)
                .orElseThrow(() -> {
                    logger.error("TrabajoInvestigacionDetalle no encontrado con id: {}", id);
                    return new RuntimeException("TrabajoInvestigacionDetalle no encontrado con id: " + id);
                });
    }

    @GetMapping
    public ResponseEntity<Page<TrabajoInvestigacionDetalle>> findAll(Pageable pageable) {
        logger.info("Solicitud recibida para listar TrabajoInvestigacionDetalle con paginación");
        return ResponseEntity.ok(service.findAll(pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TrabajoInvestigacionDetalle> update(@PathVariable Integer id, @RequestBody TrabajoInvestigacionDetalle detalle) {
        logger.info("Solicitud recibida para actualizar TrabajoInvestigacionDetalle con id: {}", id);
        return ResponseEntity.ok(service.update(id, detalle));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        logger.info("Solicitud recibida para eliminar TrabajoInvestigacionDetalle con id: {}", id);
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        logger.error("Manejando excepción: {}", ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }
}
