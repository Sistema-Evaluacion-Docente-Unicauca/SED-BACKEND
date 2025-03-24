package co.edu.unicauca.sed.api.service.evaluacion_docente;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import co.edu.unicauca.sed.api.domain.Autoevaluacion;
import co.edu.unicauca.sed.api.domain.AutoevaluacionOds;
import co.edu.unicauca.sed.api.domain.Fuente;
import co.edu.unicauca.sed.api.domain.ObjetivoDesarrolloSostenible;
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
                    LOGGER.info("📎 Evidencia ODS {} guardada en {}", odsDTO.getOidOds(), ruta);

                } catch (IOException e) {
                    LOGGER.error("❌ Error al guardar evidencia del ODS {}: {}", odsDTO.getOidOds(), e.getMessage());
                    throw new RuntimeException("Error al guardar evidencia del ODS " + odsDTO.getOidOds(), e);
                }
            }

            autoevaluacionOdsRepository.save(entidad);

            LOGGER.debug("✅ ODS {} {}", odsDTO.getOidAutoevaluacionOds() == null ? "creado" : "actualizado", odsDTO.getOidOds());
        }
    }
}