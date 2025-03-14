package co.edu.unicauca.sed.api.controller;

import co.edu.unicauca.sed.api.domain.Pregunta;
import co.edu.unicauca.sed.api.dto.ApiResponse;
import co.edu.unicauca.sed.api.service.encuesta.PreguntaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.springframework.data.domain.Page;

/**
 * Controlador para la gestión de preguntas.
 */
@RestController
@RequestMapping("api/pregunta")
@RequiredArgsConstructor
public class PreguntaController {

    private final PreguntaService preguntaService;

    /**
     * Obtiene todas las preguntas registradas.
     *
     * @return Lista de preguntas.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<Pregunta>>> obtenerTodos(Pageable pageable) {
        return ResponseEntity.ok(preguntaService.obtenerTodos(pageable));
    }

    /**
     * Busca una pregunta por su identificador único (OID).
     *
     * @param oid Identificador de la pregunta.
     * @return Pregunta encontrada o error si no existe.
     */
    @GetMapping("/{oid}")
    public ResponseEntity<ApiResponse<Pregunta>> obtenerPorOid(@PathVariable Integer oid) {
        return ResponseEntity.ok(preguntaService.buscarPorOid(oid));
    }

    /**
     * Guarda una nueva pregunta en la base de datos.
     *
     * @param pregunta Datos de la pregunta a guardar.
     * @return Pregunta guardada o error si ocurre una excepción.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Pregunta>> guardar(@RequestBody Pregunta pregunta) {
        return ResponseEntity.ok(preguntaService.guardar(pregunta));
    }

    /**
     * Guarda múltiples preguntas en la base de datos.
     *
     * @param preguntas Lista de preguntas a guardar.
     * @return Lista de preguntas guardadas o error si ocurre una excepción.
     */
    @PostMapping("guardarTodas")
    public ResponseEntity<ApiResponse<List<Pregunta>>> guardarTodas(@RequestBody List<Pregunta> preguntas) {
        return ResponseEntity.ok(preguntaService.guardarTodas(preguntas));
    }

    /**
     * Elimina una pregunta por su identificador.
     *
     * @param oid Identificador de la pregunta a eliminar.
     * @return Respuesta indicando el resultado de la eliminación.
     */
    @DeleteMapping("/{oid}")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Integer oid) {
        return ResponseEntity.ok(preguntaService.eliminar(oid));
    }
}
