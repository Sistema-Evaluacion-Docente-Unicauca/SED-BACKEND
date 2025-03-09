package co.edu.unicauca.sed.api.controller;

import co.edu.unicauca.sed.api.domain.EstadoUsuario;
import co.edu.unicauca.sed.api.service.EstadoUsuarioService;
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
@RequestMapping("api/estado-usuario")
public class EstadoUsuarioController {

    private static final Logger logger = LoggerFactory.getLogger(EstadoUsuarioController.class);

    @Autowired
    private EstadoUsuarioService service;

    @PostMapping
    public ResponseEntity<EstadoUsuario> create(@RequestBody EstadoUsuario estadoUsuario) {
        logger.info("Solicitud recibida para crear EstadoUsuario: {}", estadoUsuario);
        return new ResponseEntity<>(service.create(estadoUsuario), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EstadoUsuario> findById(@PathVariable Integer id) {
        logger.info("Solicitud recibida para buscar EstadoUsuario con id: {}", id);
        Optional<EstadoUsuario> estadoUsuario = service.findById(id);
        return estadoUsuario.map(ResponseEntity::ok)
                .orElseThrow(() -> {
                    logger.error("EstadoUsuario no encontrado con id: {}", id);
                    return new RuntimeException("EstadoUsuario no encontrado con id: " + id);
                });
    }

    @GetMapping
    public ResponseEntity<Page<EstadoUsuario>> findAll(Pageable pageable) {
        logger.info("Solicitud recibida para listar EstadoUsuario con paginación");
        return ResponseEntity.ok(service.findAll(pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EstadoUsuario> update(@PathVariable Integer id, @RequestBody EstadoUsuario estadoUsuario) {
        logger.info("Solicitud recibida para actualizar EstadoUsuario con id: {}", id);
        return ResponseEntity.ok(service.update(id, estadoUsuario));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        logger.info("Solicitud recibida para eliminar EstadoUsuario con id: {}", id);
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        logger.error("Error manejado: {}", ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }
}
