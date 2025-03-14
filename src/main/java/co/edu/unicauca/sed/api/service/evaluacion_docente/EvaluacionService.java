package co.edu.unicauca.sed.api.service.evaluacion_docente;

import co.edu.unicauca.sed.api.dto.ApiResponse;
import co.edu.unicauca.sed.api.dto.EvaluacionDocenteDTO;
import org.springframework.stereotype.Service;

/**
 * Interfaz para gestionar la evaluación docente.
 */
@Service
public interface EvaluacionService {

    /**
     * Guarda una evaluación docente completa en la base de datos.
     *
     * @param dto Datos de la evaluación.
     * @return ApiResponse indicando el resultado de la operación.
     */
    ApiResponse<Void> guardarEvaluacionDocente(EvaluacionDocenteDTO dto);
}
