package co.edu.unicauca.sed.api.service.encuesta;

import co.edu.unicauca.sed.api.domain.EncuestaPregunta;
import co.edu.unicauca.sed.api.repository.EncuestaPreguntaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class EncuestaPreguntaService {

    @Autowired
    private EncuestaPreguntaRepository encuestaPreguntaRepository;

    public List<EncuestaPregunta> findAll() {
        // Convertir Iterable a List usando StreamSupport
        return StreamSupport.stream(encuestaPreguntaRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    public EncuestaPregunta findByOid(Integer oid) {
        return encuestaPreguntaRepository.findById(oid).orElse(null);
    }

    public EncuestaPregunta save(EncuestaPregunta encuestaPregunta, Integer oidEncuesta, Integer oidPregunta) {
        return encuestaPreguntaRepository.save(encuestaPregunta);
    }

    public void delete(Integer oid) {
        encuestaPreguntaRepository.deleteById(oid);
    }
}
