package co.edu.unicauca.sed.api.service;

import co.edu.unicauca.sed.api.model.EncuestaEstudiante;
import co.edu.unicauca.sed.api.repository.EncuestaEstudianteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class EncuestaEstudianteService {

    @Autowired
    private EncuestaEstudianteRepository encuestaEstudianteRepository;

    public Iterable<EncuestaEstudiante> findAll() {
        return encuestaEstudianteRepository.findAll();
    }

    public Optional<EncuestaEstudiante> findById(Integer id) {
        return encuestaEstudianteRepository.findById(id);
    }

    public EncuestaEstudiante save(EncuestaEstudiante encuestaEstudiante) {
        return encuestaEstudianteRepository.save(encuestaEstudiante);
    }

    public void deleteById(Integer id) {
        encuestaEstudianteRepository.deleteById(id);
    }
}
