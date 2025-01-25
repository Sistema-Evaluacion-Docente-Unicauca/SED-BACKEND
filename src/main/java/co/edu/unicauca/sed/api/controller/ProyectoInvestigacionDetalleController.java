package co.edu.unicauca.sed.api.controller;

import co.edu.unicauca.sed.api.model.ProyectoInvestigacionDetalle;
import co.edu.unicauca.sed.api.service.ProyectoInvestigacionDetalleService;
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
@RequestMapping("api/proyecto-investigacion-detalle")
public class ProyectoInvestigacionDetalleController {

    private static final Logger logger = LoggerFactory.getLogger(ProyectoInvestigacionDetalleController.class);

    @Autowired
    private ProyectoInvestigacionDetalleService service;

    @PostMapping
    public ResponseEntity<ProyectoInvestigacionDetalle> create(@RequestBody ProyectoInvestigacionDetalle detalle) {
        logger.info("Solicitud recibida para crear ProyectoInvestigacionDetalle: {}", detalle);
        return new ResponseEntity<>(service.create(detalle), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProyectoInvestigacionDetalle> findById(@PathVariable Integer id) {
        logger.info("Solicitud recibida para buscar ProyectoInvestigacionDetalle con id: {}", id);
        Optional<ProyectoInvestigacionDetalle> detalle = service.findById(id);
        return detalle.map(ResponseEntity::ok)
                .orElseThrow(() -> {
                    logger.error("ProyectoInvestigacionDetalle no encontrado con id: {}", id);
                    return new RuntimeException("ProyectoInvestigacionDetalle no encontrado con id: " + id);
                });
    }

    @GetMapping
    public ResponseEntity<Page<ProyectoInvestigacionDetalle>> findAll(Pageable pageable) {
        logger.info("Solicitud recibida para listar ProyectoInvestigacionDetalle con paginación");
        return ResponseEntity.ok(service.findAll(pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProyectoInvestigacionDetalle> update(@PathVariable Integer id, @RequestBody ProyectoInvestigacionDetalle detalle) {
        logger.info("Solicitud recibida para actualizar ProyectoInvestigacionDetalle con id: {}", id);
        return ResponseEntity.ok(service.update(id, detalle));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        logger.info("Solicitud recibida para eliminar ProyectoInvestigacionDetalle con id: {}", id);
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        logger.error("Manejando excepción: {}", ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }
}
