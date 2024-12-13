package co.edu.unicauca.sed.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

/**
 * Manejo global de excepciones en la aplicación.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Maneja excepciones de conversión de tipos (ClassCastException).
     *
     * @param e Excepción capturada.
     * @return Respuesta con mensaje de error y estado HTTP 500.
     */
    @ExceptionHandler(ClassCastException.class)
    public ResponseEntity<Map<String, String>> handleClassCastException(ClassCastException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error de conversión de tipos: " + e.getMessage()));
    }

    /**
     * Maneja excepciones de tipo IllegalArgumentException.
     *
     * @param e Excepción capturada.
     * @return Respuesta con mensaje de error y estado HTTP 400.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", e.getMessage()));
    }

    /**
     * Maneja cualquier otra excepción no capturada específicamente.
     *
     * @param e Excepción capturada.
     * @return Respuesta con mensaje de error y estado HTTP 500.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneralException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Ha ocurrido un error inesperado: " + e.getMessage()));
    }
}
