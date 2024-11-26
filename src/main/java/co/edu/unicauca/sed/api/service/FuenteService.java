package co.edu.unicauca.sed.api.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
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
import org.springframework.util.StringUtils;
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

    @Value("${document.upload-dir}")
    private String uploadDir;

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
     * Saves multiple sources with their respective files.
     *
     * @param sourcesJson   JSON string containing FuenteCreateDTO objects
     * @param informeFuente Common file shared by all sources
     * @param observation   Observation related to the sources
     * @param allFiles      Map of all files submitted, excluding the common file
     * @throws IOException If there are errors handling the files
     */
    public void saveSource(String sourcesJson, MultipartFile informeFuente, String observation,
            Map<String, MultipartFile> allFiles) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        List<FuenteCreateDTO> sources = objectMapper.readValue(sourcesJson, new TypeReference<List<FuenteCreateDTO>>() {
        });

        Map<String, MultipartFile> informeEjecutivoFiles = allFiles != null
                ? allFiles.entrySet().stream().filter(entry -> !entry.getKey().equals("informeFuente"))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
                : Map.of();

        Path commonFilePath = null;

        for (FuenteCreateDTO sourceDTO : sources) {
            Actividad activity = actividadService.findByOid(sourceDTO.getOidActividad());
            if (activity.getProceso() == null) {
                throw new IllegalStateException("No se pudo obtener el proceso asociado a la actividad.");
            }

            String academicPeriod = activity.getProceso().getOidPeriodoAcademico().getIdPeriodo();
            String evaluatorName = StringUtils.trimAllWhitespace(
                    activity.getProceso().getEvaluado().getNombres() + "_" +
                            activity.getProceso().getEvaluado().getApellidos());

            // Save the common file if not already saved
            if (commonFilePath == null && informeFuente != null) {
                commonFilePath = saveFile(informeFuente, academicPeriod, evaluatorName);
            }

            String executiveReportName = sourceDTO.getInformeEjecutivo();
            Path executiveReportPath = null;

            // Match the file by executive report name
            if (executiveReportName != null) {
                Optional<MultipartFile> matchedFile = informeEjecutivoFiles.values().stream()
                        .filter(file -> file != null
                                && file.getOriginalFilename().equalsIgnoreCase(executiveReportName))
                        .findFirst();

                if (matchedFile.isPresent()) {
                    executiveReportPath = saveFile(matchedFile.get(), academicPeriod, evaluatorName);
                }
            }

            Optional<Fuente> existingSource = fuenteRepository.findByActividadAndTipoFuente(activity,
                    sourceDTO.getTipoFuente());
            Fuente source = existingSource.orElse(new Fuente());

            // Handle file replacement
            if (existingSource.isPresent()) {
                Fuente existing = existingSource.get();

                // Replace source file if a new one is uploaded
                if (informeFuente != null && existing.getRutaDocumentoFuente() != null
                        && !existing.getNombreDocumentoFuente().equals(informeFuente.getOriginalFilename())) {
                    deleteFile(existing.getRutaDocumentoFuente());
                }

                // Replace executive report file if a new one is uploaded
                if (executiveReportPath != null && existing.getRutaDocumentoInforme() != null
                        && !existing.getNombreDocumentoInforme().equals(executiveReportName)) {
                    deleteFile(existing.getRutaDocumentoInforme());
                }
            }

            String commonFileName = informeFuente != null ? informeFuente.getOriginalFilename() : null;
            EstadoFuente stateSource = createEstadoFuente(2);

            assignSourceValues(source, sourceDTO, commonFileName, commonFilePath, observation, stateSource, activity,
                    executiveReportName, executiveReportPath);

            fuenteRepository.save(source);
        }
    }

    /**
     * Creates a new EstadoFuente entity with the given ID.
     *
     * @param oidEstado The ID of the EstadoFuente
     * @return EstadoFuente entity with the specified ID
     */
    private EstadoFuente createEstadoFuente(int oidEstado) {
        EstadoFuente stateSource = new EstadoFuente();
        stateSource.setOidEstadoFuente(oidEstado);
        return stateSource;
    }

    /**
     * Saves a file to the specified directory structure.
     *
     * @param file             The file to save
     * @param periodoAcademico Academic period associated with the file
     * @param evaluadoNombre   Name of the evaluated person associated with the file
     * @return Path of the saved file
     * @throws IOException If there are errors writing the file
     */
    private Path saveFile(MultipartFile file, String periodoAcademico, String evaluadoNombre) throws IOException {
        Path directoryPath = Paths.get(uploadDir, periodoAcademico, evaluadoNombre);
        Files.createDirectories(directoryPath);
        Path targetPath = directoryPath.resolve(file.getOriginalFilename());

        Files.write(targetPath, file.getBytes());

        return targetPath;
    }

    /**
     * Assigns values to a Fuente entity based on the provided parameters.
     *
     * @param source               The Fuente entity to update
     * @param sourceDTO            FuenteCreateDTO containing new values
     * @param commonFileName       Name of the common file
     * @param commonFilePath       Path of the common file
     * @param observation          Observation related to the source
     * @param stateSource          EstadoFuente entity representing the state of the
     *                             source
     * @param activity             Actividad entity associated with the source
     * @param informeEjecutivoName Name of the executive report file
     * @param informeEjecutivoPath Path of the executive report file
     */
    private void assignSourceValues(Fuente source, FuenteCreateDTO sourceDTO, String commonFileName,
            Path commonFilePath, String observation, EstadoFuente stateSource,
            Actividad activity, String informeEjecutivoName, Path informeEjecutivoPath) {
        source.setTipoFuente(sourceDTO.getTipoFuente());
        source.setCalificacion(sourceDTO.getCalificacion());
        source.setNombreDocumentoFuente(commonFileName);
        source.setRutaDocumentoFuente(commonFilePath != null ? commonFilePath.toString() : null);
        source.setObservacion(observation);
        source.setActividad(activity);
        source.setEstadoFuente(stateSource);

        if (informeEjecutivoName != null) {
            source.setNombreDocumentoInforme(informeEjecutivoName);
        }
        if (informeEjecutivoPath != null) {
            source.setRutaDocumentoInforme(informeEjecutivoPath.toString());
        }
    }

    /**
     * Deletes a source by its unique identifier.
     *
     * @param oid The ID of the source to delete
     */
    public void delete(Integer oid) {
        fuenteRepository.deleteById(oid);
    }

    /**
     * Deletes a file from the filesystem.
     *
     * @param filePath The path of the file to delete
     */
    private void deleteFile(String filePath) {
        try {
            Path path = Paths.get(filePath);
            Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new RuntimeException("Error while deleting the file: " + filePath, e);
        }
    }

    /**
     * Retrieves the file resource based on the ID and the type of document (fuente
     * or informe).
     *
     * @param id      The ID of the Fuente entity
     * @param informe Flag to determine if RUTADOCUMENTOINFORME should be used
     *                (true) or RUTADOCUMENTOFUENTE (false)
     * @return ResponseEntity containing the file resource or an error message
     */
    public ResponseEntity<?> getFile(Integer id, boolean isReport) {
        try {
            Fuente fuente = findByOid(id);
            Optional<Fuente> fuenteOptional = Optional.ofNullable(fuente);

            if (fuenteOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("La fuente con el ID especificado no fue encontrada.");
            }

            // Determine which path and name to use
            String filePathString;
            String fileName;

            if (isReport) {
                // Validate if report exists
                if (fuente.getRutaDocumentoInforme() == null || fuente.getRutaDocumentoInforme().isEmpty()) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body("El reporte solicitado no está disponible para esta fuente.");
                }
                filePathString = fuente.getRutaDocumentoInforme();
                fileName = fuente.getNombreDocumentoInforme();
            } else {
                // Default to fuente
                filePathString = fuente.getRutaDocumentoFuente();
                fileName = fuente.getNombreDocumentoFuente();
            }

            // Ensure the file exists
            Path filePath = Paths.get(filePathString);
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("El archivo solicitado no existe: " + fileName);
            }

            // Return the file as a downloadable resource
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .body(resource);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ocurrió un error al procesar la solicitud. Error: " + e.getMessage());
        }
    }
}
