package co.edu.unicauca.sed.api.service.actividad;

import co.edu.unicauca.sed.api.dto.actividad.ActividadBaseDTO;
import co.edu.unicauca.sed.api.exception.ValidationException;
import co.edu.unicauca.sed.api.mapper.ActividadMapper;
import co.edu.unicauca.sed.api.model.*;
import co.edu.unicauca.sed.api.repository.*;
import co.edu.unicauca.sed.api.service.EavAtributoService;
import co.edu.unicauca.sed.api.service.EstadoActividadService;
import co.edu.unicauca.sed.api.service.PeriodoAcademicoService;
import co.edu.unicauca.sed.api.service.ProcesoService;
import co.edu.unicauca.sed.api.service.fuente.FuenteService;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

/**
 * Servicio para gestionar las actividades, incluyendo operaciones de consulta,
 * creación, actualización, y eliminación.
 */
@Service
public class ActividadService {

    private static final Logger logger = LoggerFactory.getLogger(ActividadService.class);

    @Autowired
    private ActividadRepository actividadRepository;

    @Autowired
    private ProcesoRepository procesoRepository;

    @Autowired
    private ActividadDTOService actividadDTOService;

    @Autowired
    private ActividadSortService actividadSortService;

    @Autowired
    private ActividadMapper actividadMapper;

    @Autowired
    private PeriodoAcademicoService periodoAcademicoService;

    @Autowired
    private ActividadDetalleService actividadDetalleService;

    @Autowired
    private EstadoActividadService estadoActividadService;

    @Autowired
    private FuenteService fuenteService;

    @Autowired
    private ProcesoService procesoService;

    @Autowired
    private EavAtributoService eavAtributoService;

    @Autowired
    private ActividadDTOService actividadDtoService;

    public Page<ActividadBaseDTO> findAll(Pageable pageable, Boolean ascendingOrder) {
        boolean order = (ascendingOrder != null) ? ascendingOrder : ActividadSortService.DEFAULT_ASCENDING_ORDER;

        logger.info("🔵 [FIND_ALL] Buscando actividades con paginación: page={}, size={}", pageable.getPageNumber(),
                pageable.getPageSize());

        Page<Actividad> actividades = actividadRepository.findAll(pageable);
        logger.info("✅ [FIND_ALL] Se encontraron {} actividades en la base de datos.", actividades.getTotalElements());

        List<ActividadBaseDTO> actividadDTOs = actividades.getContent().stream()
            .map(actividad -> actividadDtoService.buildActividadBaseDTO(actividad))
            .collect(Collectors.toList());

        List<ActividadBaseDTO> sortedDTOs = actividadSortService.sortActivitiesByType(actividadDTOs, order);
        logger.info("✅ [FIND_ALL] Se ordenaron {} actividades según el criterio especificado.", sortedDTOs.size());

        return new PageImpl<>(sortedDTOs, pageable, actividades.getTotalElements());
    }

    public Actividad findByOid(Integer oid) {
        return actividadRepository.findById(oid).orElse(null);
    }

    public ActividadBaseDTO findDTOByOid(Integer oid) {
        Actividad actividad = actividadRepository.findById(oid)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró una actividad con el ID: " + oid));
        return actividadDTOService.buildActividadBaseDTO(actividad);
    }

    @Transactional
    public Actividad save(ActividadBaseDTO actividadDTO) {
        try {
            Actividad actividad = actividadMapper.convertToEntity(actividadDTO);
            asignarPeriodoAcademicoActivo(actividad);

            if (actividad.getProceso().getNombreProceso() == null || actividad.getProceso().getNombreProceso().isEmpty()) {
                actividad.getProceso().setNombreProceso("ACTIVIDAD");
            }

            actividad.getProceso().setEvaluador(new Usuario(actividadDTO.getOidEvaluador()));
            actividad.getProceso().setEvaluado(new Usuario(actividadDTO.getOidEvaluado()));
            procesoService.guardarProceso(actividad);

            if (actividad.getNombreActividad() == null || actividad.getNombreActividad().isEmpty()) {
                actividad.setNombreActividad(actividadDetalleService.generarNombreActividad(actividadDTO));
            }

            // 3️⃣ Guardar actividad principal
        Actividad savedActividad = actividadRepository.save(actividad);
        logger.info("✅ [SAVE] Actividad guardada con ID: {}", savedActividad.getOidActividad());

        // 4️⃣ Guardar fuentes
        fuenteService.saveSource(savedActividad);

        // 5️⃣ Guardar atributos dinámicos en EAV
        eavAtributoService.guardarAtributosDinamicos(actividadDTO, actividad);

        return savedActividad;

        } catch (Exception e) {
            logger.error("❌ [ERROR] Error al guardar actividad: {}", e.getMessage(), e);
            throw new RuntimeException("Error al guardar la actividad: " + e.getMessage(), e);
        }
    }

    @Transactional
    public Actividad update(Integer idActividad, ActividadBaseDTO actividadDTO) {
        Actividad actividadExistente = actividadRepository.findById(idActividad)
                .orElseThrow(() -> {
                    logger.warn("⚠️ [UPDATE] Actividad con ID {} no encontrada.", idActividad);
                    return new ValidationException(404, "Actividad con ID " + idActividad + " no encontrada.");
                });

        if (actividadDTO.getNombreActividad() == null || actividadDTO.getNombreActividad().isEmpty()) {
            actividadDTO.setNombreActividad(actividadDetalleService.generarNombreActividad(actividadDTO));
        }

        actividadMapper.actualizarCamposBasicos(actividadExistente, actividadDTO);
        estadoActividadService.asignarEstadoActividad(actividadExistente, actividadDTO.getOidEstadoActividad());

        // 🔹 Actualizar atributos dinámicos
        eavAtributoService.actualizarAtributosDinamicos(actividadDTO, actividadExistente);

        // Guardar cambios en la actividad
        Actividad actividadActualizada = actividadRepository.save(actividadExistente);
        logger.info("✅ [UPDATE] Actividad actualizada con ID: {}", actividadActualizada.getOidActividad());

        return actividadActualizada;
    }

    public void delete(Integer oid) {
        try {
            logger.info("🔵 [DELETE] Intentando eliminar actividad con ID: {}", oid);
            actividadRepository.deleteById(oid);
            logger.info("✅ [DELETE] Actividad eliminada con ID: {}", oid);
        } catch (EmptyResultDataAccessException e) {
            logger.warn("⚠️ [DELETE] No se encontró actividad con ID: {}", oid);
            throw new ValidationException(404, "Actividad con ID " + oid + " no encontrada.");
        } catch (Exception e) {
            logger.error("❌ [ERROR] Error al eliminar actividad con ID {}: {}", oid, e.getMessage(), e);
            throw new RuntimeException("Error al eliminar la actividad: " + e.getMessage(), e);
        }
    }

    private void asignarPeriodoAcademicoActivo(Actividad actividad) {
        try {
            Integer idPeriodoAcademico = periodoAcademicoService.obtenerIdPeriodoAcademicoActivo();
            logger.info("🔵 [PERIODO] Asignando periodo académico activo con ID: {}", idPeriodoAcademico);

            if (actividad.getProceso() == null) {
                logger.warn("⚠️ [PERIODO] La actividad no tiene un proceso asociado. Se creará uno nuevo.");
                actividad.setProceso(new Proceso());
            }

            PeriodoAcademico periodoAcademico = new PeriodoAcademico();
            periodoAcademico.setOidPeriodoAcademico(idPeriodoAcademico);
            actividad.getProceso().setOidPeriodoAcademico(periodoAcademico);

            logger.info("✅ [PERIODO] Periodo académico asignado con ID: {}", idPeriodoAcademico);

        } catch (Exception e) {
            logger.error("❌ [ERROR] Error al asignar periodo académico activo: {}", e.getMessage(), e);
            throw new RuntimeException("Error al asignar periodo académico: " + e.getMessage(), e);
        }
    }
}
