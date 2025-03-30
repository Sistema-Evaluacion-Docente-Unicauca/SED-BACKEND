package co.edu.unicauca.sed.api.service.evaluacion_docente;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import co.edu.unicauca.sed.api.domain.Autoevaluacion;
import co.edu.unicauca.sed.api.domain.AutoevaluacionOds;
import co.edu.unicauca.sed.api.domain.Fuente;
import co.edu.unicauca.sed.api.domain.ObjetivoDesarrolloSostenible;
import co.edu.unicauca.sed.api.dto.ArchivoDTO;
import co.edu.unicauca.sed.api.dto.OdsDTO;
import co.edu.unicauca.sed.api.repository.AutoevaluacionOdsRepository;
import co.edu.unicauca.sed.api.repository.ObjetivoDesarrolloSostenibleRepository;
import co.edu.unicauca.sed.api.service.fuente.FuenteService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AutoevaluacionOdsServiceImpl implements AutoevaluacionOdsService {

    private final AutoevaluacionOdsRepository autoevaluacionOdsRepository;
    private final ObjetivoDesarrolloSostenibleRepository odsRepository;
    private final FuenteService fuenteService;
    private static final Logger LOGGER = LoggerFactory.getLogger(AutoevaluacionServiceImpl.class);

    @Override
    public void guardarOds(List<OdsDTO> odsList, Autoevaluacion autoevaluacion,
            Map<Integer, MultipartFile> archivosOds, Fuente fuente) {

        for (OdsDTO odsDTO : odsList) {
            ObjetivoDesarrolloSostenible ods = odsRepository.findById(odsDTO.getOidOds()).orElseThrow(() -> new NoSuchElementException("ODS no encontrado: " + odsDTO.getOidOds()));

            AutoevaluacionOds entidad;

            if (odsDTO.getOidAutoevaluacionOds() != null) {
                entidad = autoevaluacionOdsRepository.findById(odsDTO.getOidAutoevaluacionOds()).orElseGet(() -> {
                    LOGGER.warn("❗ ODS con ID {} no encontrado. Se creará nuevo.", odsDTO.getOidAutoevaluacionOds());
                    return new AutoevaluacionOds();
                });
            } else {
                entidad = new AutoevaluacionOds();
            }

            entidad.setAutoevaluacion(autoevaluacion);
            entidad.setOds(ods);
            entidad.setResultado(odsDTO.getResultado());

            MultipartFile archivo = archivosOds.get(odsDTO.getOidOds());
            if (archivo != null && !archivo.isEmpty()) {
                try {
                    String ruta = fuenteService.guardarDocumentoFuente(fuente, archivo, "ods");
                    String nombreArchivo = Paths.get(ruta).getFileName().toString();

                    entidad.setNombreDocumento(nombreArchivo);
                    entidad.setRutaDocumento(ruta);
                } catch (IOException e) {
                    LOGGER.error("❌ Error al guardar evidencia del ODS {}: {}", odsDTO.getOidOds(), e.getMessage());
                    throw new RuntimeException("Error al guardar evidencia del ODS " + odsDTO.getOidOds(), e);
                }
            }

            autoevaluacionOdsRepository.save(entidad);
        }
    }

    @Override
    public ArchivoDTO obtenerArchivoPorId(Integer idOds) {
        AutoevaluacionOds ods = autoevaluacionOdsRepository.findById(idOds)
                .orElseThrow(() -> new RuntimeException("ODS con ID " + idOds + " no encontrado."));
        return new ArchivoDTO(ods.getNombreDocumento(), ods.getRutaDocumento());
    }

    @Override
    public List<Map<String, Object>> obtenerOds(Autoevaluacion autoevaluacion) {
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

    @Override
    public Map<Integer, MultipartFile> mapearArchivoODS(List<OdsDTO> odsSeleccionados,
            Map<String, MultipartFile> archivos) {
        if (archivos == null || archivos.isEmpty() || odsSeleccionados == null || odsSeleccionados.isEmpty()) {
            return Map.of();
        }

        Map<Integer, MultipartFile> resultado = new HashMap<>();

        for (OdsDTO ods : odsSeleccionados) {
            String nombreDocumento = ods.getDocumento();
            Integer oidOds = ods.getOidOds();

            if (nombreDocumento != null && oidOds != null) {
                Optional<Map.Entry<String, MultipartFile>> archivoEncontrado = archivos.entrySet().stream()
                    .filter(entry -> entry.getValue().getOriginalFilename() != null && entry.getValue().getOriginalFilename().equals(nombreDocumento))
                    .findFirst();

                archivoEncontrado.ifPresent(entry -> resultado.put(oidOds, entry.getValue()));
            }
        }

        return resultado;
    }
}
