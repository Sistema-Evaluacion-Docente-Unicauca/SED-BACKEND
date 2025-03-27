package co.edu.unicauca.sed.api.service.actividad;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
import co.edu.unicauca.sed.api.repository.UsuarioRepository;
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

    @Autowired
    private ActividadDetalleService actividadDetalleService;

    @Autowired
    private UsuarioRepository usuarioRepository;

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
            Actividad actividad = actividadRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("No se encontró una actividad con el ID: " + id));
            return new ApiResponse<>(200, "Actividad encontrada.",actividadDTOService.buildActividadBaseDTO(actividad));
        } catch (IllegalArgumentException e) {
            return new ApiResponse<>(404, e.getMessage(), null);
        }
    }

    @Transactional
    @Override
    public ApiResponse<List<Actividad>> guardar(List<ActividadBaseDTO> actividadesDTO) {
        List<Actividad> actividadesGuardadas = new ArrayList<>();
        List<String> errores = new ArrayList<>();

        for (ActividadBaseDTO dto : actividadesDTO) {
            try {
                Actividad guardada = guardarActividad(dto);
                actividadesGuardadas.add(guardada);
            } catch (DataIntegrityViolationException e) {
                errores.add("Actividad con ID " + dto.getOidActividad() + ": ya existe.");
            } catch (Exception e) {
                logger.info("Error guardando actividad: " + e.getMessage());
                errores.add("Actividad con ID " + dto.getOidActividad() + ": " + e.getMessage());
            }
        }

        if (!actividadesGuardadas.isEmpty()) {
            String mensaje = construirMensajeFinal(actividadesGuardadas.size(), errores.size());
            return new ApiResponse<>(201, mensaje, actividadesGuardadas);
        } else {
            return new ApiResponse<>(400, "No se pudo guardar ninguna actividad. Errores: " + String.join(" | ", errores), null);
        }
    }

    private String construirMensajeFinal(int exitosas, int fallidas) {
        String mensaje = "Actividades guardadas: " + exitosas;
        if (fallidas > 0) {
            mensaje += ". Con errores en " + fallidas + " actividad(es).";
        }
        return mensaje;
    }    

    private Actividad guardarActividad(ActividadBaseDTO dto) {
        validarDuplicado(dto);

        Actividad actividad = actividadMapper.convertToEntity(dto);
        asignarPeriodoAcademicoActivo(actividad);

        if (actividad.getProceso().getNombreProceso() == null || actividad.getProceso().getNombreProceso().isEmpty()) {
            actividad.getProceso().setNombreProceso("ACTIVIDAD");
        }

        asignarEvaluadorYEvaluado(actividad, dto);
        procesoService.guardarProceso(actividad);
        asignarNombreActividadSiNecesario(actividad, dto);

        Actividad actividadGuardada = actividadRepository.save(actividad);
        guardarComponentesRelacionados(dto, actividadGuardada);

        return actividadGuardada;
    }

    private void validarDuplicado(ActividadBaseDTO dto) {
        if (dto.getOidActividad() != null && actividadRepository.existsById(dto.getOidActividad())) {
            throw new DataIntegrityViolationException("La actividad con ID " + dto.getOidActividad() + " ya existe.");
        }
    }

    private void asignarEvaluadorYEvaluado(Actividad actividad, ActividadBaseDTO dto) {
        Usuario evaluado;

        Optional<Usuario> posibleEvaluado = usuarioRepository.findById(dto.getOidEvaluado());
        if (posibleEvaluado.isPresent()) {
            evaluado = new Usuario(dto.getOidEvaluado());
        } else {
            evaluado = usuarioRepository.findByIdentificacion(String.valueOf(dto.getOidEvaluado()));
            if (evaluado == null) {
                throw new RuntimeException("No se encontró evaluado con identificación " + dto.getOidEvaluado());
            }
        }

        actividad.getProceso().setEvaluado(evaluado);

        Usuario evaluador;
        if (dto.getOidEvaluador() != 0) {
            evaluador = new Usuario(dto.getOidEvaluador());
        } else {
            Integer idTipoActividad = dto.getTipoActividad().getOidTipoActividad();
            evaluador = obtenerEvaluadorAutomatico(idTipoActividad, evaluado);
        }

        actividad.getProceso().setEvaluador(evaluador);
    }

    private void asignarNombreActividadSiNecesario(Actividad actividad, ActividadBaseDTO dto) {
        if (actividad.getNombreActividad() == null || actividad.getNombreActividad().isEmpty()) {
            actividad.setNombreActividad(actividadDetalleService.generarNombreActividad(dto));
        }
    }

    private void guardarComponentesRelacionados(ActividadBaseDTO dto, Actividad actividadGuardada) {
        fuenteService.guardarFuente(actividadGuardada);
        eavAtributoService.guardarAtributosDinamicos(dto, actividadGuardada);
    }

    @Transactional
    @Override
    public ApiResponse<Actividad> actualizar(Integer idActividad, ActividadBaseDTO actividadDTO) {
        try {
            Actividad actividadExistente = actividadRepository.findById(idActividad)
                .orElseThrow(() -> new ValidationException(404, "Actividad con ID " + idActividad + " no encontrada."));

            if (actividadDTO.getOidActividad() != null && !actividadDTO.getOidActividad().equals(idActividad)
                    && actividadRepository.existsById(actividadDTO.getOidActividad())) {
                return new ApiResponse<>(409, "Error: Ya existe una actividad con ID " + actividadDTO.getOidActividad(),null);
            }

            if (actividadDTO.getNombreActividad() == null || actividadDTO.getNombreActividad().isEmpty()) {
                actividadDTO.setNombreActividad(actividadDetalleService.generarNombreActividad(actividadDTO));
            }

            Proceso proceso = actividadExistente.getProceso();
            if (proceso == null) {
                throw new ValidationException(400, "Error: La actividad no tiene un proceso asociado.");
            }

            proceso.setEvaluador(new Usuario(actividadDTO.getOidEvaluador()));
            proceso.setEvaluado(new Usuario(actividadDTO.getOidEvaluado()));

            // Si el nombre del proceso está vacío, asignar un valor por defecto
            if (proceso.getNombreProceso() == null || proceso.getNombreProceso().isEmpty()) {
                proceso.setNombreProceso("ACTUALIZADO - ACTIVIDAD");
            }

            procesoService.actualizar(actividadExistente.getProceso().getOidProceso(), proceso);

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

            if (actividad.getProceso() == null) {
                actividad.setProceso(new Proceso());
            }

            PeriodoAcademico periodoAcademico = new PeriodoAcademico();
            periodoAcademico.setOidPeriodoAcademico(idPeriodoAcademico);
            actividad.getProceso().setOidPeriodoAcademico(periodoAcademico);
        } catch (Exception e) {
            logger.error("❌ [ERROR] Error al asignar periodo académico activo: {}", e.getMessage(), e);
            throw new RuntimeException("Error al asignar periodo académico: " + e.getMessage(), e);
        }
    }

    private Usuario obtenerEvaluadorAutomatico(Integer oidTipoActividad, Usuario evaluado) {
        final int ROL_DECANO = 3;
        final int ROL_JEFE_DEPTO = 4;
        final int ROL_SECRETARIA = 5;
    
        try {
            switch (oidTipoActividad) {
                case 1: // ASESORÍA
                case 4: // CAPACITACIÓN
                case 6: // OTROS SERVICIOS
                case 7: // EXTENSIÓN
                case 9: // DOCENCIA
                case 2: // TRABAJO DE DOCENCIA
                    return usuarioRepository.findFirstActiveByDepartamentoAndRolId(evaluado.getUsuarioDetalle().getDepartamento(), ROL_JEFE_DEPTO)
                        .orElseThrow(() -> new RuntimeException("No se encontró Jefe de Departamento activo para el departamento " + evaluado.getUsuarioDetalle().getDepartamento()));
        
                case 5: // ADMINISTRACIÓN
                    return usuarioRepository.findFirstActiveByFacultadAndRolId(evaluado.getUsuarioDetalle().getFacultad(), ROL_DECANO)
                        .orElseThrow(() -> new RuntimeException("No se encontró Decano activo para la facultad " + evaluado.getUsuarioDetalle().getFacultad()));
        
                case 3: // PROYECTOS INVESTIGACIÓN
                case 8: // TRABAJOS DE INVESTIGACION
                    return usuarioRepository.findById(ROL_SECRETARIA).orElseThrow(() -> new RuntimeException(
                        "No se encontró el usuario evaluador con ID " + ROL_SECRETARIA));
        
                default:
                    return usuarioRepository
                        .findFirstActiveByFacultadAndRolId(evaluado.getUsuarioDetalle().getFacultad(), ROL_SECRETARIA)
                        .orElseThrow(() -> new RuntimeException("No se encontró una secretaria activa para la facultad " + evaluado.getUsuarioDetalle().getFacultad()));
            }
        } catch (RuntimeException  e) {
            return usuarioRepository
                .findFirstActiveByFacultadAndRolId(evaluado.getUsuarioDetalle().getFacultad(), ROL_SECRETARIA)
                .orElseThrow(() -> new RuntimeException("❌ No se encontró una secretaria/o activa para la facultad " + evaluado.getUsuarioDetalle().getFacultad()));
        }
    }
}
