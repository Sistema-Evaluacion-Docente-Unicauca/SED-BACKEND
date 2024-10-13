package co.edu.unicauca.sed.api.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.unicauca.sed.api.model.Actividad;
import co.edu.unicauca.sed.api.model.Autoevaluacion;
import co.edu.unicauca.sed.api.model.Fuente;
import co.edu.unicauca.sed.api.repository.ActividadRepository;
import co.edu.unicauca.sed.api.repository.FuenteRepository;

@Service
public class ActividadService {

    @Autowired
    private ActividadRepository actividadRepository;

    @Autowired
    private FuenteRepository fuenteRepository;

    /**
     * Encuentra todas las actividades junto con sus fuentes asociadas.
     */
    public List<Actividad> findAll() {
        List<Actividad> list = new ArrayList<>();
        actividadRepository.findAllWithFuentes().forEach(list::add);
        return list;
    }

    /**
     * Encuentra una actividad por su OID y carga sus fuentes asociadas.
     */
    public Actividad findByOid(Integer oid) {
        Optional<Actividad> resultado = actividadRepository.findByOidWithFuentes(oid);

        if (resultado.isPresent()) {
            return resultado.get();
        }

        return null;
    }

    @Transactional
    public Actividad save(Actividad actividad) {
        Actividad result = null;
        try {
            result = this.actividadRepository.save(actividad);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return result;
    }

    /**
     * Elimina una actividad por su OID.
     */
    public void delete(Integer oid) {
        actividadRepository.deleteById(oid);
    }
}
