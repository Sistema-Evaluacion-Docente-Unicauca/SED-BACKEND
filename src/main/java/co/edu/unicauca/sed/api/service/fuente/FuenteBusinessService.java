package co.edu.unicauca.sed.api.service.fuente;

import co.edu.unicauca.sed.api.domain.Actividad;
import co.edu.unicauca.sed.api.domain.EstadoFuente;
import co.edu.unicauca.sed.api.domain.Fuente;
import co.edu.unicauca.sed.api.domain.Usuario;
import co.edu.unicauca.sed.api.dto.FuenteCreateDTO;
import co.edu.unicauca.sed.api.repository.FuenteRepository;
import co.edu.unicauca.sed.api.service.EstadoFuenteService;
import co.edu.unicauca.sed.api.service.notificacion.NotificacionDocumentoService;
import co.edu.unicauca.sed.api.repository.ActividadRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
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
    @Lazy
    private ActividadRepository actividadRepository;

    @Autowired
    private FuenteFileService fileService;

    @Autowired
    private EstadoFuenteService estadoFuenteService;

    @Autowired
    private NotificacionDocumentoService notificacionDocumentoService;

    /**
     * Procesa una fuente, actualizando o creando su información y archivos asociados.
     *
     * @param sourceDTO             El DTO de la fuente.
     * @param informeFuente         Archivo común asociado a la fuente.
     * @param observation           Observación general.
     * @param informeEjecutivoFiles Archivos adicionales (informes ejecutivos).
     */
        public void processSource(FuenteCreateDTO sourceDTO, MultipartFile informeFuente, String observation, Map<String, MultipartFile> informeEjecutivoFiles) {
        try {
            String mensajeTipoFuente;
            Actividad actividad = actividadRepository.findById(sourceDTO.getOidActividad())
                .orElseThrow(() -> new IllegalArgumentException("No se encontró una actividad con el ID: " + sourceDTO.getOidActividad()));
            String periodoAcademico = actividad.getProceso().getOidPeriodoAcademico().getIdPeriodo();
            String nombres = actividad.getProceso().getEvaluado().getNombres().replaceAll("\\s+", "_");
            String apellidos = actividad.getProceso().getEvaluado().getApellidos().replaceAll("\\s+", "_");
            String nombreEvaluado = nombres + "_" + apellidos;
            String contratacion = actividad.getProceso().getEvaluado().getUsuarioDetalle().getContratacion();
            String departamento = actividad.getProceso().getEvaluado().getUsuarioDetalle().getDepartamento();
            String nombreActividad = actividad.getNombreActividad().replace("-", "").replaceAll("\\s+", "_");
            String idEvaluador = actividad.getProceso().getEvaluador().getIdentificacion();

            // Garantizar que el repositorio no devuelva null
            Optional<Fuente> optionalFuente = Optional.ofNullable(
                    fuenteRepository.findByActividadAndTipoFuente(actividad, sourceDTO.getTipoFuente())
            ).orElse(Optional.empty());

            Fuente source = optionalFuente.orElse(new Fuente());
            // Manejo según tipo de fuente
            Path commonFilePath = null;
            Path executiveReportPath = null;
            Usuario evaluador = actividad.getProceso().getEvaluador();

            Usuario evaluado = actividad.getProceso().getEvaluado();

            if ("2".equals(sourceDTO.getTipoFuente())) {
                mensajeTipoFuente = "Fuente 2";
                commonFilePath = fileService.handleCommonFile(optionalFuente, informeFuente, periodoAcademico, nombreEvaluado, contratacion, departamento, nombreActividad, idEvaluador);
                notificacionDocumentoService.notificarEvaluado(mensajeTipoFuente, evaluador, evaluado);
            } else if ("1".equals(sourceDTO.getTipoFuente())) {
                mensajeTipoFuente = "Fuente 1 (Autoevaluación)";
                commonFilePath = fileService.handleCommonFile(optionalFuente, informeFuente, periodoAcademico, nombreEvaluado, contratacion, departamento, null, null);
                executiveReportPath = fileService.handleExecutiveReport(optionalFuente, sourceDTO, informeEjecutivoFiles, periodoAcademico, nombreEvaluado, contratacion, departamento);
            } else {
                logger.warn("Tipo de fuente desconocido: {}", sourceDTO.getTipoFuente());
                throw new IllegalArgumentException("Tipo de fuente desconocido: " + sourceDTO.getTipoFuente());
            }

            // Determinar y asignar estado
            EstadoFuente stateSource = determineStateSource(source);

            // Asignar valores a la fuente
            assignSourceValues(source, sourceDTO, commonFilePath, observation, stateSource, actividad, executiveReportPath);
            fuenteRepository.save(source);
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
            String observation, EstadoFuente stateSource, Actividad actividad, Path executiveReportPath) {
        try {
            // Asignar valores básicos
            source.setTipoFuente(sourceDTO.getTipoFuente());
            source.setCalificacion(sourceDTO.getCalificacion());
            source.setNombreDocumentoFuente(commonFilePath != null ? commonFilePath.getFileName().toString() : source.getNombreDocumentoFuente());
            source.setRutaDocumentoFuente(commonFilePath != null ? commonFilePath.toString() : source.getRutaDocumentoFuente());
            source.setObservacion(observation);
            source.setActividad(actividad);
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
