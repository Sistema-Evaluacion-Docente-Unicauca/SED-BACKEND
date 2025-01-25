package co.edu.unicauca.sed.api.service;

import co.edu.unicauca.sed.api.model.TrabajoInvestigacionDetalle;
import co.edu.unicauca.sed.api.repository.TrabajoInvestigacionDetalleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TrabajoInvestigacionDetalleService {

    private static final Logger logger = LoggerFactory.getLogger(TrabajoInvestigacionDetalleService.class);

    @Autowired
    private TrabajoInvestigacionDetalleRepository repository;

    public TrabajoInvestigacionDetalle create(TrabajoInvestigacionDetalle detalle) {
        logger.info("Creando TrabajoInvestigacionDetalle: {}", detalle);
        return repository.save(detalle);
    }

    public Optional<TrabajoInvestigacionDetalle> findById(Integer id) {
        logger.info("Buscando TrabajoInvestigacionDetalle con id: {}", id);
        return repository.findById(id);
    }

    public Page<TrabajoInvestigacionDetalle> findAll(Pageable pageable) {
        logger.info("Listando TrabajoInvestigacionDetalle con paginación");
        return repository.findAll(pageable);
    }

    public TrabajoInvestigacionDetalle update(Integer id, TrabajoInvestigacionDetalle detalle) {
        try {
            logger.info("Actualizando TrabajoInvestigacionDetalle con id: {}", id);
            return repository.findById(id).map(existing -> {
                existing.setActoAdministrativo(detalle.getActoAdministrativo());
                existing.setActividad(detalle.getActividad());
                return repository.save(existing);
            }).orElseThrow(() -> {
                logger.error("TrabajoInvestigacionDetalle no encontrado con id: {}", id);
                return new RuntimeException("TrabajoInvestigacionDetalle no encontrado con id: " + id);
            });
        } catch (Exception e) {
            logger.error("Error actualizando TrabajoInvestigacionDetalle con id: {}", id, e);
            throw new RuntimeException("Error actualizando TrabajoInvestigacionDetalle con id: " + id, e);
        }
    }

    public void delete(Integer id) {
        try {
            logger.info("Eliminando TrabajoInvestigacionDetalle con id: {}", id);
            if (!repository.existsById(id)) {
                logger.error("TrabajoInvestigacionDetalle no encontrado para eliminar con id: {}", id);
                throw new RuntimeException("TrabajoInvestigacionDetalle no encontrado para eliminar con id: " + id);
            }
            repository.deleteById(id);
        } catch (Exception e) {
            logger.error("Error eliminando TrabajoInvestigacionDetalle con id: {}", id, e);
            throw new RuntimeException("Error eliminando TrabajoInvestigacionDetalle con id: " + id, e);
        }
    }
}
