package co.edu.unicauca.sed.api.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import co.edu.unicauca.sed.api.dto.ApiResponse;
import co.edu.unicauca.sed.api.model.Actividad;
import co.edu.unicauca.sed.api.model.PeriodoAcademico;
import co.edu.unicauca.sed.api.model.Proceso;
import co.edu.unicauca.sed.api.model.Usuario;
import co.edu.unicauca.sed.api.repository.ProcesoRepository;
import co.edu.unicauca.sed.api.specification.ProcesoSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@Service
public class ProcesoService {

    private static final Logger logger = LoggerFactory.getLogger(ProcesoService.class);

    @Autowired
    private ProcesoRepository procesoRepository;

    @Autowired
    private PeriodoAcademicoService periodoAcademicoService;

    /**
     * Obtiene todos los procesos con filtros y paginación.
     */
    public ApiResponse<Page<Proceso>> findAll(Integer evaluadorId, Integer evaluadoId, Integer idPeriodo,
            String nombreProceso, LocalDateTime fechaCreacion, LocalDateTime fechaActualizacion, Pageable pageable) {
        try {
            if (idPeriodo == null) {
                idPeriodo = periodoAcademicoService.obtenerIdPeriodoAcademicoActivo();
            }

            Specification<Proceso> spec = ProcesoSpecification.byFilters(
                    evaluadorId, evaluadoId, idPeriodo, nombreProceso, fechaCreacion, fechaActualizacion);

            Page<Proceso> procesos = procesoRepository.findAll(spec, pageable);

            if (procesos.isEmpty()) {
                return new ApiResponse<>(204, "No se encontraron procesos con los filtros dados.", Page.empty());
            }

            return new ApiResponse<>(200, "Procesos obtenidos correctamente.", procesos);

        } catch (Exception e) {
            logger.error("❌ [ERROR] Error al obtener los procesos: {}", e.getMessage(), e);
            return new ApiResponse<>(500, "Error inesperado al obtener los procesos.", Page.empty());
        }
    }


    /**
     * Busca un proceso por su OID.
     */
    public ApiResponse<Proceso> findByOid(Integer oid) {
        try {
            Optional<Proceso> resultado = procesoRepository.findById(oid);

            if (resultado.isEmpty()) {
                return new ApiResponse<>(404, "Proceso con ID " + oid + " no encontrado.", null);
            }

            return new ApiResponse<>(200, "Proceso encontrado correctamente.", resultado.get());
        } catch (Exception e) {
            logger.error("❌ [ERROR] Error al buscar proceso por ID: {}", e.getMessage(), e);
            return new ApiResponse<>(500, "Error inesperado al buscar el proceso.", null);
        }
    }

    /**
     * Guarda un nuevo proceso.
     */
    public ApiResponse<Proceso> save(Proceso proceso) {
        try {
            if (proceso.getNombreProceso() != null) {
                proceso.setNombreProceso(proceso.getNombreProceso().toUpperCase());
            }

            Proceso savedProceso = procesoRepository.save(proceso);
            logger.info("✅ [SAVE] Proceso guardado con ID: {}", savedProceso.getOidProceso());

            return new ApiResponse<>(201, "Proceso guardado correctamente.", savedProceso);
        } catch (DataIntegrityViolationException e) {
            logger.error("❌ [ERROR] Restricción de clave única violada al guardar el proceso: {}", e.getMessage());
            return new ApiResponse<>(409, "Error: Ya existe un proceso con estos datos.", null);
        } catch (Exception e) {
            logger.error("❌ [ERROR] Error inesperado al guardar el proceso: {}", e.getMessage(), e);
            return new ApiResponse<>(500, "Error inesperado al guardar el proceso.", null);
        }
    }

    /**
     * Actualiza un proceso existente.
     */
    public ApiResponse<Proceso> update(Integer oid, Proceso proceso) {
        try {
            Proceso existingProceso = procesoRepository.findById(oid).orElse(null);

            if (existingProceso == null) {
                return new ApiResponse<>(404, "Proceso con ID " + oid + " no encontrado.", null);
            }

            existingProceso.setEvaluador(proceso.getEvaluador());
            existingProceso.setEvaluado(proceso.getEvaluado());
            existingProceso.setOidPeriodoAcademico(proceso.getOidPeriodoAcademico());
            existingProceso.setNombreProceso(proceso.getNombreProceso().toUpperCase());
            existingProceso.setResolucion(proceso.getResolucion());
            existingProceso.setOficio(proceso.getOficio());
            existingProceso.setConsolidado(proceso.getConsolidado());
            existingProceso.setActividades(proceso.getActividades());

            Proceso updatedProceso = procesoRepository.save(existingProceso);
            logger.info("✅ [UPDATE] Proceso actualizado con ID: {}", updatedProceso.getOidProceso());

            return new ApiResponse<>(200, "Proceso actualizado correctamente.", updatedProceso);
        } catch (Exception e) {
            logger.error("❌ [ERROR] Error inesperado al actualizar el proceso: {}", e.getMessage(), e);
            return new ApiResponse<>(500, "Error inesperado al actualizar el proceso.", null);
        }
    }

    /**
     * Elimina un proceso por su OID.
     */
    public ApiResponse<Void> delete(Integer oid) {
        try {
            if (!procesoRepository.existsById(oid)) {
                return new ApiResponse<>(404, "Proceso con ID " + oid + " no encontrado.", null);
            }

            procesoRepository.deleteById(oid);
            logger.info("✅ [DELETE] Proceso eliminado con ID: {}", oid);
            return new ApiResponse<>(200, "Proceso eliminado correctamente.", null);
        } catch (Exception e) {
            logger.error("❌ [ERROR] Error al eliminar proceso con ID {}: {}", oid, e.getMessage(), e);
            return new ApiResponse<>(500, "Error inesperado al eliminar el proceso.", null);
        }
    }

    /**
     * Guarda un proceso para una actividad.
     */
    public ApiResponse<Void> guardarProceso(Actividad actividad) {
        if (actividad.getProceso() != null) {
            try {
                Proceso savedProceso = procesoRepository.save(actividad.getProceso());
                actividad.setProceso(savedProceso);
                logger.info("✅ [SAVE] Proceso guardado con ID: {}", savedProceso.getOidProceso());
                return new ApiResponse<>(200, "Proceso guardado correctamente.", null);
            } catch (DataIntegrityViolationException e) {
                logger.error("❌ [ERROR] Restricción de clave única violada al guardar el proceso: {}", e.getMessage());
                return new ApiResponse<>(409, "Error: Ya existe un proceso con estos datos.", null);
            } catch (Exception e) {
                logger.error("❌ [ERROR] Error inesperado al guardar el proceso: {}", e.getMessage(), e);
                return new ApiResponse<>(500, "Error inesperado al guardar el proceso.", null);
            }
        }
        return new ApiResponse<>(400, "Error: La actividad no contiene un proceso válido.", null);
    }

    public Proceso buscarProcesoExistente(Integer idEvaluador, Integer idEvaluado, Integer idPeriodoAcademico, String nombreProceso) {
        try {
            return procesoRepository.findByEvaluadorAndEvaluadoAndOidPeriodoAcademicoAndNombreProceso(
                    new Usuario(idEvaluador),
                    new Usuario(idEvaluado),
                    new PeriodoAcademico(idPeriodoAcademico),
                    nombreProceso).orElse(null);
        } catch (Exception e) {
            logger.error("❌ [ERROR] Error al buscar proceso existente: {}", e.getMessage(), e);
            return null; // Devolvemos `null` en caso de error para evitar interrupciones
        }
    }
    
    public Proceso crearNuevoProceso(Integer idEvaluado, Integer idEvaluador, Integer idPeriodoAcademico) {
        try {
            Proceso proceso = new Proceso();
            proceso.setEvaluador(new Usuario(idEvaluador));
            proceso.setEvaluado(new Usuario(idEvaluado));
            proceso.setOidPeriodoAcademico(new PeriodoAcademico(idPeriodoAcademico));
            proceso.setNombreProceso("Consolidado Generado");
    
            Proceso savedProceso = this.procesoRepository.save(proceso);
            logger.info("✅ [SAVE] Nuevo proceso creado con ID: {}", savedProceso.getOidProceso());
    
            return savedProceso;
        } catch (DataIntegrityViolationException e) {
            logger.error("❌ [ERROR] Restricción de clave única violada al crear el proceso: {}", e.getMessage());
            return null; // Retornamos `null` para indicar que no se pudo crear
        } catch (Exception e) {
            logger.error("❌ [ERROR] Error inesperado al crear el proceso: {}", e.getMessage(), e);
            return null;
        }
    }
}
