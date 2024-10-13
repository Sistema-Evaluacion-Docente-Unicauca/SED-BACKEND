package co.edu.unicauca.sed.api.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.edu.unicauca.sed.api.model.TipoActividad;
import co.edu.unicauca.sed.api.repository.TipoActividadRepository;

@Service
public class TipoActividadService {

    @Autowired
    private TipoActividadRepository tipoActividadRepository;

    public List<TipoActividad> findAll() {
        List<TipoActividad> list = new ArrayList<>();
        this.tipoActividadRepository.findAll().forEach(list::add);
        return list;
    }

    public TipoActividad findByOid(Integer oid) {
        Optional<TipoActividad> resultado = this.tipoActividadRepository.findById(oid);

        if (resultado.isPresent()) {
            return resultado.get();
        }

        return null;
    }

    public TipoActividad save(TipoActividad tipoActividad) {
        TipoActividad result = null;
        try {
            result = this.tipoActividadRepository.save(tipoActividad);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return result;
    }

    public void delete(Integer oid) {
        this.tipoActividadRepository.deleteById(oid);
    }
}
