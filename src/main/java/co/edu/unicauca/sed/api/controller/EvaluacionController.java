package co.edu.unicauca.sed.api.controller;

import co.edu.unicauca.sed.api.dto.ApiResponse;
import co.edu.unicauca.sed.api.dto.EvaluacionDocenteDTO;
import co.edu.unicauca.sed.api.service.evaluacion_docente.EvaluacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador para gestionar la evaluación docente.
 */
@RestController
@RequestMapping("api/evaluacion")
@RequiredArgsConstructor
public class EvaluacionController {

    private final EvaluacionService evaluacionService;

    /**
     * Endpoint para guardar una evaluación docente completa, incluyendo la calificación de la fuente.
     *
     * @param dto Datos de la evaluación.
     * @return Respuesta indicando si la operación fue exitosa.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> guardarEvaluacionDocente(@RequestBody EvaluacionDocenteDTO dto) {
        return ResponseEntity.ok(evaluacionService.guardarEvaluacionDocente(dto));
    }
}
