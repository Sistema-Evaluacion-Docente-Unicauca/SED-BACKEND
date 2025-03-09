package co.edu.unicauca.sed.api.service;

import co.edu.unicauca.sed.api.domain.EncuestaEstudiante;
import co.edu.unicauca.sed.api.repository.EncuestaEstudianteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class EncuestaEstudianteService {

    @Autowired
    private EncuestaEstudianteRepository encuestaEstudianteRepository;

    public List<EncuestaEstudiante> findAll() {
        List<EncuestaEstudiante> list = new ArrayList<>();
        this.encuestaEstudianteRepository.findAll().forEach(list::add);
        return list;
    }

    public EncuestaEstudiante findByOid(Integer oid) {
        Optional<EncuestaEstudiante> resultado = this.encuestaEstudianteRepository.findById(oid);

        if (resultado.isPresent()) {
            return resultado.get();
        }

        return null;
    }

    public EncuestaEstudiante save(EncuestaEstudiante encuestaEstudiante) {
        EncuestaEstudiante result = null;
        try {
            result = this.encuestaEstudianteRepository.save(encuestaEstudiante);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return result;
    }

    public void delete(Integer oid) {
        this.encuestaEstudianteRepository.deleteById(oid);
    }
}
