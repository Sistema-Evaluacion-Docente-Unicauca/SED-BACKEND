package co.edu.unicauca.sed.api.service;

import co.edu.unicauca.sed.api.model.TrabajoDocenciaDetalle;
import co.edu.unicauca.sed.api.repository.TrabajoDocenciaDetalleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TrabajoDocenciaDetalleService {

    private static final Logger logger = LoggerFactory.getLogger(TrabajoDocenciaDetalleService.class);

    @Autowired
    private TrabajoDocenciaDetalleRepository repository;

    public TrabajoDocenciaDetalle create(TrabajoDocenciaDetalle detalle) {
        logger.info("Creando TrabajoDocenciaDetalle: {}", detalle);
        return repository.save(detalle);
    }

    public Optional<TrabajoDocenciaDetalle> findById(Integer id) {
        logger.info("Buscando TrabajoDocenciaDetalle con id: {}", id);
        return repository.findById(id);
    }

    public Page<TrabajoDocenciaDetalle> findAll(Pageable pageable) {
        logger.info("Listando TrabajoDocenciaDetalle con paginación");
        return repository.findAll(pageable);
    }

    public TrabajoDocenciaDetalle update(Integer id, TrabajoDocenciaDetalle detalle) {
        try {
            logger.info("Actualizando TrabajoDocenciaDetalle con id: {}", id);
            return repository.findById(id).map(existing -> {
                existing.setActoAdministrativo(detalle.getActoAdministrativo());
                existing.setActividad(detalle.getActividad());
                return repository.save(existing);
            }).orElseThrow(() -> {
                logger.error("TrabajoDocenciaDetalle no encontrado con id: {}", id);
                return new RuntimeException("TrabajoDocenciaDetalle no encontrado con id: " + id);
            });
        } catch (Exception e) {
            logger.error("Error actualizando TrabajoDocenciaDetalle con id: {}", id, e);
            throw new RuntimeException("Error actualizando TrabajoDocenciaDetalle con id: " + id, e);
        }
    }

    public void delete(Integer id) {
        try {
            logger.info("Eliminando TrabajoDocenciaDetalle con id: {}", id);
            if (!repository.existsById(id)) {
                logger.error("TrabajoDocenciaDetalle no encontrado para eliminar con id: {}", id);
                throw new RuntimeException("TrabajoDocenciaDetalle no encontrado para eliminar con id: " + id);
            }
            repository.deleteById(id);
        } catch (Exception e) {
            logger.error("Error eliminando TrabajoDocenciaDetalle con id: {}", id, e);
            throw new RuntimeException("Error eliminando TrabajoDocenciaDetalle con id: " + id, e);
        }
    }
}
