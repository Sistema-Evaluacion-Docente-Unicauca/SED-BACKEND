package co.edu.unicauca.sed.api.service;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import co.edu.unicauca.sed.api.dto.ApiResponse;
import co.edu.unicauca.sed.api.model.EstadoPeriodoAcademico;
import co.edu.unicauca.sed.api.model.PeriodoAcademico;
import co.edu.unicauca.sed.api.repository.EstadoPeriodoAcademicoRepository;
import co.edu.unicauca.sed.api.repository.PeriodoAcademicoRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class PeriodoAcademicoService {

    @Autowired
    private PeriodoAcademicoRepository periodoAcademicoRepository;

    @Autowired
    private EstadoPeriodoAcademicoRepository estadoPeriodoAcademicoRepository;

    private static final Logger logger = LoggerFactory.getLogger(PeriodoAcademicoService.class);

    public ApiResponse<Page<PeriodoAcademico>> findAll(Pageable pageable) {
        try {
            Page<PeriodoAcademico> periodos = periodoAcademicoRepository.findAll(pageable);
            if (periodos.isEmpty()) {
                return new ApiResponse<>(204, "No se encontraron períodos académicos.", Page.empty());
            }
            return new ApiResponse<>(200, "Períodos académicos obtenidos correctamente.", periodos);
        } catch (Exception e) {
            logger.error("❌ [ERROR] Error al obtener los períodos académicos: {}", e.getMessage(), e);
            return new ApiResponse<>(500, "Error inesperado al obtener los períodos académicos.", Page.empty());
        }
    }

    public ApiResponse<PeriodoAcademico> findByOid(Integer oid) {
        try {
            PeriodoAcademico periodo = periodoAcademicoRepository.findById(oid)
                    .orElseThrow(
                            () -> new EntityNotFoundException("Período académico con ID " + oid + " no encontrado."));
            return new ApiResponse<>(200, "Período académico encontrado correctamente.", periodo);
        } catch (EntityNotFoundException e) {
            return new ApiResponse<>(404, e.getMessage(), null);
        } catch (Exception e) {
            logger.error("❌ [ERROR] Error al buscar el período académico: {}", e.getMessage(), e);
            return new ApiResponse<>(500, "Error inesperado al buscar el período académico.", null);
        }
    }

    public ApiResponse<PeriodoAcademico> save(PeriodoAcademico periodoAcademico) {
        try {
            validatePeriodoAcademico(null, periodoAcademico);
            PeriodoAcademico saved = periodoAcademicoRepository.save(periodoAcademico);
            return new ApiResponse<>(201, "Período académico guardado correctamente.", saved);
        } catch (IllegalArgumentException e) {
            return new ApiResponse<>(400, e.getMessage(), null);
        } catch (Exception e) {
            logger.error("❌ [ERROR] Error al guardar el período académico: {}", e.getMessage(), e);
            return new ApiResponse<>(500, "Error inesperado al guardar el período académico.", null);
        }
    }

    public ApiResponse<Void> update(Integer oid, PeriodoAcademico periodoAcademico) {
        try {
            Integer estadoId = periodoAcademico.getEstadoPeriodoAcademico().getOidEstadoPeriodoAcademico();
            EstadoPeriodoAcademico estado = estadoPeriodoAcademicoRepository.findById(estadoId)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "El EstadoPeriodoAcademico con OID " + estadoId + " no existe."));

            periodoAcademico.setEstadoPeriodoAcademico(estado);
            validatePeriodoAcademico(oid, periodoAcademico);

            periodoAcademicoRepository.findById(oid)
                .orElseThrow(
                    () -> new EntityNotFoundException("Período académico con ID " + oid + " no encontrado."));

            periodoAcademico.setOidPeriodoAcademico(oid);
            periodoAcademicoRepository.save(periodoAcademico);
            return new ApiResponse<>(200, "Período académico actualizado correctamente.", null);
        } catch (EntityNotFoundException e) {
            return new ApiResponse<>(404, e.getMessage(), null);
        } catch (IllegalArgumentException e) {
            return new ApiResponse<>(400, e.getMessage(), null);
        } catch (Exception e) {
            logger.error("❌ [ERROR] Error al actualizar el período académico: {}", e.getMessage(), e);
            return new ApiResponse<>(500, "Error inesperado al actualizar el período académico.", null);
        }
    }

    public ApiResponse<Void> delete(Integer oid) {
        try {
            periodoAcademicoRepository.deleteById(oid);
            return new ApiResponse<>(200, "Período académico eliminado correctamente.", null);
        } catch (Exception e) {
            logger.error("❌ [ERROR] Error al eliminar el período académico: {}", e.getMessage(), e);
            return new ApiResponse<>(500, "Error inesperado al eliminar el período académico.", null);
        }
    }

    /**
     * Valida las condiciones necesarias para guardar o actualizar un período académico.
     *
     * @param oid              El identificador del período académico (puede ser null para un nuevo registro).
     * @param periodoAcademico El objeto PeriodoAcademico que se desea guardar o actualizar.
     * @throws IllegalArgumentException Si se violan las reglas de validación.
     */
    private void validatePeriodoAcademico(Integer oid, PeriodoAcademico periodoAcademico) {
        if (periodoAcademicoRepository.existsByIdPeriodo(periodoAcademico.getIdPeriodo())) {
            if (oid == null) {
                throw new IllegalArgumentException(
                    "El ID del período académico '" + periodoAcademico.getIdPeriodo() + "' ya existe.");
            }
    
            ApiResponse<PeriodoAcademico> response = findByOid(oid);
            PeriodoAcademico periodoExistente = response.getData();
    
            if (periodoExistente != null && !periodoExistente.getIdPeriodo().equals(periodoAcademico.getIdPeriodo())) {
                throw new IllegalArgumentException(
                    "El ID del período académico '" + periodoAcademico.getIdPeriodo() + "' ya existe en otro registro.");
            }
        }
    
        EstadoPeriodoAcademico estadoPeriodoAcademico = estadoPeriodoAcademicoRepository
            .findById(periodoAcademico.getEstadoPeriodoAcademico().getOidEstadoPeriodoAcademico())
            .orElseThrow(() -> new IllegalArgumentException("El EstadoPeriodoAcademico con OID "
                + periodoAcademico.getEstadoPeriodoAcademico().getOidEstadoPeriodoAcademico() + " no existe."));
    
        if ("ACTIVO".equals(estadoPeriodoAcademico.getNombre())) {
            Optional<PeriodoAcademico> periodoActivo = getPeriodoAcademicoActivo();
    
            if (periodoActivo.isPresent() && (oid == null || !periodoActivo.get().getOidPeriodoAcademico().equals(oid))) {
                throw new IllegalArgumentException(
                    "Ya existe un período académico activo con ID: " + periodoActivo.get().getIdPeriodo());
            }
        }
    }

    /**
     * Obtiene el período académico que está marcado como activo en la base de
     * datos.
     *
     * @return Un Optional que contiene el período académico activo si existe.
     */
    public Optional<PeriodoAcademico> getPeriodoAcademicoActivo() {
        String nombreEstadoActivo = "ACTIVO";
        return periodoAcademicoRepository.findByEstadoPeriodoAcademicoNombre(nombreEstadoActivo);
    }

    /**
     * Obtiene el identificador del período académico activo.
     *
     * @return El identificador del período académico activo.
     * @throws IllegalStateException Si no se encuentra un período académico activo.
     */
    public Integer obtenerIdPeriodoAcademicoActivo() {
        return getPeriodoAcademicoActivo()
                .map(PeriodoAcademico::getOidPeriodoAcademico)
                .orElseThrow(() -> {
                    return new EntityNotFoundException("No se encontró un período académico activo.");
                });
    }
}
