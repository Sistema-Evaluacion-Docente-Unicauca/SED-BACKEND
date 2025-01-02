package co.edu.unicauca.sed.api.service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import org.springframework.core.io.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import co.edu.unicauca.sed.api.dto.FuenteCreateDTO;
import co.edu.unicauca.sed.api.model.Actividad;
import co.edu.unicauca.sed.api.model.EstadoFuente;
import co.edu.unicauca.sed.api.model.Fuente;
import co.edu.unicauca.sed.api.repository.FuenteRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
public class FuenteService {

    @Autowired
    private FuenteRepository fuenteRepository;

    @Autowired
    private DocumentoService documentoService;

    @Autowired
    private ActividadService actividadService;

    @Autowired
    private FileService fileService;

    @Autowired
    private EstadoFuenteService estadoFuenteService;

    @Value("${document.upload-dir}")
    private String uploadDir;

    public static final String PREFIJO_INFORME = "informe";
    public static final String PREFIJO_FUENTE = "fuente";
    public static final int ESTADO_DILIGENCIADO = 2;
    public static final int ESTADO_PENDIENTE = 1;

    public FuenteService(ActividadService actividadService, FuenteRepository fuenteRepository) {
        this.actividadService = actividadService;
        this.fuenteRepository = fuenteRepository;
    }

    /**
     * Recupera todas las fuentes desde el repositorio con soporte de paginación.
     *
     * @param pageable Parámetro para definir la paginación (número de página y
     *                 tamaño de página).
     * @return Página de entidades Fuente.
     */
    public Page<Fuente> findAll(Pageable pageable) {
        return fuenteRepository.findAll(pageable);
    }

    /**
     * Busca una fuente por su identificador único.
     *
     * @param oid El ID de la fuente a buscar.
     * @return La entidad Fuente si se encuentra, null en caso contrario.
     */
    public Fuente findByOid(Integer oid) {
        return fuenteRepository.findById(oid).orElse(null);
    }

    /**
     * Busca todas las fuentes asociadas a una actividad específica.
     *
     * @param oidActividad El ID de la actividad.
     * @return Lista de entidades Fuente vinculadas a la actividad.
     */
    public List<Fuente> findByActividadOid(Integer oidActividad) {
        return fuenteRepository.findByActividadOid(oidActividad);
    }

    /**
     * Guarda una fuente y sube su archivo asociado.
     *
     * @param fuente  La entidad Fuente a guardar.
     * @param archivo El archivo a subir.
     * @return La entidad Fuente guardada.
     */
    public Fuente save(Fuente fuente, MultipartFile archivo) {
        Fuente response = fuenteRepository.save(fuente);
        if (response != null) {
            documentoService.upload(response.getNombreDocumentoFuente(), archivo);
        }
        return response;
    }

    /**
     * Elimina una fuente por su identificador único.
     *
     * @param oid El ID de la fuente a eliminar.
     */
    public void delete(Integer oid) {
        fuenteRepository.deleteById(oid);
    }

    /**
     * Guarda múltiples fuentes junto con sus archivos asociados.
     *
     * @param sourcesJson   JSON con los datos de las fuentes.
     * @param informeFuente Archivo común asociado a las fuentes.
     * @param observation   Observación general.
     * @param allFiles      Archivos adicionales para manejar.
     * @throws IOException Si ocurre un error al manejar los archivos.
     */
    public void saveSource(String sourcesJson, MultipartFile informeFuente, String observation,
            Map<String, MultipartFile> allFiles) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        List<FuenteCreateDTO> sources = objectMapper.readValue(sourcesJson, new TypeReference<List<FuenteCreateDTO>>() {
        });

        // Filtrar archivos adicionales excluyendo informeFuente
        Map<String, MultipartFile> informeEjecutivoFiles = allFiles != null
                ? allFiles.entrySet().stream().filter(entry -> !entry.getKey().equals("informeFuente"))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
                : Map.of();

        Path commonFilePath = null;
        String commonFileName = null;

        for (FuenteCreateDTO sourceDTO : sources) {
            // Obtener la actividad asociada
            Actividad activity = actividadService.findByOid(sourceDTO.getOidActividad());

            // Obtener el período académico y el evaluado dinámicamente desde la actividad
            String academicPeriod = activity.getProceso().getOidPeriodoAcademico().getIdPeriodo();
            String evaluatedName = activity.getProceso().getEvaluado().getNombres() + "_"
                    + activity.getProceso().getEvaluado().getApellidos();
            evaluatedName = evaluatedName.replaceAll("\\s+", "_");

            // Manejar el archivo fuente (informeFuente)
            if (informeFuente != null) {
                // Guardar el archivo fuente en una ruta dinámica
                commonFilePath = fileService.saveFile(informeFuente, academicPeriod, evaluatedName, PREFIJO_FUENTE);
                commonFileName = informeFuente.getOriginalFilename();
            }

            // Busca si ya existe una fuente asociada para reemplazar o actualizar
            Optional<Fuente> optionalFuente = fuenteRepository.findByActividadAndTipoFuente(activity, sourceDTO.getTipoFuente());
            Fuente source = optionalFuente.orElse(new Fuente());

            // Eliminar archivos antiguos si se modifican
            if (optionalFuente.isPresent()) {
                Fuente existingSource = optionalFuente.get();

                // Eliminar archivo fuente si se proporciona uno nuevo
                if (informeFuente != null && existingSource.getRutaDocumentoFuente() != null) {
                    fileService.deleteFile(existingSource.getRutaDocumentoFuente());
                }

                // Eliminar informe ejecutivo si se proporciona uno nuevo o si se vacía el campo
                if (sourceDTO.getInformeEjecutivo() != null) {
                    if (!sourceDTO.getInformeEjecutivo().equals(existingSource.getNombreDocumentoInforme())) {
                        fileService.deleteFile(existingSource.getRutaDocumentoInforme());
                    }
                }
            }

            // Manejar el informe ejecutivo
            String executiveReportName = sourceDTO.getInformeEjecutivo();
            Path executiveReportPath = null;

            if (executiveReportName != null && !executiveReportName.isEmpty()) {
                Optional<MultipartFile> matchedFile = informeEjecutivoFiles.values().stream().filter(file -> file.getOriginalFilename().equalsIgnoreCase(executiveReportName)).findFirst();

                if (matchedFile.isPresent()) {
                    // Guardar informe ejecutivo en una ruta dinámica
                    executiveReportPath = fileService.saveFile(matchedFile.get(), academicPeriod, evaluatedName, PREFIJO_INFORME);
                }
            }

            EstadoFuente stateSource;

            if (source.getEstadoFuente() != null && source.getEstadoFuente().getOidEstadoFuente() == 1) {
                // Si el estado actual es 1, lo actualizamos a 2
                stateSource = estadoFuenteService.createEstadoFuente(ESTADO_DILIGENCIADO);
            } else if (source.getEstadoFuente() != null) {
                // Si el estado no es null y no es 1, mantenemos el valor actual
                stateSource = source.getEstadoFuente();
            } else {
                // Si el estado es null, lo configuramos a 2 (estado diligenciado)
                stateSource = estadoFuenteService.createEstadoFuente(ESTADO_DILIGENCIADO);
            }

            // Asigna valores actualizados o nuevos a la fuente
            assignSourceValues(source, sourceDTO, commonFileName, commonFilePath, observation, stateSource, activity, executiveReportName, executiveReportPath);

            fuenteRepository.save(source);
        }
    }

    /**
     * Asigna valores a una entidad Fuente, actualizando o configurando información como el estado, actividad, y archivos asociados.
     *
     * @param source              La entidad Fuente a modificar.
     * @param sourceDTO           Datos provenientes del DTO.
     * @param commonFileName      Nombre del archivo común asociado.
     * @param commonFilePath      Ruta del archivo común.
     * @param observation         Observación a asociar.
     * @param stateSource         Estado de la fuente.
     * @param activity            Actividad vinculada.
     * @param executiveReportName Nombre del informe ejecutivo.
     * @param executiveReportPath Ruta del informe ejecutivo.
     */
    private void assignSourceValues(Fuente source, FuenteCreateDTO sourceDTO, String commonFileName,
            Path commonFilePath, String observation, EstadoFuente stateSource,
            Actividad activity, String executiveReportName, Path executiveReportPath) {
        source.setTipoFuente(sourceDTO.getTipoFuente());
        source.setCalificacion(sourceDTO.getCalificacion());

        // Asignar nombre y ruta del documento fuente
        source.setNombreDocumentoFuente(commonFileName); // Asignar el nombre del archivo fuente
        source.setRutaDocumentoFuente(commonFilePath != null ? commonFilePath.toString() : null); // Asignar ruta

        source.setObservacion(observation);
        source.setActividad(activity);
        source.setEstadoFuente(stateSource);

        // Manejar el informe ejecutivo
        if (executiveReportName != null && !executiveReportName.isEmpty()) {
            source.setNombreDocumentoInforme(executiveReportName);
            source.setRutaDocumentoInforme(executiveReportPath != null ? executiveReportPath.toString() : null);
        } else {
            // Si el informe ejecutivo viene vacío, limpiar los valores
            source.setNombreDocumentoInforme(null);
            source.setRutaDocumentoInforme(null);
        }
    }

    /**
     * Recupera un archivo asociado a una fuente.
     *
     * @param id       ID de la fuente.
     * @param isReport Indica si se debe recuperar el informe (true) o la fuente
     * @return Respuesta con el archivo como recurso descargable.
     */
    public ResponseEntity<?> getFile(Integer id, boolean isReport) {
        try {
            // Busca la fuente por ID
            Fuente fuente = fuenteRepository.findById(id).orElseThrow(() -> new RuntimeException("Fuente con ID " + id + " no encontrada."));

            // Determina el archivo y la ruta según el flag
            String filePath = isReport ? fuente.getRutaDocumentoInforme() : fuente.getRutaDocumentoFuente();
            String fileName = isReport ? fuente.getNombreDocumentoInforme() : fuente.getNombreDocumentoFuente();

            // Validar que la ruta no sea nula ni vacía
            if (filePath == null || filePath.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("El archivo solicitado no está disponible para esta fuente.");
            }

            // Recupera el recurso utilizando FileService
            Resource resource = fileService.getFileResource(filePath);

            // Retorna el archivo como respuesta
            return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"").body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ocurrió un error al procesar la solicitud. Error: " + e.getMessage());
        }
    }
}
