package co.edu.unicauca.sed.api.service.actividad;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import co.edu.unicauca.sed.api.service.EavAtributoService;
import co.edu.unicauca.sed.api.domain.Actividad;
import co.edu.unicauca.sed.api.domain.PeriodoAcademico;
import co.edu.unicauca.sed.api.domain.Proceso;
import co.edu.unicauca.sed.api.domain.Usuario;
import co.edu.unicauca.sed.api.dto.ApiResponse;
import co.edu.unicauca.sed.api.dto.actividad.ActividadBaseDTO;
import co.edu.unicauca.sed.api.exception.ValidationException;
import co.edu.unicauca.sed.api.mapper.ActividadMapper;
import co.edu.unicauca.sed.api.repository.ActividadRepository;
import co.edu.unicauca.sed.api.service.fuente.FuenteService;
import co.edu.unicauca.sed.api.service.periodo_academico.PeriodoAcademicoService;
import co.edu.unicauca.sed.api.service.proceso.ProcesoService;

@Service
public class ActividadServiceImpl implements ActividadService {

    private static final Logger logger = LoggerFactory.getLogger(ActividadServiceImpl.class);

    @Autowired
    private ActividadRepository actividadRepository;

    @Autowired
    private ActividadDTOService actividadDTOService;

    @Autowired
    private ActividadQueryService actividadQueryService;

    @Autowired
    private ActividadMapper actividadMapper;

    @Autowired
    private PeriodoAcademicoService periodoAcademicoService;

    @Autowired
    private EstadoActividadService estadoActividadService;

    @Autowired
    private FuenteService fuenteService;

    @Autowired
    private ProcesoService procesoService;

    @Autowired
    private EavAtributoService eavAtributoService;

    @Override
    public ApiResponse<Page<ActividadBaseDTO>> obtenerTodos(Pageable paginacion, Boolean ordenAscendente) {
        boolean orden = (ordenAscendente != null) ? ordenAscendente : true;

        Page<Actividad> actividades = actividadRepository.findAll(paginacion);
        if (actividades.isEmpty()) {
            return new ApiResponse<>(404, "No se encontraron actividades.", Page.empty());
        }

        List<ActividadBaseDTO> actividadDTOs = actividades.getContent().stream()
                .map(actividadDTOService::buildActividadBaseDTO)
                .collect(Collectors.toList());

        List<ActividadBaseDTO> sortedDTOs = actividadQueryService.ordenarActividadesPorTipo(actividadDTOs, orden);
        return new ApiResponse<>(200, "Actividades obtenidas correctamente.",
                new PageImpl<>(sortedDTOs, paginacion, actividades.getTotalElements()));
    }

    @Override
    public Actividad buscarPorId(Integer id) {
        return actividadRepository.findById(id).orElse(null);
    }

    @Override
    public ApiResponse<ActividadBaseDTO> buscarDTOPorId(Integer id) {
        try {
            Actividad actividad = actividadRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("No se encontró una actividad con el ID: " + id));

            return new ApiResponse<>(200, "Actividad encontrada.",
                    actividadDTOService.buildActividadBaseDTO(actividad));
        } catch (IllegalArgumentException e) {
            return new ApiResponse<>(404, e.getMessage(), null);
        }
    }

    @Transactional
    @Override
    public ApiResponse<Actividad> guardar(ActividadBaseDTO actividadDTO) {
        try {
            if (actividadDTO.getOidActividad() != null
                    && actividadRepository.existsById(actividadDTO.getOidActividad())) {
                return new ApiResponse<>(409,
                        "Error: La actividad con ID " + actividadDTO.getOidActividad() + " ya existe.", null);
            }

            Actividad actividad = actividadMapper.convertToEntity(actividadDTO);
            asignarPeriodoAcademicoActivo(actividad);

            actividad.getProceso().setEvaluador(new Usuario(actividadDTO.getOidEvaluador()));
            actividad.getProceso().setEvaluado(new Usuario(actividadDTO.getOidEvaluado()));

            procesoService.guardarProceso(actividad);

            Actividad actividadGuardada = actividadRepository.save(actividad);
            fuenteService.guardarFuente(actividadGuardada);
            eavAtributoService.guardarAtributosDinamicos(actividadDTO, actividadGuardada);

            return new ApiResponse<>(201, "Actividad guardada correctamente.", actividadGuardada);
        } catch (DataIntegrityViolationException e) {
            return new ApiResponse<>(409, "Error: Ya existe un registro con los mismos datos.", null);
        }
    }

    @Transactional
    @Override
    public ApiResponse<Actividad> actualizar(Integer idActividad, ActividadBaseDTO actividadDTO) {
        try {
            Actividad actividadExistente = actividadRepository.findById(idActividad)
                    .orElseThrow(
                            () -> new ValidationException(404, "Actividad con ID " + idActividad + " no encontrada."));

            actividadMapper.actualizarCamposBasicos(actividadExistente, actividadDTO);
            estadoActividadService.asignarEstadoActividad(actividadExistente, actividadDTO.getOidEstadoActividad());
            eavAtributoService.actualizarAtributosDinamicos(actividadDTO, actividadExistente);

            Actividad actividadActualizada = actividadRepository.save(actividadExistente);
            return new ApiResponse<>(200, "Actividad actualizada correctamente.", actividadActualizada);
        } catch (ValidationException e) {
            return new ApiResponse<>(404, e.getMessage(), null);
        }
    }

    @Transactional
    @Override
    public ApiResponse<Void> eliminar(Integer id) {
        try {
            if (!actividadRepository.existsById(id)) {
                return new ApiResponse<>(404, "Actividad con ID " + id + " no encontrada.", null);
            }
            actividadRepository.deleteById(id);
            return new ApiResponse<>(200, "Actividad eliminada correctamente.", null);
        } catch (Exception e) {
            return new ApiResponse<>(500, "Error al eliminar la actividad: " + e.getMessage(), null);
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
