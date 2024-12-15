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
     * Retrieves all sources from the repository.
     *
     * @return List of all Fuente entities
     */
    public List<Fuente> findAll() {
        return (List<Fuente>) fuenteRepository.findAll();
    }

    /**
     * Finds a source by its unique identifier.
     *
     * @param oid The ID of the source to retrieve
     * @return Fuente entity if found, null otherwise
     */
    public Fuente findByOid(Integer oid) {
        return fuenteRepository.findById(oid).orElse(null);
    }

    /**
     * Finds all sources associated with a specific activity.
     *
     * @param oidActividad The ID of the activity
     * @return List of Fuente entities linked to the activity
     */
    public List<Fuente> findByActividadOid(Integer oidActividad) {
        return fuenteRepository.findByActividadOid(oidActividad);
    }

    /**
     * Saves a source entity and uploads its associated file.
     *
     * @param fuente  The Fuente entity to save
     * @param archivo The file to upload
     * @return The saved Fuente entity
     */
    public Fuente save(Fuente fuente, MultipartFile archivo) {
        Fuente response = fuenteRepository.save(fuente);
        if (response != null) {
            documentoService.upload(response.getNombreDocumentoFuente(), archivo);
        }
        return response;
    }

        /**
     * Deletes a source by its unique identifier.
     *
     * @param oid The ID of the source to delete
     */
    public void delete(Integer oid) {
        fuenteRepository.deleteById(oid);
    }

    //Cunado se envia fuente vacia debe eliminar la referencia en el informe ejecutivo

    /**
     * Guarda múltiples fuentes junto con sus archivos asociados.
     *
     * @param sourcesJson   JSON con datos de fuentes.
     * @param informeFuente Archivo común.
     * @param observation   Observación general.
     * @param allFiles      Archivos adicionales.
     * @throws IOException Si ocurre un error al manejar los archivos.
     */
    public void saveSource(String sourcesJson, MultipartFile informeFuente, String observation,
            Map<String, MultipartFile> allFiles) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        List<FuenteCreateDTO> sources = objectMapper.readValue(sourcesJson, new TypeReference<List<FuenteCreateDTO>>() {
        });

        // Filtrar archivos adicionales excluyendo informeFuente
        Map<String, MultipartFile> informeEjecutivoFiles = allFiles != null
                ? allFiles.entrySet().stream()
                        .filter(entry -> !entry.getKey().equals("informeFuente"))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
                : Map.of();

        Path commonFilePath = null;
        String commonFileName = null;

        for (FuenteCreateDTO sourceDTO : sources) {
            // Obtener la actividad asociada
            Actividad activity = actividadService.findByOid(sourceDTO.getOidActividad());

            // Obtener el período académico y el evaluado dinámicamente desde la actividad
            String academicPeriod = activity.getProceso().getOidPeriodoAcademico().getIdPeriodo(); // Ejemplo: "2024-2"
            String evaluatedName = activity.getProceso().getEvaluado().getNombres() + "_"
                    + activity.getProceso().getEvaluado().getApellidos();
            evaluatedName = evaluatedName.replaceAll("\\s+", "_"); // Reemplazar espacios por guiones bajos

            // Manejar el archivo fuente (informeFuente)
            if (informeFuente != null) {
                // Guardar el archivo fuente en una ruta dinámica
                commonFilePath = fileService.saveFile(informeFuente, academicPeriod, evaluatedName, PREFIJO_FUENTE);
                commonFileName = informeFuente.getOriginalFilename();
                System.out.println("Nombre de fuente que llega: " + commonFileName);
            }

            // Busca si ya existe una fuente asociada para reemplazar o actualizar
            Optional<Fuente> optionalFuente = fuenteRepository.findByActividadAndTipoFuente(activity,
                    sourceDTO.getTipoFuente());
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
                Optional<MultipartFile> matchedFile = informeEjecutivoFiles.values().stream()
                        .filter(file -> file.getOriginalFilename().equalsIgnoreCase(executiveReportName))
                        .findFirst();

                if (matchedFile.isPresent()) {
                    // Guardar informe ejecutivo en una ruta dinámica
                    executiveReportPath = fileService.saveFile(matchedFile.get(), academicPeriod, evaluatedName, PREFIJO_INFORME);
                }
            }

            EstadoFuente stateSource = source.getEstadoFuente() != null ? source.getEstadoFuente()
                    : estadoFuenteService.createEstadoFuente(ESTADO_DILIGENCIADO);

            // Asigna valores actualizados o nuevos a la fuente
            assignSourceValues(source, sourceDTO, commonFileName, commonFilePath, observation, stateSource, activity, executiveReportName, executiveReportPath);

            fuenteRepository.save(source);
        }
    }

    private void assignSourceValues(Fuente source, FuenteCreateDTO sourceDTO, String commonFileName,
            Path commonFilePath, String observation, EstadoFuente stateSource,
            Actividad activity, String executiveReportName, Path executiveReportPath) {
        source.setTipoFuente(sourceDTO.getTipoFuente());
        source.setCalificacion(sourceDTO.getCalificacion());

        // Asignar nombre y ruta del documento fuente
        System.out.println("Nombre de fuente que se esta guardadando: " + commonFileName);

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
     * @param isReport Indica si se debe recuperar el informe (true) o la fuente (false).
     * @return Respuesta con el archivo como recurso descargable.
     */
    public ResponseEntity<?> getFile(Integer id, boolean isReport) {
        try {
            // Busca la fuente por ID
            Fuente fuente = fuenteRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Fuente con ID " + id + " no encontrada."));

            // Determina el archivo y la ruta según el flag
            String filePath = isReport ? fuente.getRutaDocumentoInforme() : fuente.getRutaDocumentoFuente();
            String fileName = isReport ? fuente.getNombreDocumentoInforme() : fuente.getNombreDocumentoFuente();

            // Validar que la ruta no sea nula ni vacía
            if (filePath == null || filePath.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("El archivo solicitado no está disponible para esta fuente.");
            }

            // Recupera el recurso utilizando FileService
            Resource resource = fileService.getFileResource(filePath);

            // Retorna el archivo como respuesta
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .body(resource);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ocurrió un error al procesar la solicitud. Error: " + e.getMessage());
        }
    }

}
