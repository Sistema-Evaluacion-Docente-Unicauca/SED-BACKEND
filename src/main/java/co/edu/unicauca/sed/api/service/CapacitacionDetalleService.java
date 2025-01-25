package co.edu.unicauca.sed.api.service;

import co.edu.unicauca.sed.api.model.CapacitacionDetalle;
import co.edu.unicauca.sed.api.repository.CapacitacionDetalleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CapacitacionDetalleService {

    private static final Logger logger = LoggerFactory.getLogger(CapacitacionDetalleService.class);

    @Autowired
    private CapacitacionDetalleRepository repository;

    public CapacitacionDetalle create(CapacitacionDetalle detalle) {
        logger.info("Creando CapacitacionDetalle: {}", detalle);
        return repository.save(detalle);
    }

    public Optional<CapacitacionDetalle> findById(Integer id) {
        logger.info("Buscando CapacitacionDetalle con id: {}", id);
        return repository.findById(id);
    }

    public Page<CapacitacionDetalle> findAll(Pageable pageable) {
        logger.info("Listando CapacitacionDetalle con paginación");
        return repository.findAll(pageable);
    }

    public CapacitacionDetalle update(Integer id, CapacitacionDetalle detalle) {
        try {
            logger.info("Actualizando CapacitacionDetalle con id: {}", id);
            return repository.findById(id).map(existing -> {
                existing.setActoAdministrativo(detalle.getActoAdministrativo());
                existing.setDetalle(detalle.getDetalle());
                return repository.save(existing);
            }).orElseThrow(() -> {
                logger.error("CapacitacionDetalle no encontrado con id: {}", id);
                return new RuntimeException("CapacitacionDetalle no encontrado con id: " + id);
            });
        } catch (Exception e) {
            logger.error("Error actualizando CapacitacionDetalle con id: {}", id, e);
            throw new RuntimeException("Error actualizando CapacitacionDetalle con id: " + id, e);
        }
    }

    public void delete(Integer id) {
        try {
            logger.info("Eliminando CapacitacionDetalle con id: {}", id);
            if (!repository.existsById(id)) {
                logger.error("CapacitacionDetalle no encontrado para eliminar con id: {}", id);
                throw new RuntimeException("CapacitacionDetalle no encontrado para eliminar con id: " + id);
            }
            repository.deleteById(id);
        } catch (Exception e) {
            logger.error("Error eliminando CapacitacionDetalle con id: {}", id, e);
            throw new RuntimeException("Error eliminando CapacitacionDetalle con id: " + id, e);
        }
    }
}
