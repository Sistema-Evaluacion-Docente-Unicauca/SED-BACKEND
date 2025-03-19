package co.edu.unicauca.sed.api.service.evaluacion_docente;

import co.edu.unicauca.sed.api.domain.Encuesta;
import co.edu.unicauca.sed.api.domain.EncuestaRespuesta;
import co.edu.unicauca.sed.api.domain.EstadoEtapaDesarrollo;
import co.edu.unicauca.sed.api.domain.EstadoFuente;
import co.edu.unicauca.sed.api.domain.EvaluacionEstudiante;
import co.edu.unicauca.sed.api.domain.Fuente;
import co.edu.unicauca.sed.api.domain.Pregunta;
import co.edu.unicauca.sed.api.dto.ApiResponse;
import co.edu.unicauca.sed.api.dto.EncuestaPreguntaDTO;
import co.edu.unicauca.sed.api.dto.EvaluacionDocenteDTO;
import co.edu.unicauca.sed.api.repository.EncuestaRespuestaRepository;
import co.edu.unicauca.sed.api.repository.EstadoEtapaDesarrolloRepository;
import co.edu.unicauca.sed.api.repository.EvaluacionEstudianteRepository;
import co.edu.unicauca.sed.api.repository.FuenteRepository;
import co.edu.unicauca.sed.api.repository.PreguntaRepository;
import co.edu.unicauca.sed.api.service.fuente.FuenteBusinessServiceImpl;
import co.edu.unicauca.sed.api.service.fuente.FuenteService;
import jakarta.persistence.EntityNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Service
public class EvaluacionEstudianteServiceImpl implements EvaluacionEstudianteService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EvaluacionEstudianteServiceImpl.class);

    @Autowired
    private EvaluacionEstudianteRepository evaluacionEstudianteRepository;

    @Autowired
    private FuenteRepository fuenteRepository;

    @Autowired
    private EncuestaService encuestaService;

    @Autowired
    private EstadoEtapaDesarrolloRepository estadoEtapaDesarrolloRepository;

    @Autowired
    private FuenteService fuenteService;

    @Autowired
    private EncuestaRespuestaRepository encuestaRespuestaRepository;

    @Autowired
    private PreguntaRepository preguntaRepository;

    @Autowired
    private FuenteBusinessServiceImpl fuenteBussines;

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<Page<EvaluacionEstudiante>> buscarTodos(Pageable pageable) {
        try {
            LOGGER.info("📄 Listando Evaluaciones de Estudiantes con paginación.");
            Page<EvaluacionEstudiante> evaluaciones = evaluacionEstudianteRepository.findAll(pageable);
            return new ApiResponse<>(200, "Evaluaciones de estudiantes obtenidas correctamente.", evaluaciones);
        } catch (Exception e) {
            LOGGER.error("❌ Error al listar Evaluaciones de Estudiantes", e);
            return new ApiResponse<>(500, "Error al listar evaluaciones: " + e.getMessage(), null);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<EvaluacionEstudiante> buscarPorId(Integer oid) {
        try {
            LOGGER.info("🔍 Buscando EvaluacionEstudiante con ID: {}", oid);
            Optional<EvaluacionEstudiante> evaluacion = evaluacionEstudianteRepository.findById(oid);
            return evaluacion.map(value -> new ApiResponse<>(200, "Evaluación de estudiante encontrada.", value))
                    .orElseGet(() -> new ApiResponse<>(404, "Evaluación de estudiante no encontrada.", null));
        } catch (Exception e) {
            LOGGER.error("❌ Error al buscar EvaluacionEstudiante con ID: {}", oid, e);
            return new ApiResponse<>(500, "Error al buscar la evaluación: " + e.getMessage(), null);
        }
    }

    @Override
    @Transactional
    public ApiResponse<EvaluacionEstudiante> guardar(EvaluacionEstudiante evaluacionEstudiante) {
        try {
            LOGGER.info("✅ Guardando EvaluacionEstudiante.");
            if (evaluacionEstudiante.getObservacion() != null) {
                evaluacionEstudiante.setObservacion(evaluacionEstudiante.getObservacion().toUpperCase());
            }
            EvaluacionEstudiante resultado = evaluacionEstudianteRepository.save(evaluacionEstudiante);
            return new ApiResponse<>(201, "Evaluación de estudiante guardada exitosamente.", resultado);
        } catch (Exception e) {
            LOGGER.error("❌ Error al guardar EvaluacionEstudiante", e);
            return new ApiResponse<>(500, "Error al guardar la evaluación: " + e.getMessage(), null);
        }
    }

    @Override
    @Transactional
    public ApiResponse<Void> eliminar(Integer oid) {
        try {
            LOGGER.info("🗑️ Eliminando EvaluacionEstudiante con ID: {}", oid);
            if (!evaluacionEstudianteRepository.existsById(oid)) {
                return new ApiResponse<>(404, "Evaluación de estudiante no encontrada.", null);
            }
            evaluacionEstudianteRepository.deleteById(oid);
            return new ApiResponse<>(200, "Evaluación de estudiante eliminada correctamente.", null);
        } catch (Exception e) {
            LOGGER.error("❌ Error al eliminar EvaluacionEstudiante con ID: {}", oid, e);
            return new ApiResponse<>(500, "Error al eliminar la evaluación: " + e.getMessage(), null);
        }
    }

    @Override
    @Transactional
    public ApiResponse<Void> guardarEvaluacionDocente(EvaluacionDocenteDTO dto, MultipartFile documentoFuente, MultipartFile firmaEstudiante) {
        try {

            // Obtener la fuente
            Fuente fuente = obtenerFuente(dto.getOidFuente());

            // Guardar firma si existe
            if (firmaEstudiante != null && !firmaEstudiante.isEmpty()) {
                String prefijo = "firma";
                String rutaFirma = fuenteService.guardarDocumentoFuente(fuente, firmaEstudiante, prefijo);
                rutaFirma = (Paths.get(rutaFirma).getFileName().toString());
                dto.setFirma(rutaFirma);
            }

            // Guardar o actualizar EvaluacionEstudiante
            EvaluacionEstudiante evaluacionEstudiante = guardarEvaluacionEstudiante(dto, fuente);

            // Guardar o actualizar Encuesta
            Encuesta encuesta = encuestaService.guardarEncuesta(dto, evaluacionEstudiante);

            // 🔄 Guardar respuestas y calcular la calificación
            float calificacionFinal = guardarRespuestasYCalcularNota(dto.getPreguntas(), encuesta);

            if (documentoFuente != null && !documentoFuente.isEmpty()) {
                String prefijo = "fuente";
                String rutaDocumento = fuenteService.guardarDocumentoFuente(fuente, documentoFuente, prefijo);
                
                fuente.setRutaDocumentoFuente(rutaDocumento);
            
                // ✅ Convertir String a Path y obtener el nombre del archivo
                fuente.setNombreDocumentoFuente(Paths.get(rutaDocumento).getFileName().toString());
            }
            
            // 🔄 Actualizar la calificación de la fuente
            actualizarFuente(fuente, calificacionFinal, dto.getTipoCalificacion(), dto.getObservacion());

            return new ApiResponse<>(200, "Evaluación docente guardada correctamente.", null);
        } catch (Exception e) {
            LOGGER.error("❌ Error al guardar la evaluación docente.", e);
            return new ApiResponse<>(500, "Error inesperado al guardar la evaluación docente.", null);
        }
    }

    private Fuente obtenerFuente(Integer oidFuente) {
        return fuenteRepository.findById(oidFuente)
                .orElseThrow(() -> new EntityNotFoundException("Fuente no encontrada con ID: " + oidFuente));
    }

    private EvaluacionEstudiante guardarEvaluacionEstudiante(EvaluacionDocenteDTO dto, Fuente fuente) {
        EvaluacionEstudiante evaluacionEstudiante = evaluacionEstudianteRepository.findByFuente(fuente)
                .orElse(new EvaluacionEstudiante());

        evaluacionEstudiante.setFuente(fuente);
        evaluacionEstudiante.setObservacion(dto.getEvaluacionEstudiante().getObservacion());
        evaluacionEstudiante.setFirma(dto.getFirma());

        EstadoEtapaDesarrollo estado = estadoEtapaDesarrolloRepository.findById(dto.getOidEstadoEtapaDesarrollo())
                .orElseThrow(() -> new EntityNotFoundException(
                        "EstadoEtapaDesarrollo no encontrado con ID: " + dto.getOidEstadoEtapaDesarrollo()));

        evaluacionEstudiante.setEstadoEtapaDesarrollo(estado);
        return evaluacionEstudianteRepository.save(evaluacionEstudiante);
    }

    private float guardarRespuestasYCalcularNota(List<EncuestaPreguntaDTO> preguntasDTO, Encuesta encuesta) {
        float calificacionTotal = 0;
        float sumaPesos = 0;

        for (EncuestaPreguntaDTO preguntaDTO : preguntasDTO) {
            Pregunta pregunta = preguntaRepository.findById(preguntaDTO.getOidPregunta())
                .orElseThrow(() -> new EntityNotFoundException(
                    "Pregunta con ID " + preguntaDTO.getOidPregunta() + " no encontrada."));

            EncuestaRespuesta encuestaRespuesta = encuestaRespuestaRepository.findByEncuestaAndPregunta(encuesta, pregunta).orElse(new EncuestaRespuesta());

            encuestaRespuesta.setEncuesta(encuesta);
            encuestaRespuesta.setPregunta(pregunta);
            encuestaRespuesta.setRespuesta(preguntaDTO.getRespuesta());
            encuestaRespuestaRepository.save(encuestaRespuesta);

            // Cálculo de la calificación basada en porcentaje de importancia
            float peso = pregunta.getPorcentajeImportancia();
            float valorRespuesta = Float.parseFloat(preguntaDTO.getRespuesta());

            calificacionTotal += valorRespuesta * peso;
            sumaPesos += peso;
        }

        return (sumaPesos > 0) ? calificacionTotal / sumaPesos : 0;
    }

    private void actualizarFuente(Fuente fuente, float nuevaCalificacion, String tipoCalificacion, String observacion) {
        fuente.setCalificacion(nuevaCalificacion);
        fuente.setTipoCalificacion(tipoCalificacion.toUpperCase());
        EstadoFuente estadoFuente = fuenteBussines.determinarEstadoFuente(fuente);
        fuente.setEstadoFuente(estadoFuente);
        fuente.setObservacion(observacion.toUpperCase());
        fuenteRepository.save(fuente);
    }
}
