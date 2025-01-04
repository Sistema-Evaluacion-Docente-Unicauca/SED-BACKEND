package co.edu.unicauca.sed.api.service.actividad;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.edu.unicauca.sed.api.dto.FuenteDTO;
import co.edu.unicauca.sed.api.model.Actividad;
import co.edu.unicauca.sed.api.model.Fuente;

@Service
public class ActividadTransformacionService {

    @Autowired
    private ActividadCalculoService calculoService;

    /**
     * Transforma una actividad en un mapa de datos con cálculos específicos.
     *
     * @param actividad    Actividad a transformar.
     * @param horasTotales Total de horas para calcular porcentajes.
     * @return Mapa con los datos de la actividad transformados.
     */
    public Map<String, Object> transformarActividad(Actividad actividad, float horasTotales) {
        float porcentaje = calculoService.calcularPorcentaje(actividad.getHorasSemanales(), horasTotales);
        double promedio = calculoService.calcularPromedio(actividad.getFuentes());
        double acumulado = calculoService.calcularAcumulado(promedio, porcentaje);

        int totalFuentes = actividad.getFuentes().size();

        return Map.of(
                "oidActividad", actividad.getOidActividad(),
                "codigoActividad", actividad.getCodigoActividad(),
                "nombre", actividad.getNombre(),
                "horas", actividad.getHorasSemanales(),
                "fuentes", transformarFuentes(actividad.getFuentes()),
                "porcentaje", porcentaje,
                "promedio", promedio,
                "acumulado", acumulado,
                "totalFuentes", totalFuentes
        );
    }

    /**
     * Transforma una lista de fuentes en una lista de DTOs.
     *
     * @param fuentes Lista de fuentes.
     * @return Lista de FuenteDTO con los datos transformados.
     */
    public List<FuenteDTO> transformarFuentes(List<Fuente> fuentes) {
        return fuentes.stream()
                .sorted(Comparator.comparing(fuente -> fuente.getTipoFuente()))
                .map(fuente -> new FuenteDTO(
                        fuente.getOidFuente(),
                        fuente.getEstadoFuente() != null ? fuente.getEstadoFuente().getNombreEstado() : null,
                        fuente.getCalificacion(),
                        fuente.getTipoFuente() != null ? fuente.getTipoFuente() : "Sin tipo"))
                .collect(Collectors.toList());
    }
}
