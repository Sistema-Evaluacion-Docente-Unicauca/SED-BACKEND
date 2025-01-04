package co.edu.unicauca.sed.api.service.fuente;

import co.edu.unicauca.sed.api.dto.FuenteCreateDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Servicio para integrar datos JSON y manejar archivos adicionales.
 */
@Service
public class FuenteIntegrationService {

    private static final Logger logger = LoggerFactory.getLogger(FuenteIntegrationService.class);

    /**
     * Convierte un JSON en una lista de DTOs de fuentes.
     *
     * @param sourcesJson JSON que contiene los datos de las fuentes.
     * @return Lista de objetos FuenteCreateDTO.
     */
    public List<FuenteCreateDTO> parseSourcesJson(String sourcesJson) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            List<FuenteCreateDTO> sources = objectMapper.readValue(sourcesJson, new TypeReference<List<FuenteCreateDTO>>() {});
            return sources;
        } catch (IOException e) {
            logger.error("Error al convertir el JSON de fuentes a objetos DTO", e);
            throw new RuntimeException("Error al procesar el JSON de fuentes: " + e.getMessage(), e);
        }
    }

    /**
     * Filtra los archivos adicionales excluyendo el archivo fuente común.
     *
     * @param allFiles Mapa de todos los archivos a procesar.
     * @return Un mapa que contiene únicamente los archivos de informes ejecutivos.
     */
    public Map<String, MultipartFile> filterExecutiveFiles(Map<String, MultipartFile> allFiles) {
        try {
            if (allFiles == null || allFiles.isEmpty()) {
                logger.warn("El mapa de archivos adicionales está vacío o es nulo");
                return Map.of();
            }

            Map<String, MultipartFile> filteredFiles = allFiles.entrySet().stream()
                    .filter(entry -> !entry.getKey().equals("informeFuente"))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

            return filteredFiles;
        } catch (Exception e) {
            logger.error("Error al filtrar los archivos adicionales", e);
            throw new RuntimeException("Error al filtrar los archivos adicionales: " + e.getMessage(), e);
        }
    }
}
