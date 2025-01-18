package co.edu.unicauca.sed.api.service;

import co.edu.unicauca.sed.api.model.EstadoUsuario;
import co.edu.unicauca.sed.api.repository.EstadoUsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class EstadoUsuarioService {

    private static final Logger logger = LoggerFactory.getLogger(EstadoUsuarioService.class);

    @Autowired
    private EstadoUsuarioRepository repository;

    public EstadoUsuario create(EstadoUsuario estadoUsuario) {
        logger.info("Creando EstadoUsuario: {}", estadoUsuario);
        return repository.save(estadoUsuario);
    }

    public Optional<EstadoUsuario> findById(Integer id) {
        logger.info("Buscando EstadoUsuario con id: {}", id);
        return repository.findById(id);
    }

    public Page<EstadoUsuario> findAll(Pageable pageable) {
        logger.info("Listando EstadoUsuario con paginación");
        return repository.findAll(pageable);
    }

    public EstadoUsuario update(Integer id, EstadoUsuario estadoUsuario) {
        try {
            logger.info("Actualizando EstadoUsuario con id: {}", id);
            return repository.findById(id).map(existing -> {
                existing.setNombre(estadoUsuario.getNombre());
                return repository.save(existing);
            }).orElseThrow(() -> {
                logger.error("EstadoUsuario no encontrado con id: {}", id);
                return new RuntimeException("EstadoUsuario no encontrado con id: " + id);
            });
        } catch (Exception e) {
            logger.error("Error actualizando EstadoUsuario con id: {}", id, e);
            throw new RuntimeException("Error actualizando EstadoUsuario con id: " + id, e);
        }
    }

    public void delete(Integer id) {
        try {
            logger.info("Eliminando EstadoUsuario con id: {}", id);
            if (!repository.existsById(id)) {
                logger.error("EstadoUsuario no encontrado para eliminar con id: {}", id);
                throw new RuntimeException("EstadoUsuario no encontrado para eliminar con id: " + id);
            }
            repository.deleteById(id);
        } catch (Exception e) {
            logger.error("Error eliminando EstadoUsuario con id: {}", id, e);
            throw new RuntimeException("Error eliminando EstadoUsuario con id: " + id, e);
        }
    }
}
