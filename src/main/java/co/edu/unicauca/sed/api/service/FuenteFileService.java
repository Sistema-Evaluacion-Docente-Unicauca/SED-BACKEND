package co.edu.unicauca.sed.api.service;

import co.edu.unicauca.sed.api.model.Actividad;
import co.edu.unicauca.sed.api.model.Fuente;
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
     * @param academicPeriod Identificador del período académico.
     * @param evaluatedName  Nombre del evaluado.
     * @return La ruta del archivo guardado.
     */
    public Path handleCommonFile(Optional<Fuente> optionalFuente, MultipartFile informeFuente,
            String academicPeriod, String evaluatedName) {
        try {
            if (optionalFuente.isPresent()) {
                Fuente existingSource = optionalFuente.get();
                if (informeFuente != null && existingSource.getRutaDocumentoFuente() != null) {
                    String existingFileName = Path.of(existingSource.getRutaDocumentoFuente()).getFileName().toString();
                    if (!informeFuente.getOriginalFilename().equals(existingFileName)) {
                        fileService.deleteFile(existingSource.getRutaDocumentoFuente());
                        Path savedFile = fileService.saveFile(informeFuente, academicPeriod, evaluatedName, "fuente");
                        return savedFile;
                    }
                    return Path.of(existingSource.getRutaDocumentoFuente());
                } else {
                    if (informeFuente != null) {
                        Path savedFile = fileService.saveFile(informeFuente, academicPeriod, evaluatedName, "fuente");
                        return savedFile;
                    }
                }
            } else {
                logger.warn("No se encontró una fuente existente para la actividad y tipoFuente proporcionados.");
            }
            return null;
        } catch (Exception e) {
            logger.error("Error al manejar el archivo fuente común", e);
            throw new RuntimeException("Error al manejar el archivo fuente común: " + e.getMessage(), e);
        }
    }

    /**
     * Maneja el informe ejecutivo, eliminándolo y guardándolo si es necesario.
     *
     * @param optionalFuente        Fuente existente opcional.
     * @param sourceDTO             El DTO de la fuente.
     * @param informeEjecutivoFiles Mapa de archivos adicionales (informes
     *                              ejecutivos).
     * @param academicPeriod        Identificador del período académico.
     * @param evaluatedName         Nombre del evaluado.
     * @return La ruta del archivo de informe ejecutivo guardado.
     */
    public Path handleExecutiveReport(Optional<Fuente> optionalFuente, FuenteCreateDTO sourceDTO,
            Map<String, MultipartFile> informeEjecutivoFiles, String academicPeriod,
            String evaluatedName) {
        try {
            if (optionalFuente.isPresent()) {
                Fuente existingSource = optionalFuente.get();

                // Validar si hay un informe ejecutivo en el DTO
                if (sourceDTO.getInformeEjecutivo() != null) {
                    String existingExecutiveReportName = existingSource.getNombreDocumentoInforme();

                    // Si no hay informe previo o el informe es diferente, manejar el guardado
                    if (existingExecutiveReportName == null || !sourceDTO.getInformeEjecutivo().equals(existingExecutiveReportName)) {
                        // Eliminar informe previo si existe
                        if (existingExecutiveReportName != null) {
                            fileService.deleteFile(existingSource.getRutaDocumentoInforme());
                        }

                        // Buscar el archivo correspondiente en los archivos adicionales
                        Optional<MultipartFile> matchedFile = informeEjecutivoFiles.values().stream().filter( file -> file.getOriginalFilename().equalsIgnoreCase(sourceDTO.getInformeEjecutivo())).findFirst();

                        // Guardar el nuevo informe
                        if (matchedFile.isPresent()) {
                            Path savedFile = fileService.saveFile(matchedFile.get(), academicPeriod, evaluatedName,"informe");
                            return savedFile;
                        }
                    }
                }
            } else {
                logger.warn("No se encontró una fuente existente para manejar el informe ejecutivo.");
            }
            return null;
        } catch (Exception e) {
            logger.error("Error al manejar el informe ejecutivo", e);
            throw new RuntimeException("Error al manejar el informe ejecutivo: " + e.getMessage(), e);
        }
    }

    /**
     * Obtiene el período académico asociado a la actividad.
     *
     * @param activity La actividad relacionada.
     * @return El identificador del período académico.
     */
    public String getAcademicPeriod(Actividad activity) {
        return activity.getProceso().getOidPeriodoAcademico().getIdPeriodo();
    }

    /**
     * Obtiene el nombre del evaluado basado en su información.
     *
     * @param activity La actividad relacionada.
     * @return El nombre del evaluado formateado.
     */
    public String getEvaluatedName(Actividad activity) {
        return (activity.getProceso().getEvaluado().getNombres() + "_" +
                activity.getProceso().getEvaluado().getApellidos()).replaceAll("\\s+", "_");
    }
}
