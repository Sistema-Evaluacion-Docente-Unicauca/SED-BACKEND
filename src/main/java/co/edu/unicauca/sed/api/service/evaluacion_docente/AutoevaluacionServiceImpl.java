package co.edu.unicauca.sed.api.service.evaluacion_docente;

import co.edu.unicauca.sed.api.domain.*;
import co.edu.unicauca.sed.api.dto.*;
import co.edu.unicauca.sed.api.repository.*;
import co.edu.unicauca.sed.api.service.fuente.FuenteService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AutoevaluacionServiceImpl implements AutoevaluacionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AutoevaluacionServiceImpl.class);

    private static final String MSG_AUTOEVALUACION_OK = "Autoevaluación guardada correctamente.";
    private static final String MSG_AUTOEVALUACION_ERROR = "Error inesperado al guardar la autoevaluación.";
    private static final String MSG_BUSQUEDA_ERROR = "Error inesperado al buscar la autoevaluación.";
    private static final String MSG_NO_EVALUACION = "No se encontró autoevaluación para la fuente con ID: ";
    private static final String MSG_NO_FUENTE = "Fuente no encontrada con ID: ";
    private static final String PREFIJO_FIRMA = "firma";
    private static final String PREFIJO_SCREENSHOT = "screenshot";
    private static final String PREFIJO_DOCUMENTO = "fuente";

    private final AutoevaluacionRepository autoevaluacionRepository;
    private final FuenteRepository fuenteRepository;
    private final ObjetivoDesarrolloSostenibleRepository odsRepository;
    private final AutoevaluacionOdsRepository autoevaluacionOdsRepository;
    private final LeccionAprendidaRepository leccionAprendidaRepository;
    private final OportunidadMejoraRepository oportunidadMejoraRepository;
    private final FuenteService fuenteService;

    @Override
    @Transactional
    public ApiResponse<Void> guardarAutoevaluacion(
            AutoevaluacionDTO dto,
            MultipartFile firma,
            MultipartFile screenshotSimca,
            MultipartFile documentoNotas,
            Map<String, MultipartFile> archivosOds) {
        try {
            LOGGER.info("📥 Procesando autoevaluación para la fuente ID: {}", dto.getOidFuente());

            Fuente fuente = fuenteService.obtenerFuente(dto.getOidFuente());

            // Verificar si ya existe autoevaluación
            Optional<Autoevaluacion> autoevaluacionOpt = autoevaluacionRepository.findByFuente(fuente);
            Autoevaluacion autoevaluacion = autoevaluacionOpt.orElseGet(() -> {
                Autoevaluacion nueva = new Autoevaluacion();
                nueva.setFuente(fuente);
                return nueva;
            });

            // Guardar archivos principales
            String rutaFirma = guardarArchivo(firma, fuente, PREFIJO_FIRMA, autoevaluacion::setFirma);
            if (rutaFirma != null)
                autoevaluacion.setRutaDocumentoFirma(rutaFirma);

            String rutaScreenshot = guardarArchivo(screenshotSimca, fuente, PREFIJO_SCREENSHOT, autoevaluacion::setScreenshotSimca);
            if (rutaScreenshot != null)
                autoevaluacion.setRutaDocumentoSc(rutaScreenshot);

            // Guardar la autoevaluación (nuevo o actualizada)
            autoevaluacion = autoevaluacionRepository.save(autoevaluacion);

            Map<Integer, MultipartFile> archivosOdsMap = filtrarArchivosOds(archivosOds);

            // Guardar ODS con sus evidencias
            guardarOds(dto.getOdsSeleccionados(), autoevaluacion, archivosOdsMap, fuente);

            // Guardar lecciones y mejoras
            guardarLecciones(dto.getLeccionesAprendidas(), autoevaluacion);
            guardarMejoras(dto.getOportunidadesMejora(), autoevaluacion);

            // Guardar documento de notas
            String rutaNotas = guardarArchivo(documentoNotas, fuente, PREFIJO_DOCUMENTO,
                    fuente::setNombreDocumentoFuente);
            if (rutaNotas != null) {
                fuente.setRutaDocumentoFuente(rutaNotas);
            }

            // Actualizar información de la fuente
            actualizarFuenteConDatos(dto, fuente);

            LOGGER.info("✅ Autoevaluación {} correctamente.", autoevaluacionOpt.isPresent() ? "actualizada" : "creada");
            return new ApiResponse<>(200, MSG_AUTOEVALUACION_OK, null);

        } catch (Exception e) {
            LOGGER.error("❌ " + MSG_AUTOEVALUACION_ERROR, e);
            return new ApiResponse<>(500, MSG_AUTOEVALUACION_ERROR, null);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<Object> buscarPorFuente(Integer oidFuente) {
        try {
            LOGGER.info("🔍 Buscando autoevaluación por fuente ID: {}", oidFuente);

            Fuente fuente = fuenteService.obtenerFuente(oidFuente);
            Autoevaluacion autoevaluacion = autoevaluacionRepository.findByFuente(fuente)
                    .orElseThrow(() -> new NoSuchElementException(MSG_NO_EVALUACION + oidFuente));

            Map<String, Object> resultado = new LinkedHashMap<>();
            resultado.put("oidFuente", fuente.getOidFuente());
            resultado.put("nombreArchivo", fuente.getNombreDocumentoFuente());
            resultado.put("tipoCalificacion", fuente.getTipoCalificacion());
            resultado.put("observacion", fuente.getObservacion());
            resultado.put("calificacion", fuente.getCalificacion());
            resultado.put("firma", autoevaluacion.getFirma());
            resultado.put("screenshotSimca", autoevaluacion.getScreenshotSimca());
            resultado.put("fechaCreacion", fuente.getFechaCreacion());
            resultado.put("fechaActualizacion", fuente.getFechaActualizacion());
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

    private String guardarArchivo(MultipartFile archivo, Fuente fuente, String prefijo, Consumer<String> setter) {
        if (archivo != null && !archivo.isEmpty()) {
            try {
                String ruta = fuenteService.guardarDocumentoFuente(fuente, archivo, prefijo);
                String nombreArchivo = Paths.get(ruta).getFileName().toString();
                setter.accept(nombreArchivo);
                return ruta;
            } catch (IOException e) {
                LOGGER.error("❌ Error al guardar el archivo '{}' para la fuente ID: {}", prefijo, fuente.getOidFuente(), e);
                throw new RuntimeException("Error al guardar el archivo: " + prefijo, e);
            }
        }
        return null;
    }

    private void guardarOds(List<OdsDTO> odsList,
            Autoevaluacion autoevaluacion,
            Map<Integer, MultipartFile> archivosOds,
            Fuente fuente) {

        for (OdsDTO odsDTO : odsList) {
            ObjetivoDesarrolloSostenible ods = odsRepository.findById(odsDTO.getOidOds())
                    .orElseThrow(() -> new NoSuchElementException("ODS no encontrado: " + odsDTO.getOidOds()));

            AutoevaluacionOds entidad = new AutoevaluacionOds();
            entidad.setAutoevaluacion(autoevaluacion);
            entidad.setOds(ods);
            entidad.setResultado(odsDTO.getResultado());

            MultipartFile archivo = archivosOds.get(odsDTO.getOidOds());
            if (archivo != null && !archivo.isEmpty()) {
                try {
                String ruta = fuenteService.guardarDocumentoFuente(fuente, archivo,  "ods");
                String nombreArchivo = Paths.get(ruta).getFileName().toString();

                    entidad.setNombreDocumento(nombreArchivo);
                    entidad.setRutaDocumento(ruta.toString());
                    LOGGER.info("📎 Evidencia ODS {} guardada en {}", odsDTO.getOidOds(), ruta);

                } catch (IOException e) {
                    LOGGER.error("❌ Error al guardar evidencia del ODS {}: {}", odsDTO.getOidOds(), e.getMessage());
                    throw new RuntimeException("Error al guardar evidencia del ODS " + odsDTO.getOidOds(), e);
                }
            }

            autoevaluacionOdsRepository.save(entidad);
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
    

    private void guardarLecciones(List<LeccionDTO> lecciones, Autoevaluacion autoevaluacion) {
        lecciones.forEach(leccion -> {
            LeccionAprendida nueva = new LeccionAprendida();
            nueva.setAutoevaluacion(autoevaluacion);
            nueva.setDescripcion(leccion.getDescripcion());
            leccionAprendidaRepository.save(nueva);
        });
    }

    private void guardarMejoras(List<OportunidadMejoraDTO> mejoras, Autoevaluacion autoevaluacion) {
        mejoras.forEach(mejora -> {
            OportunidadMejora nueva = new OportunidadMejora();
            nueva.setAutoevaluacion(autoevaluacion);
            nueva.setDescripcion(mejora.getDescripcion());
            oportunidadMejoraRepository.save(nueva);
        });
    }

    private void actualizarFuenteConDatos(AutoevaluacionDTO dto, Fuente fuente) {
        fuente.setCalificacion(dto.getCalificacion());
        fuente.setTipoCalificacion(dto.getTipoCalificacion());
        fuente.setObservacion(dto.getObservacion());
        fuenteRepository.save(fuente);
    }

    private List<Map<String, Object>> obtenerOds(Autoevaluacion autoevaluacion) {
        return autoevaluacionOdsRepository.findByAutoevaluacion(autoevaluacion).stream().map(item -> {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("oidOds", item.getOds().getOidObjetivoDesarrolloSostenible());
            map.put("nombre", item.getOds().getNombre());
            map.put("resultado", item.getResultado());
            return map;
        }).collect(Collectors.toList());
    }

    private List<String> obtenerDescripcionesLecciones(Autoevaluacion autoevaluacion) {
        return leccionAprendidaRepository.findByAutoevaluacion(autoevaluacion)
                .stream().map(LeccionAprendida::getDescripcion).collect(Collectors.toList());
    }

    private List<String> obtenerDescripcionesMejoras(Autoevaluacion autoevaluacion) {
        return oportunidadMejoraRepository.findByAutoevaluacion(autoevaluacion)
                .stream().map(OportunidadMejora::getDescripcion).collect(Collectors.toList());
    }
}
