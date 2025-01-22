package co.edu.unicauca.sed.api.service;

import co.edu.unicauca.sed.api.model.ProyectoInvestigacionDetalle;
import co.edu.unicauca.sed.api.repository.ProyectoInvestigacionDetalleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProyectoInvestigacionDetalleService {

    private static final Logger logger = LoggerFactory.getLogger(ProyectoInvestigacionDetalleService.class);

    @Autowired
    private ProyectoInvestigacionDetalleRepository repository;

    public ProyectoInvestigacionDetalle create(ProyectoInvestigacionDetalle detalle) {
        logger.info("Creando ProyectoInvestigacionDetalle: {}", detalle);
        return repository.save(detalle);
    }

    public Optional<ProyectoInvestigacionDetalle> findById(Integer id) {
        logger.info("Buscando ProyectoInvestigacionDetalle con id: {}", id);
        return repository.findById(id);
    }

    public Page<ProyectoInvestigacionDetalle> findAll(Pageable pageable) {
        logger.info("Listando ProyectoInvestigacionDetalle con paginación");
        return repository.findAll(pageable);
    }

    public ProyectoInvestigacionDetalle update(Integer id, ProyectoInvestigacionDetalle detalle) {
        try {
            logger.info("Actualizando ProyectoInvestigacionDetalle con id: {}", id);
            return repository.findById(id).map(existing -> {
                existing.setVri(detalle.getVri());
                existing.setNombreProyecto(detalle.getNombreProyecto());
                existing.setActividad(detalle.getActividad());
                return repository.save(existing);
            }).orElseThrow(() -> {
                logger.error("ProyectoInvestigacionDetalle no encontrado con id: {}", id);
                return new RuntimeException("ProyectoInvestigacionDetalle no encontrado con id: " + id);
            });
        } catch (Exception e) {
            logger.error("Error actualizando ProyectoInvestigacionDetalle con id: {}", id, e);
            throw new RuntimeException("Error actualizando ProyectoInvestigacionDetalle con id: " + id, e);
        }
    }

    public void delete(Integer id) {
        try {
            logger.info("Eliminando ProyectoInvestigacionDetalle con id: {}", id);
            if (!repository.existsById(id)) {
                logger.error("ProyectoInvestigacionDetalle no encontrado para eliminar con id: {}", id);
                throw new RuntimeException("ProyectoInvestigacionDetalle no encontrado para eliminar con id: " + id);
            }
            repository.deleteById(id);
        } catch (Exception e) {
            logger.error("Error eliminando ProyectoInvestigacionDetalle con id: {}", id, e);
            throw new RuntimeException("Error eliminando ProyectoInvestigacionDetalle con id: " + id, e);
        }
    }
}
