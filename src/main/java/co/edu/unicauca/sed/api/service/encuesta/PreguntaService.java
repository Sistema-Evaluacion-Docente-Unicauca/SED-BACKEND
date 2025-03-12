package co.edu.unicauca.sed.api.service.encuesta;

import co.edu.unicauca.sed.api.domain.Pregunta;
import co.edu.unicauca.sed.api.repository.PreguntaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PreguntaService {

    @Autowired
    private PreguntaRepository preguntaRepository;

    public List<Pregunta> findAll() {
        List<Pregunta> list = new ArrayList<>();
        this.preguntaRepository.findAll().forEach(list::add);
        return list;
    }

    public Pregunta findByOid(Integer oid) {
        Optional<Pregunta> resultado = this.preguntaRepository.findById(oid);

        if (resultado.isPresent()) {
            return resultado.get();
        }

        return null;
    }

    public Pregunta save(Pregunta pregunta) {
        Pregunta result = null;
        try {
            if (pregunta.getPregunta() != null) {
                pregunta.setPregunta(pregunta.getPregunta().toUpperCase());
            }
            result = this.preguntaRepository.save(pregunta);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return result;
    }

    public List<Pregunta> saveAll(List<Pregunta> preguntas) {
        List<Pregunta> savedPreguntas = new ArrayList<>();
        preguntaRepository.saveAll(preguntas).forEach(savedPreguntas::add);
        return savedPreguntas;
    }

    public void delete(Integer oid) {
        this.preguntaRepository.deleteById(oid);
    }
}
