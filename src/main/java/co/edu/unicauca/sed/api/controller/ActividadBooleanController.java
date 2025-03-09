package co.edu.unicauca.sed.api.controller;

import co.edu.unicauca.sed.api.domain.ActividadBoolean;
import co.edu.unicauca.sed.api.dto.ApiResponse;
import co.edu.unicauca.sed.api.service.ActividadBooleanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Page;

/**
 * Controlador REST para gestionar ACTIVIDADBOOLEAN.
 */
@RestController
@RequestMapping("/api/actividadboolean")
@RequiredArgsConstructor
public class ActividadBooleanController {

    private final ActividadBooleanService actividadBooleanService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ActividadBoolean>>> listar(@RequestParam(defaultValue = "0") int page,
                                                                      @RequestParam(defaultValue = "10") int size) {
        return actividadBooleanService.obtenerTodos(page, size);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ActividadBoolean>> obtenerPorId(@PathVariable Integer id) {
        return actividadBooleanService.obtenerPorId(id);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ActividadBoolean>> crear(@RequestBody ActividadBoolean actividadBoolean) {
        return actividadBooleanService.crear(actividadBoolean);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ActividadBoolean>> actualizar(@PathVariable Integer id, @RequestBody ActividadBoolean actividadBoolean) {
        return actividadBooleanService.actualizar(id, actividadBoolean);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Integer id) {
        return actividadBooleanService.eliminar(id);
    }
}
