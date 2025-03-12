package co.edu.unicauca.sed.api.controller;

import co.edu.unicauca.sed.api.domain.EstadoActividad;
import co.edu.unicauca.sed.api.dto.ApiResponse;
import co.edu.unicauca.sed.api.service.actividad.EstadoActividadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/estado-actividad")
public class EstadoActividadController {

    private static final Logger logger = LoggerFactory.getLogger(EstadoActividadController.class);
    private final EstadoActividadService service;

    public EstadoActividadController(EstadoActividadService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<EstadoActividad>> create(@RequestBody EstadoActividad estadoActividad) {
        logger.info("Solicitud recibida para crear EstadoActividad: {}", estadoActividad);
        return service.guardar(estadoActividad);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EstadoActividad>> findById(@PathVariable Integer id) {
        logger.info("Solicitud recibida para buscar EstadoActividad con id: {}", id);
        return service.buscarPorOid(id);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<EstadoActividad>>> findAll(Pageable pageable) {
        logger.info("Solicitud recibida para listar EstadosActividad con paginación");
        return service.obtenerTodos(pageable);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<EstadoActividad>> update(@PathVariable Integer id, @RequestBody EstadoActividad estadoActividad) {
        logger.info("Solicitud recibida para actualizar EstadoActividad con id: {}", id);
        return service.actualizar(id, estadoActividad);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Integer id) {
        logger.info("Solicitud recibida para eliminar EstadoActividad con id: {}", id);
        return service.eliminar(id);
    }
}
