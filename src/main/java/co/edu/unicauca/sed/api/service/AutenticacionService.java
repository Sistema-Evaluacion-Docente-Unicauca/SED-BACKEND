package co.edu.unicauca.sed.api.service;

import co.edu.unicauca.sed.api.model.Autenticacion;
import co.edu.unicauca.sed.api.repository.AutenticacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AutenticacionService {

    @Autowired
    private AutenticacionRepository autenticacionRepository;

    public List<Autenticacion> findAll() {
        return (List<Autenticacion>) autenticacionRepository.findAll();
    }

    public Autenticacion findByOid(Integer id) {
        Optional<Autenticacion> resultado = this.autenticacionRepository.findById(id);

        if (resultado.isPresent()) {
            return resultado.get();
        }

        return null;
    }

    public Autenticacion save(Autenticacion autenticacion) {
        Autenticacion result = null;
        try {
            result = this.autenticacionRepository.save(autenticacion);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return result;
    }

    public void deleteById(Integer id) {
        autenticacionRepository.deleteById(id);
    }
}
