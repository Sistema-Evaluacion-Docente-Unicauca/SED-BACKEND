package co.edu.unicauca.sed.api.service;

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

@Service
public class FileService {

    private static final Logger logger = LoggerFactory.getLogger(FileService.class);

    @Value("${document.upload-dir}")
    private String uploadDir;

    public Path saveFile(MultipartFile file, String academicPeriod, String evaluatedName) throws IOException {
        return saveFile(file, academicPeriod, evaluatedName, null);
    }

    /**
     * Guarda un archivo en el sistema de archivos con un prefijo para diferenciar
     * tipos de archivo.
     *
     * @param file           Archivo a guardar.
     * @param academicPeriod Periodo académico asociado.
     * @param evaluatedName  Nombre del evaluado asociado.
     * @param prefix         Prefijo a agregar al nombre del archivo (ej. "fuente",
     *                       "informe").
     * @return Ruta del archivo guardado.
     * @throws IOException Si ocurre un error al guardar.
     */
    public Path saveFile(MultipartFile file, String academicPeriod, String evaluatedName, String prefix)
            throws IOException {
        // Construir la ruta dinámica utilizando uploadDir
        Path directoryPath = Paths.get(uploadDir, academicPeriod, evaluatedName);
        Files.createDirectories(directoryPath); // Crea los directorios si no existen

        // Generar el nombre del archivo con prefijo solo si se proporciona
        String originalFilename = file.getOriginalFilename();
        String prefixedFilename = (prefix != null && !prefix.isEmpty()) ? prefix + "-" + originalFilename : originalFilename;

        // Ruta completa del archivo
        Path targetPath = directoryPath.resolve(prefixedFilename);

        try {
            // Guardar el archivo
            Files.write(targetPath, file.getBytes());
        } catch (IOException e) {
            logger.error("Error al guardar el archivo: {}, Error: {}", targetPath, e.getMessage(), e);
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
}
