package co.edu.unicauca.sed.api.service;

import co.edu.unicauca.sed.api.model.Encuesta;
import co.edu.unicauca.sed.api.repository.EncuestaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class EncuestaService {

    @Autowired
    private EncuestaRepository encuestaRepository;

    public Iterable<Encuesta> findAll() {
        return encuestaRepository.findAll();
    }

    public Optional<Encuesta> findById(Integer id) {
        return encuestaRepository.findById(id);
    }

    public Encuesta save(Encuesta encuesta) {
        return encuestaRepository.save(encuesta);
    }

    public void deleteById(Integer id) {
        encuestaRepository.deleteById(id);
    }
}
