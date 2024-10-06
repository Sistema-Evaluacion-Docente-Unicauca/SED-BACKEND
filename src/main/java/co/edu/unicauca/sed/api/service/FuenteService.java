package co.edu.unicauca.sed.api.service;

import co.edu.unicauca.sed.api.model.Fuente;
import co.edu.unicauca.sed.api.repository.FuenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class FuenteService {

    @Autowired
    private FuenteRepository fuenteRepository;

    public Iterable<Fuente> findAll() {
        return fuenteRepository.findAll();
    }

    public Optional<Fuente> findById(Integer id) {
        return fuenteRepository.findById(id);
    }

    public Fuente save(Fuente fuente) {
        return fuenteRepository.save(fuente);
    }

    public void deleteById(Integer id) {
        fuenteRepository.deleteById(id);
    }
}
