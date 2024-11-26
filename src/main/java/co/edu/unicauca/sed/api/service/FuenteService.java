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

    public List<Fuente> findAll() {
        return (List<Fuente>) fuenteRepository.findAll();
    }

    public Fuente findByOid(Integer oid) {
        return fuenteRepository.findById(oid).orElse(null);
    }

    public List<Fuente> findByActividadOid(Integer oidActividad) {
        return fuenteRepository.findByActividadOid(oidActividad);
    }

    public Fuente save(Fuente fuente, MultipartFile archivo) {
        Fuente response = fuenteRepository.save(fuente);
        if (response != null) {
            documentoService.upload(response.getNombreDocumentoFuente(), archivo);
        }
        return response;
    }

    public void saveMultipleSources(String sourcesJson, MultipartFile informeFuente, String observation, Map<String, MultipartFile> allFiles) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        List<FuenteCreateDTO> sources = objectMapper.readValue(sourcesJson, new TypeReference<List<FuenteCreateDTO>>() {});

        Map<String, MultipartFile> informeEjecutivoFiles = allFiles != null
            ? allFiles.entrySet().stream().filter(entry -> !entry.getKey().equals("informeFuente")).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)): Map.of();

        Path commonFilePath = null;

        for (FuenteCreateDTO sourceDTO : sources) {
            Actividad activity = actividadService.findByOid(sourceDTO.getOidActividad());
            if (activity.getProceso() == null) {
                throw new IllegalStateException("No se pudo obtener el proceso asociado a la actividad.");
            }

            String periodoAcademico = activity.getProceso().getOidPeriodoAcademico().getIdPeriodo();
            String evaluadoNombre = StringUtils.trimAllWhitespace(
                activity.getProceso().getEvaluado().getNombres() + "_" +
                activity.getProceso().getEvaluado().getApellidos()
            );

            // Guardar el archivo común si no se ha guardado aún
            if (commonFilePath == null && informeFuente != null) {
                commonFilePath = saveFile(informeFuente, periodoAcademico, evaluadoNombre);
            }

            String informeEjecutivoName = sourceDTO.getInformeEjecutivo();
            Path informeEjecutivoPath = null;

            // Match del archivo por nombre de informeEjecutivo
            if (informeEjecutivoName != null) {
                Optional<MultipartFile> matchedFile = informeEjecutivoFiles.values().stream().filter(file -> file.getOriginalFilename().equalsIgnoreCase(informeEjecutivoName)).findFirst();

                if (matchedFile.isPresent()) {
                    informeEjecutivoPath = saveFile(matchedFile.get(), periodoAcademico, evaluadoNombre);
                }
            }

            Optional<Fuente> existingSource = fuenteRepository.findByActividadAndTipoFuente(activity,sourceDTO.getTipoFuente());
            Fuente source = existingSource.orElse(new Fuente());

            String commonFileName = informeFuente != null ? informeFuente.getOriginalFilename() : null;
            EstadoFuente stateSource = createEstadoFuente(2);

            assignSourceValues(source, sourceDTO, commonFileName, commonFilePath, observation, stateSource, activity, informeEjecutivoName, informeEjecutivoPath);

            fuenteRepository.save(source);
        }
    }

    private EstadoFuente createEstadoFuente(int oidEstado) {
        EstadoFuente stateSource = new EstadoFuente();
        stateSource.setOidEstadoFuente(oidEstado);
        return stateSource;
    }

    private Path saveFile(MultipartFile file, String periodoAcademico, String evaluadoNombre) throws IOException {
        Path directoryPath = Paths.get(uploadDir, periodoAcademico, evaluadoNombre);
        Files.createDirectories(directoryPath);
        Path targetPath = directoryPath.resolve(file.getOriginalFilename());

        Files.write(targetPath, file.getBytes());

        return targetPath;
    }

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

    public void delete(Integer oid) {
        fuenteRepository.deleteById(oid);
    }

    /**
     * Retrieves the file resource based on the ID and the type of document (fuente or informe).
     *
     * @param id The ID of the Fuente entity
     * @param informe Flag to determine if RUTADOCUMENTOINFORME should be used (true) or RUTADOCUMENTOFUENTE (false)
     * @return ResponseEntity containing the file resource or an error message
     */
    public ResponseEntity<?> getFile(Integer id, boolean isReport) {
        try {
            Fuente fuente = findByOid(id);
            Optional<Fuente> fuenteOptional = Optional.ofNullable(fuente);
    
            if (fuenteOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("La fuente con el ID especificado no fue encontrada.");
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
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("El archivo solicitado no existe: " + fileName);
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
