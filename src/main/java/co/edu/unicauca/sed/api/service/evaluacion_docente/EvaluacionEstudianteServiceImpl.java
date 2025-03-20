package co.edu.unicauca.sed.api.service.evaluacion_docente;

import co.edu.unicauca.sed.api.domain.Encuesta;
import co.edu.unicauca.sed.api.domain.EncuestaRespuesta;
import co.edu.unicauca.sed.api.domain.EstadoEtapaDesarrollo;
import co.edu.unicauca.sed.api.domain.EstadoFuente;
import co.edu.unicauca.sed.api.domain.EvaluacionEstudiante;
import co.edu.unicauca.sed.api.domain.Fuente;
import co.edu.unicauca.sed.api.domain.Pregunta;
import co.edu.unicauca.sed.api.domain.Usuario;
import co.edu.unicauca.sed.api.dto.ApiResponse;
import co.edu.unicauca.sed.api.dto.EncuestaPreguntaDTO;
import co.edu.unicauca.sed.api.dto.EvaluacionDocenteDTO;
import co.edu.unicauca.sed.api.repository.EncuestaRepository;
import co.edu.unicauca.sed.api.repository.EncuestaRespuestaRepository;
import co.edu.unicauca.sed.api.repository.EstadoEtapaDesarrolloRepository;
import co.edu.unicauca.sed.api.repository.EvaluacionEstudianteRepository;
import co.edu.unicauca.sed.api.repository.FuenteRepository;
import co.edu.unicauca.sed.api.repository.PreguntaRepository;
import co.edu.unicauca.sed.api.service.fuente.FuenteBusinessServiceImpl;
import co.edu.unicauca.sed.api.service.fuente.FuenteService;
import co.edu.unicauca.sed.api.service.notificacion.NotificacionDocumentoService;
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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
    private EncuestaRepository encuestaRepository;

    @Autowired
    private FuenteBusinessServiceImpl fuenteBussines;

    @Autowired
    private NotificacionDocumentoService notificacionDocumentoService;

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
            String mensajeTipoFuente = "Fuente 2";
            Usuario evaluado = fuente.getActividad().getProceso().getEvaluado();
            Usuario evaluador = fuente.getActividad().getProceso().getEvaluador();
            notificacionDocumentoService.notificarEvaluado(mensajeTipoFuente, evaluador, evaluado);
            return new ApiResponse<>(200, "Evaluación docente guardada correctamente.", null);
        } catch (Exception e) {
            LOGGER.error("❌ Error al guardar la evaluación docente.", e);
            return new ApiResponse<>(500, "Error inesperado al guardar la evaluación docente.", null);
        }
    }

    private Fuente obtenerFuente(Integer oidFuente) {
        return fuenteRepository.findById(oidFuente).orElseThrow(() -> new EntityNotFoundException("Fuente no encontrada con ID: " + oidFuente));
    }

    private EvaluacionEstudiante guardarEvaluacionEstudiante(EvaluacionDocenteDTO dto, Fuente fuente) {
        EvaluacionEstudiante evaluacionEstudiante = evaluacionEstudianteRepository.findByFuente(fuente).orElse(new EvaluacionEstudiante());

        evaluacionEstudiante.setFuente(fuente);
        evaluacionEstudiante.setObservacion(dto.getObservacion().toUpperCase());
        evaluacionEstudiante.setFirma(dto.getFirma());

        EstadoEtapaDesarrollo estado = estadoEtapaDesarrolloRepository.findById(dto.getOidEstadoEtapaDesarrollo())
            .orElseThrow(() -> new EntityNotFoundException("EstadoEtapaDesarrollo no encontrado con ID: " + dto.getOidEstadoEtapaDesarrollo()));

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

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<Object> buscarPorFuente(Integer oidFuente) {
        try {
            LOGGER.info("🔍 Buscando Evaluación de Estudiante por Fuente con ID: {}", oidFuente);

            Fuente fuente = obtenerFuente(oidFuente);
            EvaluacionEstudiante evaluacionEstudiante = obtenerEvaluacionEstudiantePorFuente(fuente, oidFuente);
            Encuesta encuesta = obtenerEncuestaPorEvaluacion(evaluacionEstudiante);
            List<Map<String, Object>> preguntas = obtenerPreguntasDeEncuesta(encuesta);

            Map<String, Object> resultado = construirResultado(fuente, evaluacionEstudiante, encuesta, preguntas);

            return new ApiResponse<>(200, "Encuesta encontrada correctamente.", resultado);
        } catch (EntityNotFoundException e) {
            LOGGER.warn("⚠️ {}", e.getMessage());
            return new ApiResponse<>(404, e.getMessage(), null);
        } catch (Exception e) {
            LOGGER.error("❌ Error al buscar Encuesta por Fuente", e);
            return new ApiResponse<>(500, "Error inesperado al buscar la encuesta.", null);
        }
    }

    private EvaluacionEstudiante obtenerEvaluacionEstudiantePorFuente(Fuente fuente, Integer oidFuente) {
        return evaluacionEstudianteRepository.findByFuente(fuente)
                .orElseThrow(() -> new EntityNotFoundException(
                        "No se encontró Evaluación de Estudiante para la fuente con ID: " + oidFuente));
    }

    private Encuesta obtenerEncuestaPorEvaluacion(EvaluacionEstudiante evaluacionEstudiante) {
        return encuestaRepository.findByEvaluacionEstudiante(evaluacionEstudiante)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró Encuesta para la evaluación con ID: "
                        + evaluacionEstudiante.getOidEvaluacionEstudiante()));
    }

    private List<Map<String, Object>> obtenerPreguntasDeEncuesta(Encuesta encuesta) {
        List<EncuestaRespuesta> respuestas = encuestaRespuestaRepository.findByEncuesta(encuesta);
        return respuestas.stream().map(respuesta -> {
            Map<String, Object> preguntaMap = new HashMap<>();
            preguntaMap.put("oidPregunta", respuesta.getPregunta().getOidPregunta());
            preguntaMap.put("respuesta", Integer.parseInt(respuesta.getRespuesta())); // Convertir a número
            return preguntaMap;
        }).collect(Collectors.toList());
    }

    private Map<String, Object> construirResultado(Fuente fuente, EvaluacionEstudiante evaluacionEstudiante, Encuesta encuesta, List<Map<String, Object>> preguntas) {
        Map<String, Object> resultado = new LinkedHashMap<>();
    
        resultado.put("oidFuente", fuente.getOidFuente());
        resultado.put("evaluado", construirUsuarioMap(fuente.getActividad().getProceso().getEvaluado()));
        resultado.put("evaluador", construirUsuarioMap(fuente.getActividad().getProceso().getEvaluador()));
        resultado.put("observacion", fuente.getObservacion());
        resultado.put("nombreArchivo", fuente.getNombreDocumentoFuente());
        resultado.put("tipoCalificacion", obtenerTipoCalificacion(evaluacionEstudiante));
        resultado.put("encuesta", construirEncuesta(encuesta));
        resultado.put("estadoEtapaDesarrollo", construirEstadoEtapaDesarrollo(evaluacionEstudiante));
        resultado.put("fechaCreacion", fuente.getFechaCreacion());
        resultado.put("fechaActualizacion", fuente.getFechaActualizacion());
        resultado.put("preguntas", preguntas);
    
        return resultado;
    }
    
    private Map<String, Object> construirUsuarioMap(Usuario usuario) {
        Map<String, Object> usuarioMap = new LinkedHashMap<>();
        usuarioMap.put("oidUsuario", usuario.getOidUsuario());
        usuarioMap.put("nombreCompleto", usuario.getNombres() + " " + usuario.getApellidos());
        return usuarioMap;
    }
    
    // Obtiene el tipo de calificación
    private String obtenerTipoCalificacion(EvaluacionEstudiante evaluacionEstudiante) {
        return evaluacionEstudiante.getFuente().getTipoCalificacion();
    }
    
    // Construye el mapa de Encuesta
    private Map<String, Object> construirEncuesta(Encuesta encuesta) {
        Map<String, Object> encuestaMap = new LinkedHashMap<>();
        encuestaMap.put("nombre", encuesta.getNombre());
        return encuestaMap;
    }
    
    // Construye el mapa de EstadoEtapaDesarrollo
    private Map<String, Object> construirEstadoEtapaDesarrollo(EvaluacionEstudiante evaluacionEstudiante) {
        Map<String, Object> estadoEtapaDesarrolloMap = new LinkedHashMap<>();
        estadoEtapaDesarrolloMap.put("oidEstadoEtapaDesarrollo", evaluacionEstudiante.getEstadoEtapaDesarrollo().getOidEstadoEtapaDesarrollo());
        estadoEtapaDesarrolloMap.put("nombre", evaluacionEstudiante.getEstadoEtapaDesarrollo().getNombre());
        return estadoEtapaDesarrolloMap;
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
