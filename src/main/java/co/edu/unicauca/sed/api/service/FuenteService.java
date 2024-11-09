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
import java.util.Optional;

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
            documentoService.upload(response.getNombreDocumento(), archivo);
        }
        return response;
    }

    public void saveMultipleSources(List<FuenteCreateDTO> sources, MultipartFile file, String observation)
            throws IOException {
        String documentName = file.getOriginalFilename();
        Path filePath = Paths.get(uploadDir, documentName);
        Files.createDirectories(filePath.getParent());
        Files.write(filePath, file.getBytes());

        EstadoFuente stateSource = new EstadoFuente();
        stateSource.setOidEstadoFuente(2);

        for (FuenteCreateDTO sourceDTO : sources) {
            Actividad activity = actividadService.findByOid(sourceDTO.getOidActividad());

            // Check if a source already exists for the activity and source type
            Optional<Fuente> existingSource = fuenteRepository.findByActividadAndTipoFuente(activity,
                    sourceDTO.getTipoFuente());

            Fuente source;
            if (existingSource.isPresent()) {
                // If it exists, use the function to update its properties
                source = existingSource.get();
            } else {
                // If it does not exist, create a new instance of Source
                source = new Fuente();
            }

            // Assign common values using the function
            assignSourceValues(source, sourceDTO, documentName, filePath.toString(), observation, stateSource, activity);

            // Save the Source (new or updated)
            fuenteRepository.save(source);
        }
    }

    private void assignSourceValues(Fuente source, FuenteCreateDTO sourceDTO, String documentName,
            String documentPath, String observation, EstadoFuente stateSource, Actividad activity) {
        source.setTipoFuente(sourceDTO.getTipoFuente());
        source.setCalificacion(sourceDTO.getCalificacion());
        source.setNombreDocumento(documentName);
        source.setRutaDocumento(documentPath);
        source.setObservacion(observation);
        source.setActividad(activity);
        source.setEstadoFuente(stateSource);
    }

    public void delete(Integer oid) {
        fuenteRepository.deleteById(oid);
    }
}
