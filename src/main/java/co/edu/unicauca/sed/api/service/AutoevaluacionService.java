package co.edu.unicauca.sed.api.service;

import co.edu.unicauca.sed.api.domain.Autoevaluacion;
import co.edu.unicauca.sed.api.repository.AutoevaluacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AutoevaluacionService {

    @Autowired
    private AutoevaluacionRepository autoevaluacionRepository;

    public List<Autoevaluacion> findAll() {
        List<Autoevaluacion> list = new ArrayList<>();
        this.autoevaluacionRepository.findAll().forEach(list::add);
        return list;
    }

    public Autoevaluacion findByOid(Integer id) {
        Optional<Autoevaluacion> resultado = this.autoevaluacionRepository.findById(id);

        if (resultado.isPresent()) {
            return resultado.get();
        }

        return null;
    }

    public Autoevaluacion save(Autoevaluacion autoevaluacion) {
        Autoevaluacion result = null;
        try {
            result = this.autoevaluacionRepository.save(autoevaluacion);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return result;
    }

    public void delete(Integer id) {
        this.autoevaluacionRepository.deleteById(id);
    }
}
