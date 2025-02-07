package co.edu.unicauca.sed.api.service.actividad;

import co.edu.unicauca.sed.api.dto.actividad.ActividadBaseDTO;
import co.edu.unicauca.sed.api.enums.TipoActividadEnum;
import co.edu.unicauca.sed.api.mapper.ActividadMapper;
import co.edu.unicauca.sed.api.model.*;
import co.edu.unicauca.sed.api.repository.*;
import co.edu.unicauca.sed.api.service.EstadoActividadService;
import co.edu.unicauca.sed.api.service.PeriodoAcademicoService;
import co.edu.unicauca.sed.api.service.fuente.FuenteService;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
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

    public Page<ActividadBaseDTO> findAll(Pageable pageable, Boolean ascendingOrder) {
        boolean order = (ascendingOrder != null) ? ascendingOrder : ActividadSortService.DEFAULT_ASCENDING_ORDER;

        // Obtener actividades paginadas desde el repositorio
        Page<Actividad> actividades = actividadRepository.findAll(pageable);

        // Convertir actividades a DTOs según el tipo de detalle
        List<ActividadBaseDTO> actividadDTOs = actividades.getContent().stream()
                .map(actividad -> actividadDTOService.convertActividadToDTO(actividad))
                .collect(Collectors.toList());

        // Ordenar las actividades si es necesario
        List<ActividadBaseDTO> sortedDTOs = actividadSortService.sortActivitiesByType(actividadDTOs, order);

        // Crear y retornar un nuevo objeto Page
        return new PageImpl<>(sortedDTOs, pageable, actividades.getTotalElements());
    }

    public Actividad findByOid(Integer oid) {
        return actividadRepository.findById(oid).orElse(null);
    }

    public ActividadBaseDTO findDTOByOid(Integer oid) {
        Actividad actividad = actividadRepository.findById(oid).orElseThrow(() -> new IllegalArgumentException("No se encontró una actividad con el ID: " + oid));
        return actividadDTOService.convertActividadToDTO(actividad);
    }

    @Transactional
    public Actividad save(ActividadBaseDTO actividadDTO) {
        try {
            Actividad actividad = actividadMapper.convertToEntity(actividadDTO);
            asignarPeriodoAcademicoActivo(actividad);
            if (actividad.getProceso().getNombreProceso() == null
                    || actividad.getProceso().getNombreProceso().isEmpty()) {
                actividad.getProceso().setNombreProceso("ACTIVIDAD");
            }
            actividad.getProceso().setEvaluador(new Usuario(actividadDTO.getOidEvaluador()));
            actividad.getProceso().setEvaluado(new Usuario(actividadDTO.getOidEvaluado()));
            guardarProceso(actividad);

            if (actividad.getNombreActividad() == null || actividad.getNombreActividad().isEmpty()) {
                String nombreActividad = actividadDetalleService.generarNombreActividad(actividadDTO);
                actividad.setNombreActividad(nombreActividad);
            }

            Actividad savedActividad = actividadRepository.save(actividad);

            if (actividadDTO.getDetalle() != null) {
                Object detalleConvertido = actividadDetalleService.convertirDetalleADTO(actividadDTO);
                actividadDTO.setDetalle(detalleConvertido);

                // Obtener el TipoActividadEnum desde el OID
                TipoActividadEnum tipoActividadEnum = TipoActividadEnum.fromOid(actividadDTO.getTipoActividad().getOidTipoActividad());

                if (tipoActividadEnum == null) {
                    throw new IllegalArgumentException("No se encontró un tipo de actividad válido para OID: "
                            + actividadDTO.getTipoActividad().getOidTipoActividad());
                }

                Class<?> entityClass = tipoActividadEnum.getEntityClass();

                if (entityClass == null) {
                    throw new IllegalArgumentException("No se encontró la entidad para el DTO: "
                            + detalleConvertido.getClass().getSimpleName()
                            + ". Asegúrate de que el DTO esté registrado en TipoActividadEnum.");
                }

                actividadDetalleService.saveDetalle(savedActividad, detalleConvertido, entityClass);
            }

            fuenteService.saveSource(savedActividad);
            return savedActividad;
        } catch (Exception e) {
            throw new RuntimeException("Error al guardar la actividad: " + e.getMessage(), e);
        }
    }

    @Transactional
    public Actividad update(Integer idActividad, ActividadBaseDTO actividadDTO) {
        Actividad actividadExistente = actividadRepository.findById(idActividad)
                .orElseThrow(() -> new IllegalArgumentException("Actividad con ID " + idActividad + " no encontrada."));

        // Verificar si el tipo de actividad ha cambiado
        boolean tipoActividadCambio = !actividadExistente.getTipoActividad().getOidTipoActividad()
                .equals(actividadDTO.getTipoActividad().getOidTipoActividad());

        if (tipoActividadCambio) {
            actividadDetalleService.cambiarTipoActividad(actividadExistente, actividadDTO.getTipoActividad().getOidTipoActividad());
        }

        if (actividadDTO.getNombreActividad() == null || actividadDTO.getNombreActividad().isEmpty()) {
            String nombreActividad = actividadDetalleService.generarNombreActividad(actividadDTO);
            actividadDTO.setNombreActividad(nombreActividad);
        }
        actividadMapper.actualizarCamposBasicos(actividadExistente, actividadDTO);
        estadoActividadService.asignarEstadoActividad(actividadExistente, actividadDTO.getOidEstadoActividad());

        // Manejo de detalle de la actividad
        if (actividadDTO.getDetalle() != null) {
            TipoActividadEnum tipoActividadEnum = TipoActividadEnum.fromOid(actividadDTO.getTipoActividad().getOidTipoActividad());

            if (tipoActividadEnum == null) {
                throw new IllegalArgumentException("No se encontró un tipo de actividad válido para OID: " + actividadDTO.getTipoActividad().getOidTipoActividad());
            }

            Class<?> entityClass = tipoActividadEnum.getEntityClass();

            if (entityClass == null) {
                throw new IllegalArgumentException("No se encontró la entidad para el tipo de actividad: " + tipoActividadEnum.name());
            }

            if (tipoActividadCambio) {
                actividadDetalleService.saveDetalle(actividadExistente, actividadDTO.getDetalle(), entityClass);
            } else {
                actividadDetalleService.updateDetalle(actividadExistente, actividadDTO.getDetalle(), entityClass);
            }
        }

        return actividadRepository.save(actividadExistente);
    }

    public void delete(Integer oid) {
        actividadRepository.deleteById(oid);
    }

    private void asignarPeriodoAcademicoActivo(Actividad actividad) {
        Integer idPeriodoAcademico = periodoAcademicoService.obtenerPeriodoAcademicoActivo();

        if (actividad.getProceso() == null) {
            actividad.setProceso(new Proceso());
        }

        PeriodoAcademico periodoAcademico = new PeriodoAcademico();
        periodoAcademico.setOidPeriodoAcademico(idPeriodoAcademico);
        actividad.getProceso().setOidPeriodoAcademico(periodoAcademico);
    }

    private void guardarProceso(Actividad actividad) {
        if (actividad.getProceso() != null) {
            Proceso savedProceso = procesoRepository.save(actividad.getProceso());
            actividad.setProceso(savedProceso);
        }
    }
}
