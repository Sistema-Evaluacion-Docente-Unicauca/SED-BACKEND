package co.edu.unicauca.sed.api.service.actividad;

import co.edu.unicauca.sed.api.dto.actividad.*;
import co.edu.unicauca.sed.api.enums.TipoActividadEnum;
import co.edu.unicauca.sed.api.model.*;
import co.edu.unicauca.sed.api.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Arrays;
import jakarta.persistence.Id;

@Service
public class ActividadDetalleService {

    private static final Logger logger = LoggerFactory.getLogger(ActividadDetalleService.class);

    @Autowired
    private TipoActividadRepository tipoActividadRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private ObjectMapper objectMapper;
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

    public <T, E> void saveDetalle(Actividad actividad, T detalleDTO, Class<E> entityClass) {
        E entity = objectMapper.convertValue(detalleDTO, entityClass);
        setActividad(entity, actividad);
        getRepository(entityClass).save(entity);
    }

    public <T, E> void updateDetalle(Actividad actividad, T detalleDTO, Class<E> entityClass) {
        TipoActividadEnum tipoActividadEnum = TipoActividadEnum.fromOid(actividad.getTipoActividad().getOidTipoActividad());
    
        if (tipoActividadEnum == null || !tipoActividadEnum.getEntityClass().equals(entityClass)) {
            throw new IllegalArgumentException("El detalle no es compatible con el tipo de actividad actual.");
        }
    
        JpaRepository<E, Integer> repository = getRepository(entityClass);
        Optional<E> optionalEntity = buscarDetallePorActividad(repository, actividad.getOidActividad());
    
        // 🔹 Si no existe, usamos `saveDetalle()` en lugar de crearlo manualmente
        if (optionalEntity.isEmpty()) {
            logger.info("No se encontró detalle para la actividad con ID " + actividad.getOidActividad() + ". Creando nuevo detalle.");
            saveDetalle(actividad, detalleDTO, entityClass);
            return; // ⬅️ Evita continuar con la actualización ya que ya se guardó
        }
    
        E existingEntity = optionalEntity.get();
        
        try {
            Field idField = Arrays.stream(existingEntity.getClass().getDeclaredFields())
                    .filter(field -> field.isAnnotationPresent(Id.class))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("No se encontró un campo @Id en la entidad " + entityClass.getSimpleName()));
    
            idField.setAccessible(true);
            Integer originalId = (Integer) idField.get(existingEntity);
    
            objectMapper.updateValue(existingEntity, detalleDTO);
    
            idField.set(existingEntity, originalId);
        } catch (Exception e) {
            throw new IllegalStateException("Error al actualizar el detalle de " + entityClass.getSimpleName(), e);
        }
        repository.save(existingEntity);
    }
    

    private <E> void setActividad(E entity, Actividad actividad) {
        try {
            Method setActividadMethod = entity.getClass().getMethod("setActividad", Actividad.class);
            setActividadMethod.invoke(entity, actividad);
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo asignar la actividad a " + entity.getClass().getSimpleName(), e);
        }
    }

    private <E> JpaRepository<E, Integer> getRepository(Class<E> entityClass) {
        if (entityClass == null) {
            throw new IllegalArgumentException("Clase de entidad es null. Asegúrate de mapear correctamente el DTO.");
        }
    
        return switch (entityClass.getSimpleName()) {
            case "DocenciaDetalle" -> (JpaRepository<E, Integer>) docenciaDetalleRepository;
            case "TrabajoDocenciaDetalle" -> (JpaRepository<E, Integer>) trabajoDocenciaDetalleRepository;
            case "ProyectoInvestigacionDetalle" -> (JpaRepository<E, Integer>) proyectoInvestigacionDetalleRepository;
            case "CapacitacionDetalle" -> (JpaRepository<E, Integer>) capacitacionDetalleRepository;
            case "OtroServicioDetalle" -> (JpaRepository<E, Integer>) otroServicioDetalleRepository;
            case "ExtensionDetalle" -> (JpaRepository<E, Integer>) extensionDetalleRepository;
            case "TrabajoInvestigacionDetalle" -> (JpaRepository<E, Integer>) trabajoInvestigacionDetalleRepository;
            case "AdministracionDetalle" -> (JpaRepository<E, Integer>) administracionDetalleRepository;
            default -> throw new IllegalArgumentException("No hay repositorio para la clase " + entityClass.getSimpleName());
        };
    }

    public String generarNombreActividad(ActividadBaseDTO actividadDTO) {
        Integer idTipoActividad = actividadDTO.getTipoActividad().getOidTipoActividad();
        TipoActividad tipoActividad = tipoActividadRepository.findById(idTipoActividad).orElseThrow(
                () -> new IllegalArgumentException("Tipo de actividad no encontrado con ID: " + idTipoActividad));

        // Obtener el nombre del tipo de actividad
        String actividad = tipoActividad.getNombre();
        Object detalle = actividadDTO.getDetalle();

        ObjectMapper objectMapper = new ObjectMapper();

        switch (actividad) {
            case "DOCENCIA":
                DocenciaDetalleDTO docenciaDetalle = objectMapper.convertValue(detalle, DocenciaDetalleDTO.class);
                return String.format("%s-%s-%s", docenciaDetalle.getCodigo(), docenciaDetalle.getMateria(), docenciaDetalle.getGrupo());
            case "TRABAJO DE DOCENCIA":
            case "TRABAJO DE INVESTIGACIÓN":
                TrabajoDocenciaDetalleDTO trabajoDocenciaDetalle = objectMapper.convertValue(detalle, TrabajoDocenciaDetalleDTO.class);
                Integer idUsuario = actividadDTO.getOidEvaluador();
                Usuario usuario = usuarioRepository.findById(idUsuario).orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + idUsuario));
                String identificacion = usuario.getIdentificacion();
                return String.format("%s-%s", trabajoDocenciaDetalle.getActoAdministrativo(), identificacion);
            case "PROYECTO DE INVESTIGACIÓN":
                ProyectoInvestigacionDetalleDTO proyectoDetalle = objectMapper.convertValue(detalle, ProyectoInvestigacionDetalleDTO.class);
                return String.format("%s-%s", proyectoDetalle.getVri(), proyectoDetalle.getNombreProyecto());
            case "ADMINISTRACIÓN":
                AdministracionDetalleDTO administracionDetalle = objectMapper.convertValue(detalle, AdministracionDetalleDTO.class);
                return String.format("%s-ACTIVIDAD", administracionDetalle.getActoAdministrativo());
            case "EXTENSIÓN":
                ExtensionDetalleDTO extensionDetalle = objectMapper.convertValue(detalle, ExtensionDetalleDTO.class);
                return String.format("%s-%s", extensionDetalle.getActoAdministrativo(), extensionDetalle.getNombreProyecto());
            case "OTRO SERVICIO":
                OtroServicioDetalleDTO otroServicioDetalle = objectMapper.convertValue(detalle, OtroServicioDetalleDTO.class);
                return String.format("%s-ACTIVIDAD", otroServicioDetalle.getActoAdministrativo());
            case "CAPACITACIÓN":
                CapacitacionDetalleDTO capacitacionDetalle = objectMapper.convertValue(detalle, CapacitacionDetalleDTO.class);
                return String.format("%s-ACTIVIDAD", capacitacionDetalle.getActoAdministrativo());
            default:
                throw new IllegalArgumentException("Tipo de actividad no reconocido: " + tipoActividad);
        }
    }

    public Object convertirDetalleADTO(ActividadBaseDTO actividadDTO) {
        if (actividadDTO.getDetalle() instanceof LinkedHashMap) {
            int oidTipoActividad = actividadDTO.getTipoActividad().getOidTipoActividad();
            TipoActividadEnum tipoActividadEnum = TipoActividadEnum.fromOid(oidTipoActividad);
    
            if (tipoActividadEnum == null) {
                throw new IllegalArgumentException("No se encontró un tipo de actividad válido para OID: " 
                    + oidTipoActividad + ". Verifica que el OID está registrado en TipoActividadEnum.");
            }
    
            Class<?> dtoClass = tipoActividadEnum.getDtoClass();
            return objectMapper.convertValue(actividadDTO.getDetalle(), dtoClass);
        }
        return actividadDTO.getDetalle();
    }

    private <E> Optional<E> buscarDetallePorActividad(JpaRepository<E, Integer> repository, Integer oidActividad) {
        try {
            // Intentar llamar a findByActividadOidActividad(Integer)
            return (Optional<E>) repository.getClass()
                    .getMethod("findByActividadOidActividad", Integer.class)
                    .invoke(repository, oidActividad);
        } catch (Exception e) {
            throw new IllegalArgumentException("No se pudo invocar findByActividadOidActividad en el repositorio: " 
                + repository.getClass().getSimpleName(), e);
        }
    }

    public void actualizarDetalle(Actividad actividad, ActividadBaseDTO actividadDTO) {
        TipoActividadEnum tipoActividadEnum = TipoActividadEnum.fromOid(actividadDTO.getTipoActividad().getOidTipoActividad());
    
        if (tipoActividadEnum == null) {
            throw new IllegalArgumentException("No se encontró un tipo de actividad válido para OID: " 
                + actividadDTO.getTipoActividad().getOidTipoActividad());
        }
    
        Class<?> entityClass = tipoActividadEnum.getEntityClass();
    
        if (entityClass == null) {
            throw new IllegalArgumentException("No se encontró la entidad para el detalle del tipo de actividad.");
        }
    
        Object detalleConvertido = convertirDetalleADTO(actividadDTO);
    
        updateDetalle(actividad, detalleConvertido, entityClass);
    }

    /**
     * Maneja el cambio de tipo de actividad eliminando los detalles anteriores
     * si es necesario y actualizando la actividad con el nuevo tipo.
     *
     * @param actividad La actividad a modificar.
     * @param nuevoOidTipoActividad El nuevo tipo de actividad.
     */
    public void cambiarTipoActividad(Actividad actividad, Integer nuevoOidTipoActividad) {
        eliminarDetalle(actividad);
        TipoActividad tipoActividadExistente = tipoActividadRepository.findById(nuevoOidTipoActividad)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró un tipo de actividad válido para el OID: " + nuevoOidTipoActividad));
    
        actividad.setTipoActividad(tipoActividadExistente);
    }
    
    /**
     * Elimina el detalle de la actividad si existe.
     *
     * @param actividad La actividad de la que se eliminará el detalle.
     */
    public void eliminarDetalle(Actividad actividad) {
        TipoActividadEnum tipoActividadEnum = TipoActividadEnum.fromOid(actividad.getTipoActividad().getOidTipoActividad());
    
        if (tipoActividadEnum == null) {
            throw new IllegalArgumentException("No se encontró un tipo de actividad válido.");
        }
    
        logger.info("Buscando detalle para la actividad con ID {} del tipo de actividad '{}'", 
                     actividad.getOidActividad(), tipoActividadEnum.getNombre());
    
        Class<?> entityClass = tipoActividadEnum.getEntityClass();
        JpaRepository<Object, Integer> repository = (JpaRepository<Object, Integer>) getRepository(entityClass);
    
        // Buscar el detalle usando findByActividadOidActividad
        Optional<?> detalleOptional = buscarDetallePorActividad(repository, actividad.getOidActividad());
    
        if (detalleOptional.isEmpty()) {
            logger.warn("No se encontró detalle para la actividad con ID {} en la BD.", actividad.getOidActividad());
            return;
        }
    
        Object detalle = detalleOptional.get(); // Obtener el objeto del Optional
    
        logger.info("Eliminando detalle encontrado para la actividad con ID {}", actividad.getOidActividad());
    
        // 🔹 Convertir el objeto al tipo correcto antes de eliminarlo
        repository.delete(detalle);
        repository.flush();
    
        logger.info("Detalle eliminado correctamente para la actividad con ID {}", actividad.getOidActividad());
    }    
}
