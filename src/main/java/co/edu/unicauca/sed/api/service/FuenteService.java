package co.edu.unicauca.sed.api.service;

import co.edu.unicauca.sed.api.model.Actividad;
import co.edu.unicauca.sed.api.model.Fuente;
import co.edu.unicauca.sed.api.repository.ActividadRepository;
import co.edu.unicauca.sed.api.repository.FuenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class FuenteService {

    @Autowired
    private FuenteRepository fuenteRepository;

    @Autowired
    private ActividadRepository actividadRepository;

    public List<Fuente> findAll() {
        List<Fuente> list = new ArrayList<>();
        this.fuenteRepository.findAll().forEach(list::add);
        return list;
    }

    public Fuente findByOid(Integer oid) {
        Optional<Fuente> resultado = this.fuenteRepository.findById(oid);

        if (resultado.isPresent()) {
            return resultado.get();
        }

        return null;
    }

    public Fuente save(Fuente fuente) {
        Fuente result = null;
        try {
            result = this.fuenteRepository.save(fuente);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return result;
    }

    public void delete(Integer oid) {
        this.fuenteRepository.deleteById(oid);
    }

    public List<Fuente> findByActividadOid(Integer oidActividad) {
        Actividad actividad = actividadRepository.findById(oidActividad).orElse(null);
        if (actividad != null) {
            return fuenteRepository.findByActividad(actividad);
        }
        return null;
    }
}
