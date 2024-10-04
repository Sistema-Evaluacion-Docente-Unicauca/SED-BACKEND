package co.edu.unicauca.sed.api.service;

import co.edu.unicauca.sed.api.model.TipoActividad;
import co.edu.unicauca.sed.api.repository.TipoActividadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TipoActividadService {

    @Autowired
    private TipoActividadRepository repository;

    public List<TipoActividad> findAll() {
        return repository.findAll();
    }

    public TipoActividad findById(Integer id) {
        return repository.findById(id).orElse(null);
    }

    public TipoActividad save(TipoActividad tipoActividad) {
        return repository.save(tipoActividad);
    }

    public void delete(Integer id) {
        repository.deleteById(id);
    }
}
