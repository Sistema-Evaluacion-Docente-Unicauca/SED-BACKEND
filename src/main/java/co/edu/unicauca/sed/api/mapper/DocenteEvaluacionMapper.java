package co.edu.unicauca.sed.api.mapper;

import co.edu.unicauca.sed.api.dto.DocenteEvaluacionDTO;
import co.edu.unicauca.sed.api.model.Actividad;
import co.edu.unicauca.sed.api.model.Usuario;

import java.util.List;

public class DocenteEvaluacionMapper {

    /**
     * Transforma un objeto Usuario en un DocenteEvaluacionDTO.
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

        float porcentajeCompletado = totalFuentes > 0 ? (fuentesCompletadas / (float) totalFuentes) * 100 : 0;
        String estadoConsolidado = porcentajeCompletado == 100 ? "Completo" : "En progreso";

        return new DocenteEvaluacionDTO(
                usuario.getNombres() + " " + usuario.getApellidos(),
                usuario.getUsuarioDetalle().getIdentificacion(),
                usuario.getUsuarioDetalle().getContratacion(),
                Math.round(porcentajeCompletado * 100.0) / 100.0f,
                estadoConsolidado
        );
    }
}
