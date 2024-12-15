package co.edu.unicauca.sed.api.service;

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

  @Value("${document.upload-dir}")
  private String uploadDir;

  /**
   * Guarda un archivo en el sistema de archivos.
   *
   * @param file           Archivo a guardar.
   * @param academicPeriod Periodo académico asociado.
   * @param evaluatedName  Nombre del evaluado asociado.
   * @return Ruta del archivo guardado.
   * @throws IOException Si ocurre un error al guardar.
   */
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

    // Generar el nombre del archivo con prefijo
    String originalFilename = file.getOriginalFilename();
    String prefixedFilename = prefix + "-" + originalFilename;

    // Ruta completa del archivo
    Path targetPath = directoryPath.resolve(prefixedFilename);

    // Guardar el archivo
    Files.write(targetPath, file.getBytes());

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
          Files.delete(path); // Eliminar el archivo
          System.out.println("Archivo eliminado: " + filePath);
        }
      }
    } catch (IOException e) {
      System.err.println("Advertencia: No se pudo eliminar el archivo: " + filePath);
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
    if (!Files.exists(filePath) || !Files.isReadable(filePath)) {
      throw new RuntimeException("El archivo no existe o no es accesible: " + filePathString);
    }
    return new UrlResource(filePath.toUri());
  }
}
