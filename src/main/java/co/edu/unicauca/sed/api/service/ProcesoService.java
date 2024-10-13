package co.edu.unicauca.sed.api.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.edu.unicauca.sed.api.model.Proceso;
import co.edu.unicauca.sed.api.repository.ProcesoRepository;

@Service
public class ProcesoService {

    @Autowired
    private ProcesoRepository procesoRepository;

    public List<Proceso> findAll() {
        List<Proceso> list = new ArrayList<>();
        this.procesoRepository.findAll().forEach(list::add);
        return list;
    }

    public Proceso findByOid(Integer oid) {
        Optional<Proceso> resultado = this.procesoRepository.findById(oid);

        if (resultado.isPresent()) {
            return resultado.get();
        }

        return null;
    }

    public Proceso save(Proceso proceso) {
        Proceso result = null;
        try {
            result = this.procesoRepository.save(proceso);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return result;
    }

    public void delete(Integer oid) {
        this.procesoRepository.deleteById(oid);
    }
}
