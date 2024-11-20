package co.edu.unicauca.sed.api.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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
import java.util.Optional;
import org.springframework.util.StringUtils;

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

    public void saveMultipleSources(List<FuenteCreateDTO> sources, MultipartFile informeFuente,
            String observation) throws IOException {
        String commonFileName = informeFuente.getOriginalFilename();
        EstadoFuente stateSource = new EstadoFuente();
        stateSource.setOidEstadoFuente(2);

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

            Path specificFilePath = saveFile(informeFuente, periodoAcademico, evaluadoNombre);

            String informeEjecutivoName = null;
            Path informeEjecutivoPath = null;
            if (sourceDTO.getInformeEjecutivo() != null) {
                Path filePath = Paths.get(sourceDTO.getInformeEjecutivo());
                informeEjecutivoName = filePath.getFileName().toString();
                informeEjecutivoPath = saveFile(filePath, periodoAcademico, evaluadoNombre);
            }

            // Guardar o actualizar la fuente
            Optional<Fuente> existingSource = fuenteRepository.findByActividadAndTipoFuente(activity, sourceDTO.getTipoFuente());
            Fuente source = existingSource.orElse(new Fuente());
            assignSourceValues(source, sourceDTO, commonFileName, specificFilePath, observation, stateSource, activity, informeEjecutivoName, informeEjecutivoPath);
            fuenteRepository.save(source);
        }
    }

    private Path saveFile(Object fileInput, String periodoAcademico, String evaluadoNombre) throws IOException {
        // Valores por defecto
        periodoAcademico = (periodoAcademico != null) ? periodoAcademico : "default_period";
        evaluadoNombre = (evaluadoNombre != null) ? evaluadoNombre : "default_user";

        // Crear directorio destino
        Path directoryPath = Paths.get(uploadDir, periodoAcademico, evaluadoNombre);
        Files.createDirectories(directoryPath);

        Path targetPath;

        if (fileInput instanceof MultipartFile) {
            MultipartFile file = (MultipartFile) fileInput;
            targetPath = directoryPath.resolve(file.getOriginalFilename());
            Files.write(targetPath, file.getBytes());
        } else if (fileInput instanceof Path) {
            Path filePath = (Path) fileInput;
            targetPath = directoryPath.resolve(filePath.getFileName().toString());
            Files.copy(filePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
        } else {
            throw new IllegalArgumentException(
                    "El tipo de archivo no es compatible: " + fileInput.getClass().getName());
        }

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

        // Asignar informe ejecutivo si existe
        source.setNombreDocumentoInforme(informeEjecutivoName);
        source.setRutaDocumentoInforme(informeEjecutivoPath != null ? informeEjecutivoPath.toString() : null);
    }

    public void delete(Integer oid) {
        fuenteRepository.deleteById(oid);
    }
}
