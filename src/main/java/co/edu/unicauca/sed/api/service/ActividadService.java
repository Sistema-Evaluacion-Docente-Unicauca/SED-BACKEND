package co.edu.unicauca.sed.api.service;

import co.edu.unicauca.sed.api.model.Actividad;
import co.edu.unicauca.sed.api.repository.ActividadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActividadService {

    @Autowired
    private ActividadRepository repository;

    public List<Actividad> findAll() {
        return repository.findAll();
    }

    public Actividad findById(Integer id) {
        return repository.findById(id).orElse(null);
    }

    public Actividad save(Actividad actividad) {
        return repository.save(actividad);
    }

    public void delete(Integer id) {
        repository.deleteById(id);
    }
}
