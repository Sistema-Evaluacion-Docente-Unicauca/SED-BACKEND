package co.edu.unicauca.sed.api.controller;

import co.edu.unicauca.sed.api.domain.EstadoPeriodoAcademico;
import co.edu.unicauca.sed.api.service.periodo_academico.EstadoPeriodoAcademicoService;

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
@RequestMapping("api/estado-periodo-academico")
public class EstadoPeriodoAcademicoController {

    private static final Logger logger = LoggerFactory.getLogger(EstadoPeriodoAcademicoController.class);

    @Autowired
    private EstadoPeriodoAcademicoService service;

    @PostMapping
    public ResponseEntity<EstadoPeriodoAcademico> create(@RequestBody EstadoPeriodoAcademico estadoPeriodoAcademico) {
        logger.info("Solicitud recibida para crear EstadoPeriodoAcademico: {}", estadoPeriodoAcademico);
        return new ResponseEntity<>(service.create(estadoPeriodoAcademico), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EstadoPeriodoAcademico> findById(@PathVariable Integer id) {
        logger.info("Solicitud recibida para buscar EstadoPeriodoAcademico con id: {}", id);
        Optional<EstadoPeriodoAcademico> estadoPeriodoAcademico = service.findById(id);
        return estadoPeriodoAcademico.map(ResponseEntity::ok)
                .orElseThrow(() -> {
                    logger.error("EstadoPeriodoAcademico no encontrado con id: {}", id);
                    return new RuntimeException("EstadoPeriodoAcademico no encontrado con id: " + id);
                });
    }

    @GetMapping
    public ResponseEntity<Page<EstadoPeriodoAcademico>> findAll(Pageable pageable) {
        logger.info("Solicitud recibida para listar EstadoPeriodoAcademico con paginación");
        return ResponseEntity.ok(service.findAll(pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EstadoPeriodoAcademico> update(@PathVariable Integer id, @RequestBody EstadoPeriodoAcademico estadoPeriodoAcademico) {
        logger.info("Solicitud recibida para actualizar EstadoPeriodoAcademico con id: {}", id);
        return ResponseEntity.ok(service.update(id, estadoPeriodoAcademico));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        logger.info("Solicitud recibida para eliminar EstadoPeriodoAcademico con id: {}", id);
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        logger.error("Error manejado: {}", ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }
}
