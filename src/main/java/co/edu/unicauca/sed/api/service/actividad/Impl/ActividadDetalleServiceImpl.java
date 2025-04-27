package co.edu.unicauca.sed.api.service.actividad.Impl;

import co.edu.unicauca.sed.api.domain.TipoActividad;
import co.edu.unicauca.sed.api.domain.Usuario;
import co.edu.unicauca.sed.api.dto.AtributoDTO;
import co.edu.unicauca.sed.api.dto.actividad.ActividadBaseDTO;
import co.edu.unicauca.sed.api.repository.TipoActividadRepository;
import co.edu.unicauca.sed.api.repository.UsuarioRepository;
import co.edu.unicauca.sed.api.service.actividad.ActividadDetalleService;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementación del servicio de detalle de actividad.
 */
@Service
public class ActividadDetalleServiceImpl implements ActividadDetalleService {

    private final TipoActividadRepository tipoActividadRepository;
    private final UsuarioRepository usuarioRepository;

    public ActividadDetalleServiceImpl(TipoActividadRepository tipoActividadRepository,
            UsuarioRepository usuarioRepository) {
        this.tipoActividadRepository = tipoActividadRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public String generarNombreActividad(ActividadBaseDTO actividadDTO) {
        Integer idTipoActividad = actividadDTO.getTipoActividad().getOidTipoActividad();
        TipoActividad tipoActividad = tipoActividadRepository.findById(idTipoActividad)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Tipo de actividad no encontrado con ID: " + idTipoActividad));
    
        String nombreTipoActividad = tipoActividad.getNombre();
        List<AtributoDTO> atributos = actividadDTO.getAtributos();
    
        // Buscar valores específicos de los atributos según el tipo de actividad
        String codigo = obtenerValorAtributo(atributos, "CODIGO");
        String materia = obtenerValorAtributo(atributos, "MATERIA");
        String grupo = obtenerValorAtributo(atributos, "GRUPO");
        String actoAdministrativo = obtenerValorAtributo(atributos, "ACTOADMINISTRATIVO");
        String vri = obtenerValorAtributo(atributos, "VRI");
        String nombreProyecto = obtenerValorAtributo(atributos, "NOMBREPROYECTO");
    
        Integer idUsuario = actividadDTO.getOidEvaluador();
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + idUsuario));
        String identificacion = usuario.getIdentificacion();
    
        // Generar el nombre de la actividad según el tipo de actividad
        String nombreGenerado;
        switch (nombreTipoActividad.toUpperCase()) {
            case "DOCENCIA":
                nombreGenerado = String.format("%s-%s-%s", codigo, materia, grupo);
                break;
            case "TRABAJO DE DOCENCIA":
            case "TRABAJO DE INVESTIGACIÓN":
                nombreGenerado = String.format("%s-%s", actoAdministrativo, identificacion);
                break;
            case "PROYECTO DE INVESTIGACIÓN":
                nombreGenerado = String.format("%s-%s", vri, nombreProyecto);
                break;
            case "ADMINISTRACIÓN":
            case "OTROS SERVICIOS":
            case "CAPACITACIÓN":
                nombreGenerado = String.format("%s-ACTIVIDAD", actoAdministrativo);
                break;
            case "EXTENSIÓN":
                nombreGenerado = String.format("%s-%s", actoAdministrativo, nombreProyecto);
                break;
            case "ASESORÍA":
                nombreGenerado = String.format("%s-%s", actoAdministrativo);
                break;
            default:
                nombreGenerado = "ACTIVIDAD-GENERICA-" + idTipoActividad;
                break;
        }
        
        return nombreGenerado;
    }    

    private String obtenerValorAtributo(List<AtributoDTO> atributos, String codigoAtributo) {
        return atributos.stream()
            .filter(a -> a.getCodigoAtributo().equalsIgnoreCase(codigoAtributo))
            .map(AtributoDTO::getValor)
            .findFirst()
            .orElse("");
    }
}
