package co.edu.unicauca.sed.api.service;

import co.edu.unicauca.sed.api.model.Actividad;
import co.edu.unicauca.sed.api.repository.ActividadRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ActividadService {

    @Autowired
    private ActividadRepository actividadRepository;

    // Método para encontrar todas las actividades
    public List<Actividad> findAll() {
        List<Actividad> list = new ArrayList<>();
        this.actividadRepository.findAll().forEach(list::add);
        return list;
    }

    // Método para encontrar una actividad por su OID
    public Actividad findByOid(Integer oid) {
        Optional<Actividad> resultado = this.actividadRepository.findById(oid);

        if (resultado.isPresent()) {
            return resultado.get();
        }

        return null;
    }

    // Método para guardar una actividad
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

    // Método para eliminar una actividad por su ID
    public void delete(Integer oid) {
        this.actividadRepository.deleteById(oid);
    }
}
