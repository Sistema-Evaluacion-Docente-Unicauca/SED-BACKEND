package co.edu.unicauca.sed.api.service;

import co.edu.unicauca.sed.api.model.Encuesta;
import co.edu.unicauca.sed.api.repository.EncuestaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class EncuestaService {

    @Autowired
    private EncuestaRepository encuestaRepository;

    public List<Encuesta> findAll() {
        List<Encuesta> list = new ArrayList<>();
        this.encuestaRepository.findAll().forEach(list::add);
        return list;
    }

    public Encuesta findByOid(Integer oid) {
        Optional<Encuesta> resultado = this.encuestaRepository.findById(oid);

        if (resultado.isPresent()) {
            return resultado.get();
        }

        return null;
    }

    public Encuesta save(Encuesta encuesta) {
        Encuesta result = null;
        try {
            result = this.encuestaRepository.save(encuesta);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return result;
    }

    public void delete(Integer oid) {
        this.encuestaRepository.deleteById(oid);
    }
}
