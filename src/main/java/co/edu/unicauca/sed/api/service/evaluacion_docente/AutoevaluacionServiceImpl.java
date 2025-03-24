package co.edu.unicauca.sed.api.service.evaluacion_docente;

import co.edu.unicauca.sed.api.domain.*;
import co.edu.unicauca.sed.api.dto.*;
import co.edu.unicauca.sed.api.mapper.EvaluacionMapperUtil;
import co.edu.unicauca.sed.api.repository.*;
import co.edu.unicauca.sed.api.service.fuente.FuenteBusinessService;
import co.edu.unicauca.sed.api.service.fuente.FuenteService;
import co.edu.unicauca.sed.api.service.notificacion.NotificacionDocumentoService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AutoevaluacionServiceImpl implements AutoevaluacionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AutoevaluacionServiceImpl.class);

    private static final String MSG_AUTOEVALUACION_OK = "Autoevaluación guardada correctamente.";
    private static final String MSG_AUTOEVALUACION_ERROR = "Error inesperado al guardar la autoevaluación.";
    private static final String MSG_BUSQUEDA_ERROR = "Error inesperado al buscar la autoevaluación.";
    private static final String MSG_NO_EVALUACION = "No se encontró autoevaluación para la fuente con ID: ";
    private static final String PREFIJO_FIRMA = "firma";
    private static final String PREFIJO_SCREENSHOT = "screenshot";
    private static final String PREFIJO_FUENTE_1 = "fuente-1";
    private final AutoevaluacionRepository autoevaluacionRepository;
    private final AutoevaluacionOdsRepository autoevaluacionOdsRepository;
    private final LeccionAprendidaRepository leccionAprendidaRepository;
    private final OportunidadMejoraRepository oportunidadMejoraRepository;
    private final FuenteService fuenteService;
    private final FuenteBusinessService fuenteBussines;
    private final LeccionAprendidaService leccionAprendidaService;
    private final OportunidadMejoraService oportunidadMejoraService;
    private final NotificacionDocumentoService notificacionDocumentoService;
    private final AutoevaluacionOdsService autoevaluacionOdsService;

    @Override
    @Transactional
    public ApiResponse<Void> guardarAutoevaluacion(
            AutoevaluacionDTO dto,
            MultipartFile firma,
            MultipartFile screenshotSimca,
            MultipartFile documentoAutoevaluacion,
            Map<String, MultipartFile> archivosOds) {
        try {
            LOGGER.info("📥 Procesando autoevaluación para la fuente ID: {}", dto.getOidFuente());

            Fuente fuente = fuenteService.obtenerFuente(dto.getOidFuente());

            // Verificar si ya existe autoevaluación
            Optional<Autoevaluacion> autoevaluacionOpt = autoevaluacionRepository.findByFuente(fuente);
            Autoevaluacion autoevaluacion = autoevaluacionOpt.orElseGet(() -> {
                Autoevaluacion nueva = new Autoevaluacion(); nueva.setFuente(fuente);
                return nueva;
            });

            // Guardar archivos principales
            String rutaFirma = fuenteService.guardarDocumentoFuente(fuente,firma, PREFIJO_FIRMA);
            if (rutaFirma != null) 
                autoevaluacion.setRutaDocumentoFirma(rutaFirma);

            String rutaScreenshot = fuenteService.guardarDocumentoFuente(fuente, screenshotSimca, PREFIJO_SCREENSHOT);
            if (rutaScreenshot != null)
                autoevaluacion.setRutaDocumentoSc(rutaScreenshot);

            // Guardar la autoevaluación (nuevo o actualizada)
            autoevaluacion = autoevaluacionRepository.save(autoevaluacion);

            Map<Integer, MultipartFile> archivosOdsMap = filtrarArchivosOds(archivosOds);

            // Guardar ODS con sus evidencias
            autoevaluacionOdsService.guardarOds(dto.getOdsSeleccionados(), autoevaluacion, archivosOdsMap, fuente);

            // Guardar lecciones y mejoras
            leccionAprendidaService.guardar(dto.getLeccionesAprendidas(), autoevaluacion);

            oportunidadMejoraService.guardar(dto.getOportunidadesMejora(), autoevaluacion);

            // Guardar documento de notas
            String rutaNotas = fuenteService.guardarDocumentoFuente(fuente, documentoAutoevaluacion, PREFIJO_FUENTE_1);
            if (rutaNotas != null) {
                fuente.setRutaDocumentoFuente(rutaNotas);
                String nombreArchivo = Paths.get(rutaNotas).getFileName().toString();
                fuente.setNombreDocumentoFuente(nombreArchivo);
            }

            // Actualizar información de la fuente
            fuenteBussines.actualizarFuente(fuente, dto.getCalificacion(), dto.getTipoCalificacion(), dto.getObservacion());
            String mensajeTipoFuente = "Fuente 1";
            Usuario evaluado = fuente.getActividad().getProceso().getEvaluado();
            Usuario evaluador = fuente.getActividad().getProceso().getEvaluador();
            notificacionDocumentoService.notificarEvaluado(mensajeTipoFuente, evaluador, evaluado);
            LOGGER.info("✅ Autoevaluación {} correctamente.", autoevaluacionOpt.isPresent() ? "actualizada" : "creada");
            return new ApiResponse<>(200, MSG_AUTOEVALUACION_OK, null);
        } catch (Exception e) {
            LOGGER.error("❌ " + MSG_AUTOEVALUACION_ERROR, e);
            return new ApiResponse<>(500, MSG_AUTOEVALUACION_ERROR, null);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<Object> obtenerAutoevaluacion(Integer oidFuente) {
        try {
            LOGGER.info("🔍 Buscando autoevaluación por fuente ID: {}", oidFuente);

            Fuente fuente = fuenteService.obtenerFuente(oidFuente);
            Autoevaluacion autoevaluacion = autoevaluacionRepository.findByFuente(fuente)
                    .orElseThrow(() -> new NoSuchElementException(MSG_NO_EVALUACION + oidFuente));
            Map<String, Object> resultado = new LinkedHashMap<>();
            Map<String, Object> resumen = EvaluacionMapperUtil.construirResumenEvaluacion(
                fuente,
                fuente.getTipoCalificacion(),
                fuente.getObservacion(),
                fuente.getNombreDocumentoFuente());
            resultado.put("Fuente", resumen);
            resultado.put("firma", autoevaluacion.getFirma());
            resultado.put("screenshotSimca", autoevaluacion.getScreenshotSimca());
            resultado.put("odsSeleccionados", obtenerOds(autoevaluacion));
            resultado.put("leccionesAprendidas", obtenerDescripcionesLecciones(autoevaluacion));
            resultado.put("oportunidadesMejora", obtenerDescripcionesMejoras(autoevaluacion));

            return new ApiResponse<>(200, "Autoevaluación encontrada correctamente.", resultado);
        } catch (NoSuchElementException e) {
            LOGGER.warn("⚠️ {}", e.getMessage());
            return new ApiResponse<>(404, e.getMessage(), null);
        } catch (Exception e) {
            LOGGER.error("❌ " + MSG_BUSQUEDA_ERROR, e);
            return new ApiResponse<>(500, MSG_BUSQUEDA_ERROR, null);
        }
    }

    private Map<Integer, MultipartFile> filtrarArchivosOds(Map<String, MultipartFile> archivos) {
        if (archivos == null || archivos.isEmpty()) return Map.of();
    
        return archivos.entrySet().stream()
            .filter(entry -> entry.getKey().startsWith("ods-"))
            .collect(Collectors.toMap(
                entry -> Integer.parseInt(entry.getKey().replace("ods-", "")),
                Map.Entry::getValue
            ));
    }

    private List<Map<String, Object>> obtenerOds(Autoevaluacion autoevaluacion) {
        return autoevaluacionOdsRepository.findByAutoevaluacion(autoevaluacion).stream().map(item -> {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("oidAutoevaluacionOds", item.getOidAutoevaluacionOds());
            map.put("oidOds", item.getOds().getOidObjetivoDesarrolloSostenible());
            map.put("nombre", item.getOds().getNombre());
            map.put("resultado", item.getResultado());
            map.put("documento", item.getNombreDocumento());
            return map;
        }).collect(Collectors.toList());
    }

    private List<LeccionDTO> obtenerDescripcionesLecciones(Autoevaluacion autoevaluacion) {
        return leccionAprendidaRepository.findByAutoevaluacion(autoevaluacion)
            .stream()
            .map(leccion -> new LeccionDTO(leccion.getOidLeccionAprendida(), leccion.getDescripcion()))
            .collect(Collectors.toList());
    }

    private List<OportunidadMejoraDTO> obtenerDescripcionesMejoras(Autoevaluacion autoevaluacion) {
        return oportunidadMejoraRepository.findByAutoevaluacion(autoevaluacion)
            .stream()
            .map(oportunidadMejora -> new OportunidadMejoraDTO(oportunidadMejora.getOidOportunidadMejora(), oportunidadMejora.getDescripcion()))
            .collect(Collectors.toList());
    }
}
