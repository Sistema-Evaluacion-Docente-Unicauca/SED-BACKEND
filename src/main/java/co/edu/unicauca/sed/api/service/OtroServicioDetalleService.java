package co.edu.unicauca.sed.api.service;

import co.edu.unicauca.sed.api.model.OtroServicioDetalle;
import co.edu.unicauca.sed.api.repository.OtroServicioDetalleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class OtroServicioDetalleService {

    private static final Logger logger = LoggerFactory.getLogger(OtroServicioDetalleService.class);

    @Autowired
    private OtroServicioDetalleRepository repository;

    public OtroServicioDetalle create(OtroServicioDetalle detalle) {
        logger.info("Creando OtroServicioDetalle: {}", detalle);
        return repository.save(detalle);
    }

    public Optional<OtroServicioDetalle> findById(Integer id) {
        logger.info("Buscando OtroServicioDetalle con id: {}", id);
        return repository.findById(id);
    }

    public Page<OtroServicioDetalle> findAll(Pageable pageable) {
        logger.info("Listando OtroServicioDetalle con paginación");
        return repository.findAll(pageable);
    }

    public OtroServicioDetalle update(Integer id, OtroServicioDetalle detalle) {
        try {
            logger.info("Actualizando OtroServicioDetalle con id: {}", id);
            return repository.findById(id).map(existing -> {
                existing.setActoAdministrativo(detalle.getActoAdministrativo());
                existing.setDetalle(detalle.getDetalle());
                existing.setActividad(detalle.getActividad());
                return repository.save(existing);
            }).orElseThrow(() -> {
                logger.error("OtroServicioDetalle no encontrado con id: {}", id);
                return new RuntimeException("OtroServicioDetalle no encontrado con id: " + id);
            });
        } catch (Exception e) {
            logger.error("Error actualizando OtroServicioDetalle con id: {}", id, e);
            throw new RuntimeException("Error actualizando OtroServicioDetalle con id: " + id, e);
        }
    }

    public void delete(Integer id) {
        try {
            logger.info("Eliminando OtroServicioDetalle con id: {}", id);
            if (!repository.existsById(id)) {
                logger.error("OtroServicioDetalle no encontrado para eliminar con id: {}", id);
                throw new RuntimeException("OtroServicioDetalle no encontrado para eliminar con id: " + id);
            }
            repository.deleteById(id);
        } catch (Exception e) {
            logger.error("Error eliminando OtroServicioDetalle con id: {}", id, e);
            throw new RuntimeException("Error eliminando OtroServicioDetalle con id: " + id, e);
        }
    }
}
