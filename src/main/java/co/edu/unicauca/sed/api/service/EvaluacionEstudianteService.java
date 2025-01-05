package co.edu.unicauca.sed.api.service;

import co.edu.unicauca.sed.api.model.Encuesta;
import co.edu.unicauca.sed.api.model.EncuestaEstudiante;
import co.edu.unicauca.sed.api.model.EvaluacionEstudiante;
import co.edu.unicauca.sed.api.repository.EncuestaEstudianteRepository;
import co.edu.unicauca.sed.api.repository.EncuestaRepository;
import co.edu.unicauca.sed.api.repository.EvaluacionEstudianteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class EvaluacionEstudianteService {

    @Autowired
    private EvaluacionEstudianteRepository evaluacionEstudianteRepository;

    @Autowired
    private EncuestaRepository encuestaRepository;

    @Autowired
    private EncuestaEstudianteRepository encuestaEstudianteRepository;

    public List<EvaluacionEstudiante> findAll() {
        List<EvaluacionEstudiante> list = new ArrayList<>();
        this.evaluacionEstudianteRepository.findAll().forEach(list::add);
        return list;
    }

    public EvaluacionEstudiante findByOid(Integer oid) {
        Optional<EvaluacionEstudiante> resultado = this.evaluacionEstudianteRepository.findById(oid);

        if (resultado.isPresent()) {
            return resultado.get();
        }

        return null;
    }

    public EvaluacionEstudiante save(EvaluacionEstudiante evaluacionEstudiante) {
        EvaluacionEstudiante result = null;
        try {
            if (evaluacionEstudiante.getObservacion() != null) {
                evaluacionEstudiante.setObservacion(evaluacionEstudiante.getObservacion().toUpperCase());
            }
            result = this.evaluacionEstudianteRepository.save(evaluacionEstudiante);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return result;
    }

    public void delete(Integer oid) {
        this.evaluacionEstudianteRepository.deleteById(oid);
    }

    public EvaluacionEstudiante saveEvaluacionConEncuesta(EvaluacionEstudiante evaluacionEstudiante, Integer oidEncuesta) {
        EvaluacionEstudiante savedEvaluacion = null;
        try {
            savedEvaluacion = evaluacionEstudianteRepository.save(evaluacionEstudiante);

            Encuesta encuesta = encuestaRepository.findById(oidEncuesta).orElseThrow(() -> new RuntimeException("Encuesta no encontrada"));

            EncuestaEstudiante encuestaEstudiante = new EncuestaEstudiante();
            encuestaEstudiante.setEncuesta(encuesta);
            encuestaEstudiante.setEvaluacionEstudiante(savedEvaluacion);

            encuestaEstudianteRepository.save(encuestaEstudiante);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return savedEvaluacion;
    }
}
