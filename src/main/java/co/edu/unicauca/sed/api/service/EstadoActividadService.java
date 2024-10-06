package co.edu.unicauca.sed.api.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.edu.unicauca.sed.api.model.EstadoActividad;
import co.edu.unicauca.sed.api.repository.EstadoActividadRepository;

@Service
public class EstadoActividadService {
    @Autowired
    private EstadoActividadRepository estadoActividadRepository;

    // Método para obtener todas las instancias de EstadoActividad
    public List<EstadoActividad> findAll() {
        List<EstadoActividad> list = new ArrayList<>();
        this.estadoActividadRepository.findAll().forEach(list::add);
        return list;
    }

    // Método para encontrar una instancia de EstadoActividad por OID
    public EstadoActividad findByOid(Integer oid) {
        Optional<EstadoActividad> resultado = this.estadoActividadRepository.findById(oid);

        if (resultado.isPresent()) {
            return resultado.get();
        }

        return null;
    }

    // Método para guardar una instancia de EstadoActividad
    public EstadoActividad save(EstadoActividad estadoActividad) {
        EstadoActividad result = null;
        try {
            result = this.estadoActividadRepository.save(estadoActividad);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return result;
    }

    // Método para eliminar una instancia de EstadoActividad por OID
    public void delete(Integer oid) {
        this.estadoActividadRepository.deleteById(oid);
    }
}
