package co.edu.unicauca.sed.api.service.fuente;

import co.edu.unicauca.sed.api.model.Fuente;
import co.edu.unicauca.sed.api.service.FileService;
import co.edu.unicauca.sed.api.dto.FuenteCreateDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;

/**
 * Servicio para manejar archivos relacionados con fuentes.
 */
@Service
public class FuenteFileService {

    private static final Logger logger = LoggerFactory.getLogger(FuenteFileService.class);

    @Autowired
    private FileService fileService;

    /**
     * Maneja el archivo fuente común, eliminándolo y guardándolo si es necesario.
     *
     * @param optionalFuente Fuente existente opcional.
     * @param informeFuente  Nuevo archivo fuente.
     * @param periodoAcademico Identificador del período académico.
     * @param nombreEvaluado  Nombre del evaluado.
     * @return La ruta del archivo guardado.
     */
    public Path handleCommonFile(Optional<Fuente> optionalFuente, MultipartFile informeFuente, String periodoAcademico, String nombreEvaluado, 
        String contratacion, String departamento,  String nombreActividad, String idEvaluador) {
        try {
            if (informeFuente == null || informeFuente.isEmpty()) {
                logger.warn("El archivo fuente no fue proporcionado.");
                return null;
            }

            // Si existe una fuente previa, verifica si el archivo es diferente
            if (optionalFuente.isPresent()) {
                Fuente existingSource = optionalFuente.get();
                String existingFileName = existingSource.getRutaDocumentoFuente() != null
                        ? Path.of(existingSource.getRutaDocumentoFuente()).getFileName().toString()
                        : null;

                if (existingFileName != null && !informeFuente.getOriginalFilename().equals(existingFileName)) {
                    fileService.deleteFile(existingSource.getRutaDocumentoFuente());
                    logger.info("Archivo fuente existente eliminado: {}", existingFileName);
                }
            }

            Path savedFile = fileService.saveFile(informeFuente, periodoAcademico, nombreEvaluado, contratacion, departamento, nombreActividad, idEvaluador, "fuente");
            return savedFile;

        } catch (Exception e) {
            logger.error("Error al manejar el archivo fuente común", e);
            throw new RuntimeException("Error al manejar el archivo fuente común: " + e.getMessage(), e);
        }
    }

    public Path handleExecutiveReport(Optional<Fuente> optionalFuente, FuenteCreateDTO sourceDTO,
            Map<String, MultipartFile> informeEjecutivoFiles, String periodoAcademico,
            String nombreEvaluado, String contratacion, String departamento) {
        try {
            // Si no hay informe ejecutivo en la nueva solicitud
            if (sourceDTO.getInformeEjecutivo() == null || sourceDTO.getInformeEjecutivo().isEmpty()) {
                if (optionalFuente.isPresent()) {
                    Fuente existingSource = optionalFuente.get();
                    if (existingSource.getRutaDocumentoInforme() != null) {
                        // Eliminar el archivo previo si existe
                        fileService.deleteFile(existingSource.getRutaDocumentoInforme());
                        logger.info("Informe ejecutivo previo eliminado: {}", existingSource.getRutaDocumentoInforme());

                        // Actualizar la entidad Fuente para eliminar la referencia al archivo
                        existingSource.setNombreDocumentoInforme(null);
                        existingSource.setRutaDocumentoInforme(null);
                    }
                }
                return null; // No hay archivo nuevo que guardar
            }

            // Buscar el archivo correspondiente en los archivos adicionales
            Optional<MultipartFile> matchedFile = informeEjecutivoFiles.values().stream()
                    .filter(file -> file.getOriginalFilename().equalsIgnoreCase(sourceDTO.getInformeEjecutivo()))
                    .findFirst();

            if (matchedFile.isEmpty()) {
                logger.warn("No se encontró un archivo coincidente para el informe ejecutivo: {}",
                        sourceDTO.getInformeEjecutivo());
                return null;
            }

            if (optionalFuente.isPresent()) {
                Fuente existingSource = optionalFuente.get();
                String existingExecutiveReportName = existingSource.getNombreDocumentoInforme();

                // Eliminar el archivo previo si es diferente
                if (existingExecutiveReportName != null && !sourceDTO.getInformeEjecutivo().equals(existingExecutiveReportName)) {
                    fileService.deleteFile(existingSource.getRutaDocumentoInforme());
                    logger.info("Informe ejecutivo previo eliminado: {}", existingExecutiveReportName);
                }
            }

            // Guardar el nuevo informe ejecutivo
            Path savedFile = fileService.saveFile(matchedFile.get(), periodoAcademico, nombreEvaluado, contratacion, departamento, null, null, "informe");
            return savedFile;

        } catch (Exception e) {
            logger.error("Error al manejar el informe ejecutivo", e);
            throw new RuntimeException("Error al manejar el informe ejecutivo: " + e.getMessage(), e);
        }
    }
}
