package co.edu.unicauca.sed.api.service;

import co.edu.unicauca.sed.api.model.Actividad;
import co.edu.unicauca.sed.api.model.EstadoActividad;
import co.edu.unicauca.sed.api.repository.EstadoActividadRepository;
import co.edu.unicauca.sed.api.utils.StringUtils;

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

    @Autowired
    private StringUtils stringUtils;

    public EstadoActividad create(EstadoActividad estadoActividad) {
        logger.info("Creando EstadoActividad: {}", estadoActividad);
        estadoActividad.setNombre(stringUtils.safeToUpperCase(estadoActividad.getNombre()));
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
                existing.setNombre(estadoActividad.getNombre().toUpperCase());
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

        /**
     * Asigna el estado de la actividad si es válido.
     *
     * @param actividad          La actividad a actualizar.
     * @param oidEstadoActividad El ID del estado de actividad.
     */
    public void asignarEstadoActividad(Actividad actividad, Integer oidEstadoActividad) {
        EstadoActividad estadoExistente = repository.findById(oidEstadoActividad)
                .orElseThrow(() -> new IllegalArgumentException("Estado de actividad no válido."));
        actividad.setEstadoActividad(estadoExistente);
    }
}
