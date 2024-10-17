package co.edu.unicauca.sed.api.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import co.edu.unicauca.sed.api.model.Fuente;
import co.edu.unicauca.sed.api.repository.FuenteRepository;

@Service
public class FuenteService {

    @Autowired
    private FuenteRepository fuenteRepository;

    @Autowired
    private DocumentoService documentoService;

    /**
     * Encuentra todas las fuentes.
     */
    public List<Fuente> findAll() {
        return (List<Fuente>) fuenteRepository.findAll();
    }

    /**
     * Encuentra una fuente por su OID.
     */
    public Fuente findByOid(Integer oid) {
        return fuenteRepository.findById(oid).orElse(null);
    }

    /**
     * Encuentra todas las fuentes por el OID de la actividad.
     */
    public List<Fuente> findByActividadOid(Integer oidActividad) {
        return fuenteRepository.findByActividadOid(oidActividad);
    }

    /**
     * Guarda una fuente.
     */
    public Fuente save(Fuente fuente, MultipartFile archivo) {
        Fuente response = fuenteRepository.save(fuente);
        if (response != null) {
            documentoService.upload(response.getNombreDocumento(), archivo);
        }
        return response;
    }

    /**
     * Elimina una fuente por su OID.
     */
    public void delete(Integer oid) {
        fuenteRepository.deleteById(oid);
    }
}
