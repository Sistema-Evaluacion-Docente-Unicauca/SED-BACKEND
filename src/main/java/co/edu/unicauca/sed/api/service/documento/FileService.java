package co.edu.unicauca.sed.api.service.documento;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FileService {

    private static final Logger logger = LoggerFactory.getLogger(FileService.class);

    @Value("${document.upload-dir}")
    private String uploadDir;

    public Path saveFile(MultipartFile file, String periodoAcademico, String nombreEvaluado, String contratacion, String departamento) throws IOException {
        return saveFile(file, periodoAcademico, nombreEvaluado, contratacion, departamento, null, null, null);
    }

    /**
     * Guarda un archivo en el sistema de archivos con un prefijo para diferenciar
     * tipos de archivo.
     *
     * @param file           Archivo a guardar.
     * @param periodoAcademico Periodo académico asociado.
     * @param nombreEvaluado  Nombre del evaluado asociado.
     * @param prefix         Prefijo a agregar al nombre del archivo (ej. "fuente",
     *                       "informe").
     * @return Ruta del archivo guardado.
     * @throws IOException Si ocurre un error al guardar.
     */
    public Path saveFile(MultipartFile file, String periodoAcademico, String nombreEvaluado, String contratacion, String departamento, String nombreActividad, String idEvaluador, String prefix)
            throws IOException {
        // Construir la ruta dinámica utilizando uploadDir
        Path directoryPath = buildDynamicPath(uploadDir, periodoAcademico, departamento, contratacion, nombreEvaluado, nombreActividad);
        Files.createDirectories(directoryPath);

        // Obtener el nombre original del archivo
        String originalFilename = file.getOriginalFilename();

        // Validar si el archivo ya tiene el prefijo
        String idEvaluadorSegment = (idEvaluador != null && !idEvaluador.isEmpty()) ? idEvaluador + "-" : "";
        String prefixedFilename = (prefix != null && !prefix.isEmpty() && !originalFilename.startsWith(prefix + "-"))
                ? prefix + "-" + idEvaluadorSegment + originalFilename
                : originalFilename;


        // Ruta completa del archivo
        Path targetPath = directoryPath.resolve(prefixedFilename);

        try {
            // Guardar el archivo
            Files.write(targetPath, file.getBytes());
            logger.info("✅ Archivo guardado correctamente en: {}", targetPath);
        } catch (IOException e) {
            logger.error("❌ Error al guardar el archivo: {}, Error: {}", targetPath, e.getMessage(), e);
            throw e;
        }

        return targetPath;
    }

    /**
     * Elimina un archivo del sistema de archivos.
     *
     * @param filePath Ruta del archivo a eliminar.
     */
    public void deleteFile(String filePath) {
        try {
            if (filePath != null) {
                Path path = Paths.get(filePath);
                if (Files.exists(path)) {
                    Files.delete(path);
                    logger.info("Archivo eliminado exitosamente: {}", filePath);
                } else {
                    logger.warn("Intento de eliminar archivo inexistente: {}", filePath);
                }
            }
        } catch (IOException e) {
            logger.error("Error al eliminar el archivo: {}, Error: {}", filePath, e.getMessage(), e);
        }
    }

    /**
     * Recupera un archivo como recurso del sistema de archivos.
     *
     * @param filePathString Ruta del archivo en el sistema.
     * @return Recurso del archivo si existe.
     * @throws Exception Si ocurre un error al acceder al archivo.
     */
    public Resource getFileResource(String filePathString) throws Exception {
        Path filePath = Paths.get(filePathString);
        try {
            if (!Files.exists(filePath) || !Files.isReadable(filePath)) {
                logger.warn("El archivo no existe o no es accesible: {}", filePathString);
                throw new RuntimeException("El archivo no existe o no es accesible: " + filePathString);
            }
            logger.info("Archivo recuperado exitosamente: {}", filePathString);
            return new UrlResource(filePath.toUri());
        } catch (Exception e) {
            logger.error("Error al recuperar el archivo: {}, Error: {}", filePathString, e.getMessage(), e);
            throw e;
        }
    }

    private Path buildDynamicPath(String... segments) {
    List<String> validSegments = Arrays.stream(segments)
            .filter(segment -> segment != null && !segment.isEmpty())
            .collect(Collectors.toList());

    if (validSegments.isEmpty()) {
        throw new IllegalArgumentException("The path cannot be empty or composed only of null/empty segments.");
    }

    return Paths.get(validSegments.get(0), validSegments.subList(1, validSegments.size()).toArray(new String[0]));
}

}
