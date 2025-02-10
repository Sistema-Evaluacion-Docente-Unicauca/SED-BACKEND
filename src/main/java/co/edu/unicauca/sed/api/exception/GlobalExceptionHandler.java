package co.edu.unicauca.sed.api.exception;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import co.edu.unicauca.sed.api.dto.ApiResponse;
import jakarta.persistence.EntityNotFoundException;

import java.util.HashMap;
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
     * @return Respuesta con código de error y mensaje.
     */
    @ExceptionHandler(ClassCastException.class)
    public ResponseEntity<Map<String, Object>> handleClassCastException(ClassCastException e) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Error de conversión de tipos: " + e.getMessage());
    }

    /**
     * Maneja excepciones de tipo IllegalArgumentException.
     *
     * @param e Excepción capturada.
     * @return Respuesta con código de error y mensaje.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(IllegalArgumentException e) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    /**
     * Maneja errores de integridad de datos en la base de datos (ej. valores NULL
     * en columnas NOT NULL).
     *
     * @param e Excepción capturada.
     * @return Respuesta con código de error y mensaje.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDatabaseConstraintViolation(DataIntegrityViolationException e) {
        String errorMessage = "Error de integridad de datos.";

        // Analiza el mensaje para detectar errores de campos NULL o valores duplicados
        if (e.getCause() != null && e.getCause().getMessage() != null) {
            String detailedMessage = e.getCause().getMessage().toLowerCase();
            if (detailedMessage.contains("null value in column")) {
                errorMessage = "Uno o más campos obligatorios están vacíos.";
            } else if (detailedMessage.contains("violates unique constraint")) {
                errorMessage = "Ya existe un registro con este valor único.";
            }
        }

        return buildErrorResponse(HttpStatus.BAD_REQUEST, errorMessage);
    }

    /**
     * Maneja cualquier otra excepción no capturada específicamente.
     *
     * @param e Excepción capturada.
     * @return Respuesta con código de error y mensaje.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneralException(Exception e) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Ha ocurrido un error inesperado.");
    }

    /**
     * Método utilitario para construir la respuesta de error estándar.
     *
     * @param status  Código de estado HTTP.
     * @param mensaje Mensaje de error.
     * @return ResponseEntity con JSON estructurado.
     */
    private ResponseEntity<Map<String, Object>> buildErrorResponse(HttpStatus status, String mensaje) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("codigo", status.value());
        errorResponse.put("mensaje", mensaje);
        return ResponseEntity.status(status).body(errorResponse);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalStateException(IllegalStateException ex) {
        ApiResponse<Void> errorResponse = new ApiResponse<>(404, ex.getMessage(), null);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(InvalidDataAccessApiUsageException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataAccessException(InvalidDataAccessApiUsageException ex) {
        ApiResponse<Void> errorResponse = new ApiResponse<>(400, "Error en la consulta de datos: " + ex.getMessage(), null);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleEntityNotFoundException(EntityNotFoundException ex) {
        ApiResponse<Void> errorResponse = new ApiResponse<>(404, ex.getMessage(), null);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(DataAccessException.class)
public ResponseEntity<ApiResponse<Void>> handleDatabaseException(DataAccessException ex) {
    ApiResponse<Void> errorResponse = new ApiResponse<>(404, ex.getMessage(), null);
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
}

}
