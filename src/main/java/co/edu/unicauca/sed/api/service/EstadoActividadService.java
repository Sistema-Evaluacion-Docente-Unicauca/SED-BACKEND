package co.edu.unicauca.sed.api.service;

import co.edu.unicauca.sed.api.model.EstadoActividad;
import co.edu.unicauca.sed.api.repository.EstadoActividadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EstadoActividadService {

    @Autowired
    private EstadoActividadRepository repository;

    public List<EstadoActividad> findAll() {
        return repository.findAll();
    }

    public EstadoActividad findById(Integer id) {
        return repository.findById(id).orElse(null);
    }

    public EstadoActividad save(EstadoActividad estadoActividad) {
        return repository.save(estadoActividad);
    }

    public void delete(Integer id) {
        repository.deleteById(id);
    }
}
