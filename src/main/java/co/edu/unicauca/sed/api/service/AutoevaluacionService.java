package co.edu.unicauca.sed.api.service;

import co.edu.unicauca.sed.api.model.Autoevaluacion;
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

    // Método para obtener todas las autoevaluaciones
    public List<Autoevaluacion> findAll() {
        List<Autoevaluacion> list = new ArrayList<>();
        this.autoevaluacionRepository.findAll().forEach(list::add);
        return list;
    }

    // Método para encontrar una autoevaluación por ID
    public Autoevaluacion findByOid(Integer id) {
        Optional<Autoevaluacion> resultado = this.autoevaluacionRepository.findById(id);

        if (resultado.isPresent()) {
            return resultado.get();
        }

        return null;
    }

    // Método para guardar una autoevaluación
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

    // Método para eliminar una autoevaluación por ID
    public void delete(Integer id) {
        this.autoevaluacionRepository.deleteById(id);
    }
}
