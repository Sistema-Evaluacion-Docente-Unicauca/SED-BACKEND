package co.edu.unicauca.sed.api.service.actividad;

import co.edu.unicauca.sed.api.dto.actividad.*;
import co.edu.unicauca.sed.api.model.*;
import co.edu.unicauca.sed.api.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;

@Service
public class ActividadDetalleService {

    @Autowired
    private DocenciaDetalleRepository docenciaDetalleRepository;

    @Autowired
    private TrabajoDocenciaDetalleRepository trabajoDocenciaDetalleRepository;

    @Autowired
    private ProyectoInvestigacionDetalleRepository proyectoInvestigacionDetalleRepository;

    @Autowired
    private CapacitacionDetalleRepository capacitacionDetalleRepository;

    @Autowired
    private OtroServicioDetalleRepository otroServicioDetalleRepository;

    @Autowired
    private ExtensionDetalleRepository extensionDetalleRepository;

    @Autowired
    private TrabajoInvestigacionDetalleRepository trabajoInvestigacionDetalleRepository;

    @Autowired
    private AdministracionDetalleRepository administracionDetalleRepository;

    @Autowired
    private TipoActividadRepository tipoActividadRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ObjectMapper objectMapper;

    // Estrategias para guardar detalles específicos
    private final Map<Class<?>, BiConsumer<Actividad, Object>> saveStrategies = Map.of(
            DocenciaDetalleDTO.class, (actividad, detalle) -> saveDocenciaDetalle(actividad, (DocenciaDetalleDTO) detalle),
            TrabajoDocenciaDetalleDTO.class, (actividad, detalle) -> saveTrabajoDocenciaDetalle(actividad, (TrabajoDocenciaDetalleDTO) detalle),
            ProyectoInvestigacionDetalleDTO.class, (actividad, detalle) -> saveProyectoInvestigacionDetalle(actividad,(ProyectoInvestigacionDetalleDTO) detalle),
            CapacitacionDetalleDTO.class,(actividad, detalle) -> saveCapacitacionDetalle(actividad, (CapacitacionDetalleDTO) detalle),
            OtroServicioDetalleDTO.class,(actividad, detalle) -> saveOtroServicioDetalle(actividad, (OtroServicioDetalleDTO) detalle),
            ExtensionDetalleDTO.class,(actividad, detalle) -> saveExtensionDetalle(actividad, (ExtensionDetalleDTO) detalle),
            TrabajoInvestigacionDetalleDTO.class, (actividad, detalle) -> saveTrabajoInvestigacionDetalle(actividad,(TrabajoInvestigacionDetalleDTO) detalle),
            AdministracionDetalleDTO.class, (actividad, detalle) -> saveAdministracionDetalle(actividad,(AdministracionDetalleDTO) detalle));

    // Estrategias para actualizar detalles específicos
    private final Map<Class<?>, BiConsumer<Actividad, Object>> updateStrategies = Map.of(
            DocenciaDetalleDTO.class,(actividad, detalle) -> updateDocenciaDetalle(actividad, (DocenciaDetalleDTO) detalle),
            TrabajoDocenciaDetalleDTO.class,(actividad, detalle) -> updateTrabajoDocenciaDetalle(actividad, (TrabajoDocenciaDetalleDTO) detalle),
            ProyectoInvestigacionDetalleDTO.class,(actividad, detalle) -> updateProyectoInvestigacionDetalle(actividad,(ProyectoInvestigacionDetalleDTO) detalle),
            CapacitacionDetalleDTO.class,(actividad, detalle) -> updateCapacitacionDetalle(actividad, (CapacitacionDetalleDTO) detalle),
            OtroServicioDetalleDTO.class,
            (actividad, detalle) -> updateOtroServicioDetalle(actividad, (OtroServicioDetalleDTO) detalle),
            ExtensionDetalleDTO.class,
            (actividad, detalle) -> updateExtensionDetalle(actividad, (ExtensionDetalleDTO) detalle),
            TrabajoInvestigacionDetalleDTO.class, (actividad, detalle) -> updateTrabajoInvestigacionDetalle(actividad,(TrabajoInvestigacionDetalleDTO) detalle),
            AdministracionDetalleDTO.class, (actividad, detalle) -> updateAdministracionDetalle(actividad,(AdministracionDetalleDTO) detalle));

    /**
     * Guarda el detalle específico de una actividad basado en el tipo del DTO.
     *
     * @param actividad La entidad Actividad a la que pertenece el detalle.
     * @param detalle   El detalle específico en formato DTO o un LinkedHashMap.
     */
    public void saveActivityDetail(Actividad actividad, Object detalle) {
        if (detalle instanceof LinkedHashMap) {
            Integer idTipoActividad = actividad.getTipoActividad().getOidTipoActividad();
            TipoActividad tipoActividad = tipoActividadRepository.findById(idTipoActividad)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Tipo de actividad no encontrado con ID: " + idTipoActividad));
            detalle = convertLinkedHashMapToSpecificDTO(detalle, tipoActividad.getNombre());
        }

        BiConsumer<Actividad, Object> strategy = saveStrategies.get(detalle.getClass());
        if (strategy != null) {
            strategy.accept(actividad, detalle);
        } else {
            throw new IllegalArgumentException("Tipo de detalle no soportado: " + detalle.getClass().getName());
        }
    }

    /**
     * Convierte un LinkedHashMap en el DTO específico basado en el nombre del tipo
     * de actividad.
     *
     * @param detalle       El objeto LinkedHashMap que representa el detalle.
     * @param tipoActividad El nombre del tipo de actividad.
     * @return El detalle convertido al DTO específico.
     */
    private Object convertLinkedHashMapToSpecificDTO(Object detalle, String tipoActividad) {
        switch (tipoActividad) {
            case "DOCENCIA":
                return objectMapper.convertValue(detalle, DocenciaDetalleDTO.class);
            case "TRABAJO DE DOCENCIA":
                return objectMapper.convertValue(detalle, TrabajoDocenciaDetalleDTO.class);
            case "PROYECTO DE INVESTIGACIÓN":
                return objectMapper.convertValue(detalle, ProyectoInvestigacionDetalleDTO.class);
            case "CAPACITACIÓN":
                return objectMapper.convertValue(detalle, CapacitacionDetalleDTO.class);
            case "OTRO SERVICIO":
                return objectMapper.convertValue(detalle, OtroServicioDetalleDTO.class);
            case "EXTENSIÓN":
                return objectMapper.convertValue(detalle, ExtensionDetalleDTO.class);
            case "TRABAJO DE INVESTIGACIÓN":
                return objectMapper.convertValue(detalle, TrabajoInvestigacionDetalleDTO.class);
            case "ADMINISTRACIÓN":
                return objectMapper.convertValue(detalle, AdministracionDetalleDTO.class);
            default: throw new IllegalArgumentException("Tipo de actividad no soportado para la conversión: " + tipoActividad);
        }
    }

    /**
     * Actualiza los detalles específicos de una actividad.
     *
     * @param actividad La entidad Actividad a la que pertenece el detalle.
     * @param detalle   DTO con los datos actualizados del detalle.
     */
    public void updateActivityDetail(Actividad actividad, Object detalle) {
        if (detalle instanceof LinkedHashMap) {
            detalle = convertLinkedHashMapToSpecificDTO(detalle, actividad.getTipoActividad().getNombre());
        }
        BiConsumer<Actividad, Object> strategy = updateStrategies.get(detalle.getClass());
        if (strategy != null) {
            strategy.accept(actividad, detalle);
        } else {
            throw new IllegalArgumentException("Tipo de detalle no soportado: " + detalle.getClass().getName());
        }
    }

    private void saveDocenciaDetalle(Actividad actividad, DocenciaDetalleDTO detalleDTO) {
        DocenciaDetalle detalle = new DocenciaDetalle();
        detalle.setActividad(actividad);
        detalle.setCodigo(detalleDTO.getCodigo());
        detalle.setGrupo(detalleDTO.getGrupo());
        detalle.setMateria(detalleDTO.getMateria());
        docenciaDetalleRepository.save(detalle);
    }

    private void updateDocenciaDetalle(Actividad actividad, DocenciaDetalleDTO detalleDTO) {
        DocenciaDetalle detalleExistente = docenciaDetalleRepository.findById(actividad.getOidActividad())
                .orElseThrow(() -> new IllegalArgumentException("Detalle de docencia no encontrado."));

        detalleExistente.setCodigo(detalleDTO.getCodigo());
        detalleExistente.setGrupo(detalleDTO.getGrupo());
        detalleExistente.setMateria(detalleDTO.getMateria());
        docenciaDetalleRepository.save(detalleExistente);
    }

    private void saveTrabajoDocenciaDetalle(Actividad actividad, TrabajoDocenciaDetalleDTO detalleDTO) {
        TrabajoDocenciaDetalle detalle = new TrabajoDocenciaDetalle();
        detalle.setActividad(actividad);
        detalle.setActoAdministrativo(detalleDTO.getActoAdministrativo());
        trabajoDocenciaDetalleRepository.save(detalle);
    }

    private void updateTrabajoDocenciaDetalle(Actividad actividad, TrabajoDocenciaDetalleDTO detalleDTO) {
        TrabajoDocenciaDetalle detalleExistente = trabajoDocenciaDetalleRepository.findById(actividad.getOidActividad())
                .orElseThrow(() -> new IllegalArgumentException("Detalle de trabajo de docencia no encontrado."));

        detalleExistente.setActoAdministrativo(detalleDTO.getActoAdministrativo());
        trabajoDocenciaDetalleRepository.save(detalleExistente);
    }

    private void saveProyectoInvestigacionDetalle(Actividad actividad, ProyectoInvestigacionDetalleDTO detalleDTO) {
        ProyectoInvestigacionDetalle detalle = new ProyectoInvestigacionDetalle();
        detalle.setActividad(actividad);
        detalle.setVri(detalleDTO.getVri());
        detalle.setNombreProyecto(detalleDTO.getNombreProyecto());
        proyectoInvestigacionDetalleRepository.save(detalle);
    }

    private void updateProyectoInvestigacionDetalle(Actividad actividad, ProyectoInvestigacionDetalleDTO detalleDTO) {
        ProyectoInvestigacionDetalle detalleExistente = proyectoInvestigacionDetalleRepository
                .findById(actividad.getOidActividad())
                .orElseThrow(() -> new IllegalArgumentException("Detalle de proyecto de investigación no encontrado."));

        detalleExistente.setVri(detalleDTO.getVri());
        detalleExistente.setNombreProyecto(detalleDTO.getNombreProyecto());
        proyectoInvestigacionDetalleRepository.save(detalleExistente);
    }

    private void saveCapacitacionDetalle(Actividad actividad, CapacitacionDetalleDTO detalleDTO) {
        CapacitacionDetalle detalle = new CapacitacionDetalle();
        detalle.setActividad(actividad);
        detalle.setActoAdministrativo(detalleDTO.getActoAdministrativo());
        detalle.setDetalle(detalleDTO.getDetalle());
        capacitacionDetalleRepository.save(detalle);
    }

    private void saveOtroServicioDetalle(Actividad actividad, OtroServicioDetalleDTO detalleDTO) {
        OtroServicioDetalle detalle = new OtroServicioDetalle();
        detalle.setActividad(actividad);
        detalle.setActoAdministrativo(detalleDTO.getActoAdministrativo());
        detalle.setDetalle(detalleDTO.getDetalle());
        otroServicioDetalleRepository.save(detalle);
    }

    private void saveExtensionDetalle(Actividad actividad, ExtensionDetalleDTO detalleDTO) {
        ExtensionDetalle detalle = new ExtensionDetalle();
        detalle.setActividad(actividad);
        detalle.setActoAdministrativo(detalleDTO.getActoAdministrativo());
        detalle.setNombreProyecto(detalleDTO.getNombreProyecto());
        extensionDetalleRepository.save(detalle);
    }

    private void saveTrabajoInvestigacionDetalle(Actividad actividad, TrabajoInvestigacionDetalleDTO detalleDTO) {
        TrabajoInvestigacionDetalle detalle = new TrabajoInvestigacionDetalle();
        detalle.setActividad(actividad);
        detalle.setActoAdministrativo(detalleDTO.getActoAdministrativo());
        trabajoInvestigacionDetalleRepository.save(detalle);
    }

    private void updateCapacitacionDetalle(Actividad actividad, CapacitacionDetalleDTO detalleDTO) {
        CapacitacionDetalle detalleExistente = capacitacionDetalleRepository.findById(actividad.getOidActividad())
                .orElseThrow(() -> new IllegalArgumentException("Detalle de capacitación no encontrado."));
        detalleExistente.setActoAdministrativo(detalleDTO.getActoAdministrativo());
        detalleExistente.setDetalle(detalleDTO.getDetalle());
        capacitacionDetalleRepository.save(detalleExistente);
    }

    private void updateOtroServicioDetalle(Actividad actividad, OtroServicioDetalleDTO detalleDTO) {
        OtroServicioDetalle detalleExistente = otroServicioDetalleRepository.findById(actividad.getOidActividad())
                .orElseThrow(() -> new IllegalArgumentException("Detalle de otro servicio no encontrado."));
        detalleExistente.setActoAdministrativo(detalleDTO.getActoAdministrativo());
        detalleExistente.setDetalle(detalleDTO.getDetalle());
        otroServicioDetalleRepository.save(detalleExistente);
    }

    private void updateExtensionDetalle(Actividad actividad, ExtensionDetalleDTO detalleDTO) {
        ExtensionDetalle detalleExistente = extensionDetalleRepository.findById(actividad.getOidActividad())
                .orElseThrow(() -> new IllegalArgumentException("Detalle de extensión no encontrado."));
        detalleExistente.setActoAdministrativo(detalleDTO.getActoAdministrativo());
        detalleExistente.setNombreProyecto(detalleDTO.getNombreProyecto());
        extensionDetalleRepository.save(detalleExistente);
    }

    private void updateTrabajoInvestigacionDetalle(Actividad actividad, TrabajoInvestigacionDetalleDTO detalleDTO) {
        TrabajoInvestigacionDetalle detalleExistente = trabajoInvestigacionDetalleRepository
                .findById(actividad.getOidActividad())
                .orElseThrow(() -> new IllegalArgumentException("Detalle de trabajo de investigación no encontrado."));
        detalleExistente.setActoAdministrativo(detalleDTO.getActoAdministrativo());
        trabajoInvestigacionDetalleRepository.save(detalleExistente);
    }

    private void saveAdministracionDetalle(Actividad actividad, AdministracionDetalleDTO detalleDTO) {
        AdministracionDetalle detalle = new AdministracionDetalle();
        detalle.setActividad(actividad);
        detalle.setActoAdministrativo(detalleDTO.getActoAdministrativo());
        detalle.setDetalle(detalleDTO.getDetalle());
        administracionDetalleRepository.save(detalle);
    }

    private void updateAdministracionDetalle(Actividad actividad, AdministracionDetalleDTO detalleDTO) {
        AdministracionDetalle detalleExistente = administracionDetalleRepository
                .findById(actividad.getOidActividad())
                .orElseThrow(() -> new IllegalArgumentException("Detalle de administración no encontrado."));
        detalleExistente.setActoAdministrativo(detalleDTO.getActoAdministrativo());
        administracionDetalleRepository.save(detalleExistente);
    }

    /**
     * Genera el nombre de la actividad basado en el tipo de actividad y sus
     * detalles.
     *
     * @param actividadDTO DTO que contiene la información de la actividad.
     * @return El nombre generado para la actividad.
     */
    public String generarNombreActividad(ActividadBaseDTO actividadDTO) {
        Integer idTipoActividad = actividadDTO.getTipoActividad().getOidTipoActividad();
        TipoActividad tipoActividad = tipoActividadRepository.findById(idTipoActividad).orElseThrow(() -> new IllegalArgumentException("Tipo de actividad no encontrado con ID: " + idTipoActividad));

        // Obtener el nombre del tipo de actividad
        String actividad = tipoActividad.getNombre();
        Object detalle = actividadDTO.getDetalle();

        ObjectMapper objectMapper = new ObjectMapper();

        switch (actividad) {
            case "DOCENCIA":
                DocenciaDetalleDTO docenciaDetalle = objectMapper.convertValue(detalle, DocenciaDetalleDTO.class);
                return String.format("%s-%s-%s", docenciaDetalle.getCodigo(), docenciaDetalle.getMateria(),docenciaDetalle.getGrupo());
            case "TRABAJO DE DOCENCIA":
            case "TRABAJO DE INVESTIGACIÓN":
                TrabajoDocenciaDetalleDTO trabajoDocenciaDetalle = objectMapper.convertValue(detalle,TrabajoDocenciaDetalleDTO.class);
                Integer idUsuario = actividadDTO.getOidEvaluador();
                Usuario usuario = usuarioRepository.findById(idUsuario).orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + idUsuario));
                String identificacion = usuario.getIdentificacion();
                return String.format("%s-%s", trabajoDocenciaDetalle.getActoAdministrativo(), identificacion);
            case "PROYECTO DE INVESTIGACIÓN":
                ProyectoInvestigacionDetalleDTO proyectoDetalle = objectMapper.convertValue(detalle,ProyectoInvestigacionDetalleDTO.class);
                return String.format("%s-%s", proyectoDetalle.getVri(),proyectoDetalle.getNombreProyecto());
            case "ADMINISTRACIÓN":
                AdministracionDetalleDTO administracionDetalle = objectMapper.convertValue(detalle,AdministracionDetalleDTO.class);
                return String.format("%s-ACTIVIDAD", administracionDetalle.getActoAdministrativo());
            case "EXTENSIÓN":
                ExtensionDetalleDTO extensionDetalle = objectMapper.convertValue(detalle, ExtensionDetalleDTO.class);
                return String.format("%s-%s", extensionDetalle.getActoAdministrativo(),extensionDetalle.getNombreProyecto());
            case "OTRO SERVICIO":
                OtroServicioDetalleDTO otroServicioDetalle = objectMapper.convertValue(detalle,OtroServicioDetalleDTO.class);
                return String.format("%s-ACTIVIDAD", otroServicioDetalle.getActoAdministrativo());
            case "CAPACITACIÓN":
                CapacitacionDetalleDTO capacitacionDetalle = objectMapper.convertValue(detalle,CapacitacionDetalleDTO.class);
                return String.format("%s-ACTIVIDAD", capacitacionDetalle.getActoAdministrativo());
            default:
                throw new IllegalArgumentException("Tipo de actividad no reconocido: " + tipoActividad);
        }
    }

}
