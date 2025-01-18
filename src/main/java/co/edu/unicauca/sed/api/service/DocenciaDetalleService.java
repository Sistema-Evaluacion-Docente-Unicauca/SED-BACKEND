package co.edu.unicauca.sed.api.service;

import co.edu.unicauca.sed.api.model.DocenciaDetalle;
import co.edu.unicauca.sed.api.repository.DocenciaDetalleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DocenciaDetalleService {

    private static final Logger logger = LoggerFactory.getLogger(DocenciaDetalleService.class);

    @Autowired
    private DocenciaDetalleRepository repository;

    public DocenciaDetalle create(DocenciaDetalle detalle) {
        logger.info("Creando DocenciaDetalle: {}", detalle);
        return repository.save(detalle);
    }

    public Optional<DocenciaDetalle> findById(Integer id) {
        logger.info("Buscando DocenciaDetalle con id: {}", id);
        return repository.findById(id);
    }

    public Page<DocenciaDetalle> findAll(Pageable pageable) {
        logger.info("Listando DocenciaDetalle con paginación");
        return repository.findAll(pageable);
    }

    public DocenciaDetalle update(Integer id, DocenciaDetalle detalle) {
        try {
            logger.info("Actualizando DocenciaDetalle con id: {}", id);
            return repository.findById(id).map(existing -> {
                existing.setCodigo(detalle.getCodigo());
                existing.setGrupo(detalle.getGrupo());
                existing.setMateria(detalle.getMateria());
                existing.setActividad(detalle.getActividad());
                return repository.save(existing);
            }).orElseThrow(() -> {
                logger.error("DocenciaDetalle no encontrado con id: {}", id);
                return new RuntimeException("DocenciaDetalle no encontrado con id: " + id);
            });
        } catch (Exception e) {
            logger.error("Error actualizando DocenciaDetalle con id: {}", id, e);
            throw new RuntimeException("Error actualizando DocenciaDetalle con id: " + id, e);
        }
    }

    public void delete(Integer id) {
        try {
            logger.info("Eliminando DocenciaDetalle con id: {}", id);
            if (!repository.existsById(id)) {
                logger.error("DocenciaDetalle no encontrado para eliminar con id: {}", id);
                throw new RuntimeException("DocenciaDetalle no encontrado para eliminar con id: " + id);
            }
            repository.deleteById(id);
        } catch (Exception e) {
            logger.error("Error eliminando DocenciaDetalle con id: {}", id, e);
            throw new RuntimeException("Error eliminando DocenciaDetalle con id: " + id, e);
        }
    }
}
