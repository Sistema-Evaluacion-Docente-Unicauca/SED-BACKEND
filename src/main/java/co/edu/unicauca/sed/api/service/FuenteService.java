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

    public void saveMultipleFuentes(List<FuenteCreateDTO> fuentes, MultipartFile archivo, String observacion)
            throws IOException {
        String documentName = archivo.getOriginalFilename();
        Path filePath = Paths.get(uploadDir, documentName);
        Files.createDirectories(filePath.getParent());
        Files.write(filePath, archivo.getBytes());

        EstadoFuente estadoFuente = new EstadoFuente();
        estadoFuente.setOidEstadoFuente(2);

        for (FuenteCreateDTO fuenteCreateDTO : fuentes) {
            Actividad actividad = actividadService.findByOid(fuenteCreateDTO.getOidActividad());

            Fuente fuente = new Fuente();
            fuente.setTipoFuente(fuenteCreateDTO.getTipoFuente());
            fuente.setCalificacion(fuenteCreateDTO.getCalificacion());
            fuente.setNombreDocumento(documentName);
            fuente.setRutaDocumento(filePath.toString());
            fuente.setObservacion(observacion);
            fuente.setActividad(actividad);
            fuente.setEstadoFuente(estadoFuente);

            fuenteRepository.save(fuente);
        }
    }

    public void delete(Integer oid) {
        fuenteRepository.deleteById(oid);
    }
}
