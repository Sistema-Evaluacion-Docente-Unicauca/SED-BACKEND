package co.edu.unicauca.sed.api.service;

import co.edu.unicauca.sed.api.dto.FuenteCreateDTO;
import co.edu.unicauca.sed.api.model.Actividad;
import co.edu.unicauca.sed.api.model.Fuente;
import co.edu.unicauca.sed.api.model.EstadoFuente;
import co.edu.unicauca.sed.api.repository.FuenteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;

/**
 * Servicio para manejar la lógica de negocio relacionada con fuentes.
 */
@Service
public class FuenteBusinessService {

    private static final Logger logger = LoggerFactory.getLogger(FuenteBusinessService.class);

    @Autowired
    private FuenteRepository fuenteRepository;

    @Autowired
    private ActividadService actividadService;

    @Autowired
    private FuenteFileService fileService;

    @Autowired
    private EstadoFuenteService estadoFuenteService;

    /**
     * Procesa una fuente, actualizando o creando su información y archivos
     * asociados.
     *
     * @param sourceDTO             El DTO de la fuente.
     * @param informeFuente         Archivo común asociado a la fuente.
     * @param observation           Observación general.
     * @param informeEjecutivoFiles Archivos adicionales (informes ejecutivos).
     */
    public void processSource(FuenteCreateDTO sourceDTO, MultipartFile informeFuente, String observation,
            Map<String, MultipartFile> informeEjecutivoFiles) {
        try {
            Actividad activity = actividadService.findByOid(sourceDTO.getOidActividad());
            String academicPeriod = fileService.getAcademicPeriod(activity);
            String evaluatedName = fileService.getEvaluatedName(activity);

            Optional<Fuente> optionalFuente = fuenteRepository.findByActividadAndTipoFuente(activity, sourceDTO.getTipoFuente());
            Fuente source = optionalFuente.orElse(new Fuente());
            // Manejo según tipo de fuente
            Path commonFilePath = null;
            Path executiveReportPath = null;

            if ("2".equals(sourceDTO.getTipoFuente())) {
                // Tipo de fuente 1: Solo procesar informeFuente
                commonFilePath = fileService.handleCommonFile(optionalFuente, informeFuente, academicPeriod, evaluatedName);
            } else if ("1".equals(sourceDTO.getTipoFuente())) {
                // Tipo de fuente 2: Procesar informeFuente y archivos adicionales
                commonFilePath = fileService.handleCommonFile(optionalFuente, informeFuente, academicPeriod, evaluatedName);
                executiveReportPath = fileService.handleExecutiveReport(optionalFuente, sourceDTO, informeEjecutivoFiles, academicPeriod, evaluatedName);
            } else {
                logger.warn("Tipo de fuente desconocido: {}", sourceDTO.getTipoFuente());
                throw new IllegalArgumentException("Tipo de fuente desconocido: " + sourceDTO.getTipoFuente());
            }

            // Determinar y asignar estado
            EstadoFuente stateSource = determineStateSource(source);

            // Asignar valores a la fuente
            assignSourceValues(source, sourceDTO, commonFilePath, observation, stateSource, activity, executiveReportPath);

            // Guardar la fuente
            fuenteRepository.save(source);
            logger.info("Fuente guardada exitosamente: tipo={}, actividad={}", sourceDTO.getTipoFuente(), sourceDTO.getOidActividad());

        } catch (Exception e) {
            logger.error("Error inesperado al procesar la fuente: {}", sourceDTO, e);
            throw new RuntimeException("Error inesperado al procesar la fuente: " + e.getMessage(), e);
        }
    }

    /**
     * Determina el estado de la fuente basado en su estado actual.
     *
     * @param source La fuente cuyo estado será determinado.
     * @return El estado actualizado o conservado de la fuente.
     */
    private EstadoFuente determineStateSource(Fuente source) {
        try {
            if (source.getEstadoFuente() != null && source.getEstadoFuente().getOidEstadoFuente() == 1) {
                return estadoFuenteService.createEstadoFuente(2); // Estado diligenciado
            } else if (source.getEstadoFuente() != null) {
                return source.getEstadoFuente();
            } else {
                return estadoFuenteService.createEstadoFuente(2);
            }
        } catch (Exception e) {
            logger.error("Error al determinar el estado de la fuente", e);
            throw new RuntimeException("Error al determinar el estado de la fuente: " + e.getMessage(), e);
        }
    }

    /**
     * Asigna valores a una entidad Fuente, actualizando o configurando información
     * como el estado, actividad y archivos asociados.
     *
     * @param source              La entidad Fuente a modificar.
     * @param sourceDTO           Datos provenientes del DTO.
     * @param commonFilePath      Ruta del archivo común.
     * @param observation         Observación a asociar.
     * @param stateSource         Estado de la fuente.
     * @param activity            Actividad vinculada.
     * @param executiveReportPath Ruta del informe ejecutivo.
     */
    private void assignSourceValues(Fuente source, FuenteCreateDTO sourceDTO, Path commonFilePath,
            String observation, EstadoFuente stateSource, Actividad activity, Path executiveReportPath) {
        try {
            // Asignar valores básicos
            source.setTipoFuente(sourceDTO.getTipoFuente());
            source.setCalificacion(sourceDTO.getCalificacion());
            source.setNombreDocumentoFuente(commonFilePath != null ? commonFilePath.getFileName().toString() : source.getNombreDocumentoFuente());
            source.setRutaDocumentoFuente(commonFilePath != null ? commonFilePath.toString() : source.getRutaDocumentoFuente());
            source.setObservacion(observation);
            source.setActividad(activity);
            source.setEstadoFuente(stateSource);

            // Asignar valores del informe ejecutivo
            if (executiveReportPath != null) {
                source.setRutaDocumentoInforme(executiveReportPath.toString());
                source.setNombreDocumentoInforme(executiveReportPath.getFileName().toString());
            }
        } catch (Exception e) {
            logger.error("Error al asignar valores a la fuente", e);
            throw new RuntimeException("Error al asignar valores a la fuente: " + e.getMessage(), e);
        }
    }

}
