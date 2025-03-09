package co.edu.unicauca.sed.api.service.actividad;

import co.edu.unicauca.sed.api.domain.*;
import co.edu.unicauca.sed.api.dto.AtributoDTO;
import co.edu.unicauca.sed.api.dto.actividad.*;
import co.edu.unicauca.sed.api.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ActividadDetalleService {

    @Autowired
    private TipoActividadRepository tipoActividadRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;

    public String generarNombreActividad(ActividadBaseDTO actividadDTO) {
        Integer idTipoActividad = actividadDTO.getTipoActividad().getOidTipoActividad();
        TipoActividad tipoActividad = tipoActividadRepository.findById(idTipoActividad)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Tipo de actividad no encontrado con ID: " + idTipoActividad));

        String nombreTipoActividad = tipoActividad.getNombre();
        List<AtributoDTO> atributos = actividadDTO.getAtributos();

        // 🔹 Buscar valores específicos de los atributos según el tipo de actividad
        String codigo = obtenerValorAtributo(atributos, "CODIGO");
        String materia = obtenerValorAtributo(atributos, "MATERIA");
        String grupo = obtenerValorAtributo(atributos, "GRUPO");
        String actoAdministrativo = obtenerValorAtributo(atributos, "ACTO_ADMINISTRATIVO");
        String vri = obtenerValorAtributo(atributos, "VRI");
        String nombreProyecto = obtenerValorAtributo(atributos, "NOMBRE_PROYECTO");

        Integer idUsuario = actividadDTO.getOidEvaluador();
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + idUsuario));
        String identificacion = usuario.getIdentificacion();

        // 🔹 Generar el nombre de la actividad según el tipo de actividad
        switch (nombreTipoActividad) {
            case "DOCENCIA":
                return String.format("%s-%s-%s", codigo, materia, grupo);
            case "TRABAJO DE DOCENCIA":
            case "TRABAJO DE INVESTIGACIÓN":
                return String.format("%s-%s", actoAdministrativo, identificacion);
            case "PROYECTO DE INVESTIGACIÓN":
                return String.format("%s-%s", vri, nombreProyecto);
            case "ADMINISTRACIÓN":
            case "OTRO SERVICIO":
            case "CAPACITACIÓN":
                return String.format("%s-ACTIVIDAD", actoAdministrativo);
            case "EXTENSIÓN":
                return String.format("%s-%s", actoAdministrativo, nombreProyecto);
            default:
                throw new IllegalArgumentException("Tipo de actividad no reconocido: " + nombreTipoActividad);
        }
    }

    private String obtenerValorAtributo(List<AtributoDTO> atributos, String codigoAtributo) {
        return atributos.stream()
                .filter(a -> a.getCodigoAtributo().equalsIgnoreCase(codigoAtributo))
                .map(AtributoDTO::getValor)
                .findFirst()
                .orElse("");
    }
}
