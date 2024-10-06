package co.edu.unicauca.sed.api.service;

import co.edu.unicauca.sed.api.model.Encuesta;
import co.edu.unicauca.sed.api.model.EncuestaEstudiante;
import co.edu.unicauca.sed.api.model.EvaluacionEstudiante;
import co.edu.unicauca.sed.api.repository.EncuestaEstudianteRepository;
import co.edu.unicauca.sed.api.repository.EncuestaRepository;
import co.edu.unicauca.sed.api.repository.EvaluacionEstudianteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.time.LocalDateTime;

@Service
public class EvaluacionEstudianteService {

    @Autowired
    private EvaluacionEstudianteRepository evaluacionEstudianteRepository;

     @Autowired
    private EncuestaRepository encuestaRepository;

    @Autowired
    private EncuestaEstudianteRepository encuestaEstudianteRepository;

    public Iterable<EvaluacionEstudiante> findAll() {
        return evaluacionEstudianteRepository.findAll();
    }

    public Optional<EvaluacionEstudiante> findById(Integer id) {
        return evaluacionEstudianteRepository.findById(id);
    }

    public EvaluacionEstudiante save(EvaluacionEstudiante evaluacionEstudiante) {
        return evaluacionEstudianteRepository.save(evaluacionEstudiante);
    }

    public void deleteById(Integer id) {
        evaluacionEstudianteRepository.deleteById(id);
    }

    public EvaluacionEstudiante saveEvaluacionConEncuesta(EvaluacionEstudiante evaluacionEstudiante, Integer oidEncuesta) {
        // Guardar EvaluacionEstudiante
        EvaluacionEstudiante savedEvaluacion = evaluacionEstudianteRepository.save(evaluacionEstudiante);

        // Guardar EncuestaEstudiante
        Encuesta encuesta = encuestaRepository.findById(oidEncuesta)
                .orElseThrow(() -> new RuntimeException("Encuesta no encontrada"));

        EncuestaEstudiante encuestaEstudiante = new EncuestaEstudiante();
        encuestaEstudiante.setEncuesta(encuesta);
        encuestaEstudiante.setEvaluacionEstudiante(savedEvaluacion);

        encuestaEstudianteRepository.save(encuestaEstudiante);

        return savedEvaluacion;
    }
}
