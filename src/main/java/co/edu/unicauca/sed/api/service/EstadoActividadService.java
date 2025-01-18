package co.edu.unicauca.sed.api.service;

import co.edu.unicauca.sed.api.model.EstadoActividad;
import co.edu.unicauca.sed.api.repository.EstadoActividadRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class EstadoActividadService {

    private static final Logger logger = LoggerFactory.getLogger(EstadoActividadService.class);

    @Autowired
    private EstadoActividadRepository repository;

    public EstadoActividad create(EstadoActividad estadoActividad) {
        logger.info("Creando EstadoActividad: {}", estadoActividad);
        return repository.save(estadoActividad);
    }

    public Optional<EstadoActividad> findById(Integer id) {
        logger.info("Buscando EstadoActividad con id: {}", id);
        return repository.findById(id);
    }

    public Page<EstadoActividad> findAll(Pageable pageable) {
        logger.info("Listando EstadoActividad con paginación");
        return repository.findAll(pageable);
    }

    public EstadoActividad update(Integer id, EstadoActividad estadoActividad) {
        try {
            logger.info("Actualizando EstadoActividad con id: {}", id);
            return repository.findById(id).map(existing -> {
                existing.setNombre(estadoActividad.getNombre());
                return repository.save(existing);
            }).orElseThrow(() -> {
                logger.error("EstadoActividad no encontrado con id: {}", id);
                return new RuntimeException("EstadoActividad no encontrado con id: " + id);
            });
        } catch (Exception e) {
            logger.error("Error actualizando EstadoActividad con id: {}", id, e);
            throw new RuntimeException("Error actualizando EstadoActividad con id: " + id, e);
        }
    }

    public void delete(Integer id) {
        try {
            logger.info("Eliminando EstadoActividad con id: {}", id);
            if (!repository.existsById(id)) {
                logger.error("EstadoActividad no encontrado para eliminar con id: {}", id);
                throw new RuntimeException("EstadoActividad no encontrado para eliminar con id: " + id);
            }
            repository.deleteById(id);
        } catch (Exception e) {
            logger.error("Error eliminando EstadoActividad con id: {}", id, e);
            throw new RuntimeException("Error eliminando EstadoActividad con id: " + id, e);
        }
    }
}
