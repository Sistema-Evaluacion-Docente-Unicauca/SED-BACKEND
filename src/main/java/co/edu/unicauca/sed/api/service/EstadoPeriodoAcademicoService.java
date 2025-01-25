package co.edu.unicauca.sed.api.service;

import co.edu.unicauca.sed.api.model.EstadoPeriodoAcademico;
import co.edu.unicauca.sed.api.repository.EstadoPeriodoAcademicoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class EstadoPeriodoAcademicoService {

    private static final Logger logger = LoggerFactory.getLogger(EstadoPeriodoAcademicoService.class);

    @Autowired
    private EstadoPeriodoAcademicoRepository repository;

    public EstadoPeriodoAcademico create(EstadoPeriodoAcademico estadoPeriodoAcademico) {
        logger.info("Creando EstadoPeriodoAcademico: {}", estadoPeriodoAcademico);
        return repository.save(estadoPeriodoAcademico);
    }

    public Optional<EstadoPeriodoAcademico> findById(Integer id) {
        logger.info("Buscando EstadoPeriodoAcademico con id: {}", id);
        return repository.findById(id);
    }

    public Page<EstadoPeriodoAcademico> findAll(Pageable pageable) {
        logger.info("Listando EstadoPeriodoAcademico con paginación");
        return repository.findAll(pageable);
    }

    public EstadoPeriodoAcademico update(Integer id, EstadoPeriodoAcademico estadoPeriodoAcademico) {
        try {
            logger.info("Actualizando EstadoPeriodoAcademico con id: {}", id);
            return repository.findById(id).map(existing -> {
                existing.setNombre(estadoPeriodoAcademico.getNombre());
                return repository.save(existing);
            }).orElseThrow(() -> {
                logger.error("EstadoPeriodoAcademico no encontrado con id: {}", id);
                return new RuntimeException("EstadoPeriodoAcademico no encontrado con id: " + id);
            });
        } catch (Exception e) {
            logger.error("Error actualizando EstadoPeriodoAcademico con id: {}", id, e);
            throw new RuntimeException("Error actualizando EstadoPeriodoAcademico con id: " + id, e);
        }
    }

    public void delete(Integer id) {
        try {
            logger.info("Eliminando EstadoPeriodoAcademico con id: {}", id);
            if (!repository.existsById(id)) {
                logger.error("EstadoPeriodoAcademico no encontrado para eliminar con id: {}", id);
                throw new RuntimeException("EstadoPeriodoAcademico no encontrado para eliminar con id: " + id);
            }
            repository.deleteById(id);
        } catch (Exception e) {
            logger.error("Error eliminando EstadoPeriodoAcademico con id: {}", id, e);
            throw new RuntimeException("Error eliminando EstadoPeriodoAcademico con id: " + id, e);
        }
    }
}
