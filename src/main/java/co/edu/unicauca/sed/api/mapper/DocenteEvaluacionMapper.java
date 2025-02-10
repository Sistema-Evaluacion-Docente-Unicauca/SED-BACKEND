package co.edu.unicauca.sed.api.mapper;

import co.edu.unicauca.sed.api.dto.DocenteEvaluacionDTO;
import co.edu.unicauca.sed.api.model.Actividad;
import co.edu.unicauca.sed.api.model.Usuario;
import co.edu.unicauca.sed.api.utils.MathUtils;

import java.util.List;

public class DocenteEvaluacionMapper {

    /**
     * Transforma un objeto Usuario y sus actividades asociadas en un DocenteEvaluacionDTO.
     *
     * @param usuario     Usuario a transformar.
     * @param actividades Lista de actividades asociadas al usuario.
     * @return DocenteEvaluacionDTO.
     */
    public static DocenteEvaluacionDTO toDto(Usuario usuario, List<Actividad> actividades) {
        int totalFuentes = actividades.stream().mapToInt(actividad -> actividad.getFuentes().size()).sum();
        int fuentesCompletadas = actividades.stream()
                .flatMap(actividad -> actividad.getFuentes().stream())
                .filter(fuente -> "Diligenciado".equalsIgnoreCase(fuente.getEstadoFuente().getNombreEstado()))
                .mapToInt(fuente -> 1)
                .sum();

        float porcentajeCompletado = MathUtils.calcularPorcentajeCompletado(totalFuentes, fuentesCompletadas);
        String estadoConsolidado = porcentajeCompletado == 100 ? "Completo" : "En progreso";

        return new DocenteEvaluacionDTO(
                usuario.getOidUsuario(),
                usuario.getNombres() + " " + usuario.getApellidos(),
                usuario.getIdentificacion(),
                usuario.getUsuarioDetalle().getContratacion(),
                porcentajeCompletado,
                estadoConsolidado
        );
    }
}
