package co.edu.unicauca.sed.api.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    public void saveMultipleSources(String sourcesJson, MultipartFile informeFuente, String observation,
            Map<String, MultipartFile> allFiles) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        List<FuenteCreateDTO> sources = objectMapper.readValue(sourcesJson, new TypeReference<List<FuenteCreateDTO>>() {});
        
        Map<String, MultipartFile> informeEjecutivoFiles = allFiles != null
                ? allFiles.entrySet().stream()
                    .filter(entry -> !entry.getKey().equals("informeFuente"))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
                : Map.of();

        Path commonFilePath = null;

        for (int i = 0; i < sources.size(); i++) {
            FuenteCreateDTO sourceDTO = sources.get(i);

            Actividad activity = actividadService.findByOid(sourceDTO.getOidActividad());
            if (activity.getProceso() == null) {
                throw new IllegalStateException("No se pudo obtener el proceso asociado a la actividad.");
            }

            String periodoAcademico = activity.getProceso().getOidPeriodoAcademico().getIdPeriodo();
            String evaluadoNombre = StringUtils.trimAllWhitespace(
                    activity.getProceso().getEvaluado().getNombres() + "_" +
                            activity.getProceso().getEvaluado().getApellidos());

            if (i == 0 && informeFuente != null) {
                commonFilePath = saveFile(informeFuente, periodoAcademico, evaluadoNombre);
            }

            String informeEjecutivoName = sourceDTO.getInformeEjecutivo();
            Path informeEjecutivoPath = null;

            if (informeEjecutivoName != null && informeEjecutivoFiles.containsKey("informeEjecutivo" + (i + 1))) {
                MultipartFile informeEjecutivoFile = informeEjecutivoFiles.get("informeEjecutivo" + (i + 1));
                informeEjecutivoPath = saveFile(informeEjecutivoFile, periodoAcademico, evaluadoNombre);
            }

            Optional<Fuente> existingSource = fuenteRepository.findByActividadAndTipoFuente(activity,
                    sourceDTO.getTipoFuente());
            Fuente source = existingSource.orElse(new Fuente());

            String commonFileName = informeFuente != null ? informeFuente.getOriginalFilename() : null;
            EstadoFuente stateSource = createEstadoFuente(2);

            assignSourceValues(source, sourceDTO,
                    commonFileName,
                    commonFilePath,
                    observation,
                    stateSource,
                    activity,
                    informeEjecutivoName,
                    informeEjecutivoPath);

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
        Files.createDirectories(directoryPath); // Crear carpeta si no existe
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
}
