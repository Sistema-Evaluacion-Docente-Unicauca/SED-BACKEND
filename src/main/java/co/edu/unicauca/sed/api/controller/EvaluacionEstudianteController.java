package co.edu.unicauca.sed.api.controller;

import co.edu.unicauca.sed.api.domain.EvaluacionEstudiante;
import co.edu.unicauca.sed.api.dto.ApiResponse;
import co.edu.unicauca.sed.api.dto.EvaluacionDocenteDTO;
import co.edu.unicauca.sed.api.service.evaluacion_docente.EvaluacionEstudianteService;
import co.edu.unicauca.sed.api.service.fuente.FuenteIntegrationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * Controlador para la gestión de evaluaciones de estudiantes.
 */
@RestController
@RequestMapping("api/evaluacion-estudiante")
@RequiredArgsConstructor
public class EvaluacionEstudianteController {

    private static final Logger LOGGER = LoggerFactory.getLogger(EvaluacionEstudianteController.class);
    private final EvaluacionEstudianteService evaluacionEstudianteService;

    @Autowired
    private FuenteIntegrationService integrationService;

    /**
     * Recupera todas las evaluaciones de estudiantes con paginación.
     *
     * @param pageable Objeto de paginación.
     * @return Página de evaluaciones de estudiantes.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<EvaluacionEstudiante>>> buscarTodos(Pageable pageable) {
        LOGGER.info("📌 Buscando todas las evaluaciones de estudiantes...");
        return ResponseEntity.ok(evaluacionEstudianteService.buscarTodos(pageable));
    }

    /**
     * Recupera una evaluación específica por su ID.
     *
     * @param oid ID de la evaluación.
     * @return Evaluación encontrada o mensaje de error si no existe.
     */
    @GetMapping("/{oid}")
    public ResponseEntity<ApiResponse<EvaluacionEstudiante>> buscarPorId(@PathVariable Integer oid) {
        LOGGER.info("📌 Buscando evaluación de estudiante con ID: {}", oid);
        return ResponseEntity.ok(evaluacionEstudianteService.buscarPorId(oid));
    }

    /**
     * Guarda una evaluación de estudiante.
     *
     * @param dto Datos de la evaluación.
     * @return Confirmación de guardado.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> guardarEvaluacionDocente(
            @RequestParam("data") String evaluacionJson,
            @RequestParam(value = "documentoFuente", required = false) MultipartFile documentoFuente,
            @RequestParam(value = "firmaEstudiante", required = false) MultipartFile firmaEstudiante) {
        // Convertir JSON en DTO
        EvaluacionDocenteDTO dto = integrationService.convertirJsonAEvaluacion(evaluacionJson);

        // 🔄 Guardar Evaluación con documentos
        ApiResponse<Void> response = evaluacionEstudianteService.guardarEvaluacionDocente(dto, documentoFuente, firmaEstudiante);

        return ResponseEntity.status(response.getCodigo()).body(response);
    }

    /**
     * Elimina una evaluación de estudiante por su ID.
     *
     * @param oid ID de la evaluación a eliminar.
     * @return Confirmación de eliminación o mensaje de error si ocurre un problema.
     */
    @DeleteMapping("/{oid}")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Integer oid) {
        LOGGER.info("📌 Eliminando evaluación de estudiante con ID: {}", oid);
        return ResponseEntity.ok(evaluacionEstudianteService.eliminar(oid));
    }

    @GetMapping("/fuente/{oidFuente}")
    public ResponseEntity<ApiResponse<Object>> buscarPorFuente(@PathVariable Integer oidFuente) {
        LOGGER.info("📌 Buscando evaluación de estudiante por fuente con ID: {}", oidFuente);
        return ResponseEntity.ok(evaluacionEstudianteService.buscarPorFuente(oidFuente));
    }
}
