package co.edu.unicauca.sed.api.service.evaluacion_docente;

import co.edu.unicauca.sed.api.domain.*;
import co.edu.unicauca.sed.api.dto.ApiResponse;
import co.edu.unicauca.sed.api.dto.EvaluacionDocenteDTO;
import co.edu.unicauca.sed.api.dto.EncuestaPreguntaDTO;
import co.edu.unicauca.sed.api.dto.EvaluacionEstudianteDTO;
import co.edu.unicauca.sed.api.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Implementación del servicio para la evaluación docente.
 */
@Service
@RequiredArgsConstructor
public class EvaluacionServiceImpl implements EvaluacionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EvaluacionServiceImpl.class);

    private final EncuestaRepository encuestaRepository;
    private final EncuestaPreguntaRepository encuestaPreguntaRepository;
    private final EvaluacionEstudianteRepository evaluacionEstudianteRepository;
    private final FuenteRepository fuenteRepository;
    private final PreguntaRepository preguntaRepository;

    @Override
    @Transactional
    public ApiResponse<Void> guardarEvaluacionDocente(EvaluacionDocenteDTO dto) {
        try {
            LOGGER.info("📝 Guardando evaluación docente...");

            // Buscar la fuente
            Fuente fuente = fuenteRepository.findById(dto.getOidFuente())
                    .orElseThrow(() -> new EntityNotFoundException("Fuente no encontrada con ID: " + dto.getOidFuente()));

            // Guardar la encuesta
            Encuesta encuesta = new Encuesta();
            encuesta.setNombre(dto.getEncuesta().getNombre());
            encuesta.setEstado(dto.getEncuesta().getEstado());
            encuesta = encuestaRepository.save(encuesta);
            LOGGER.info("✅ Encuesta guardada con ID: {}", encuesta.getOidEncuesta());

            // Guardar las preguntas de la encuesta
            for (EncuestaPreguntaDTO preguntaDTO : dto.getPreguntas()) {
                EncuestaPregunta encuestaPregunta = new EncuestaPregunta();
                encuestaPregunta.setEncuesta(encuesta);
                encuestaPregunta.setRespuesta(preguntaDTO.getRespuesta());
            
                // Buscar la pregunta en la BD
                Pregunta pregunta = preguntaRepository.findById(preguntaDTO.getOidPregunta())
                    .orElseThrow(() -> new RuntimeException("No se encontró la pregunta con ID: " + preguntaDTO.getOidPregunta()));
            
                encuestaPregunta.setPregunta(pregunta);
            
                // Guardar la entidad en la base de datos
                encuestaPreguntaRepository.save(encuestaPregunta);
            }
            LOGGER.info("✅ Preguntas de la encuesta guardadas correctamente.");

            // Guardar la evaluación del estudiante
            EvaluacionEstudianteDTO evaluacionDTO = dto.getEvaluacionEstudiante();
            EvaluacionEstudiante evaluacionEstudiante = new EvaluacionEstudiante();
            evaluacionEstudiante.setFuente(fuente);
            evaluacionEstudiante.setObservacion(evaluacionDTO.getObservacion());
            evaluacionEstudiante.setFirma(evaluacionDTO.getFirma());
            evaluacionEstudiante = evaluacionEstudianteRepository.save(evaluacionEstudiante);
            LOGGER.info("✅ Evaluación del estudiante guardada con ID: {}", evaluacionEstudiante.getOidEvaluacionEstudiante());

            // Actualizar la calificación de la fuente
            actualizarCalificacionFuente(fuente, evaluacionDTO.getCalificacion());

            return new ApiResponse<>(200, "Evaluación docente guardada correctamente.", null);
        } catch (Exception e) {
            LOGGER.error("❌ Error al guardar la evaluación docente.", e);
            return new ApiResponse<>(500, "Error inesperado al guardar la evaluación docente.", null);
        }
    }

    private void actualizarCalificacionFuente(Fuente fuente, float nuevaCalificacion) {
        // Asignar la nueva calificación enviada desde el frontend
        fuente.setCalificacion(nuevaCalificacion);
        
        // Guardar la actualización en la base de datos
        fuenteRepository.save(fuente);
    
        LOGGER.info("✅ Calificación de la fuente actualizada a: {}", nuevaCalificacion);
    }
    
    
}
