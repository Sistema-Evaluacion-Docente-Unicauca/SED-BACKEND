package co.edu.unicauca.sed.api.service;

import co.edu.unicauca.sed.api.model.ExtensionDetalle;
import co.edu.unicauca.sed.api.repository.ExtensionDetalleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ExtensionDetalleService {

    private static final Logger logger = LoggerFactory.getLogger(ExtensionDetalleService.class);

    @Autowired
    private ExtensionDetalleRepository repository;

    public ExtensionDetalle create(ExtensionDetalle detalle) {
        logger.info("Creando ExtensionDetalle: {}", detalle);
        return repository.save(detalle);
    }

    public Optional<ExtensionDetalle> findById(Integer id) {
        logger.info("Buscando ExtensionDetalle con id: {}", id);
        return repository.findById(id);
    }

    public Page<ExtensionDetalle> findAll(Pageable pageable) {
        logger.info("Listando ExtensionDetalle con paginación");
        return repository.findAll(pageable);
    }

    public ExtensionDetalle update(Integer id, ExtensionDetalle detalle) {
        try {
            logger.info("Actualizando ExtensionDetalle con id: {}", id);
            return repository.findById(id).map(existing -> {
                existing.setActoAdministrativo(detalle.getActoAdministrativo());
                existing.setNombreProyecto(detalle.getNombreProyecto());
                existing.setActividad(detalle.getActividad());
                return repository.save(existing);
            }).orElseThrow(() -> {
                logger.error("ExtensionDetalle no encontrado con id: {}", id);
                return new RuntimeException("ExtensionDetalle no encontrado con id: " + id);
            });
        } catch (Exception e) {
            logger.error("Error actualizando ExtensionDetalle con id: {}", id, e);
            throw new RuntimeException("Error actualizando ExtensionDetalle con id: " + id, e);
        }
    }

    public void delete(Integer id) {
        try {
            logger.info("Eliminando ExtensionDetalle con id: {}", id);
            if (!repository.existsById(id)) {
                logger.error("ExtensionDetalle no encontrado para eliminar con id: {}", id);
                throw new RuntimeException("ExtensionDetalle no encontrado para eliminar con id: " + id);
            }
            repository.deleteById(id);
        } catch (Exception e) {
            logger.error("Error eliminando ExtensionDetalle con id: {}", id, e);
            throw new RuntimeException("Error eliminando ExtensionDetalle con id: " + id, e);
        }
    }
}
