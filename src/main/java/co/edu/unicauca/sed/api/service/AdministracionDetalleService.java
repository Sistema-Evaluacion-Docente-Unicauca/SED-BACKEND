package co.edu.unicauca.sed.api.service;

import co.edu.unicauca.sed.api.model.AdministracionDetalle;
import co.edu.unicauca.sed.api.repository.AdministracionDetalleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AdministracionDetalleService {

    private static final Logger logger = LoggerFactory.getLogger(AdministracionDetalleService.class);

    @Autowired
    private AdministracionDetalleRepository repository;

    public AdministracionDetalle create(AdministracionDetalle detalle) {
        logger.info("Creando AdministracionDetalle: {}", detalle);
        return repository.save(detalle);
    }

    public Optional<AdministracionDetalle> findById(Integer id) {
        logger.info("Buscando AdministracionDetalle con id: {}", id);
        return repository.findById(id);
    }

    public Page<AdministracionDetalle> findAll(Pageable pageable) {
        logger.info("Listando AdministracionDetalle con paginación");
        return repository.findAll(pageable);
    }

    public AdministracionDetalle update(Integer id, AdministracionDetalle detalle) {
        logger.info("Actualizando AdministracionDetalle con id: {}", id);
        return repository.findById(id).map(existing -> {
            existing.setActoAdministrativo(detalle.getActoAdministrativo());
            existing.setDetalle(detalle.getDetalle());
            return repository.save(existing);
        }).orElseThrow(() -> {
            logger.error("AdministracionDetalle no encontrado con id: {}", id);
            return new RuntimeException("AdministracionDetalle no encontrado con id: " + id);
        });
    }

    public void delete(Integer id) {
        logger.info("Eliminando AdministracionDetalle con id: {}", id);
        if (!repository.existsById(id)) {
            logger.error("AdministracionDetalle no encontrado para eliminar con id: {}", id);
            throw new RuntimeException("AdministracionDetalle no encontrado para eliminar con id: " + id);
        }
        repository.deleteById(id);
    }
}
