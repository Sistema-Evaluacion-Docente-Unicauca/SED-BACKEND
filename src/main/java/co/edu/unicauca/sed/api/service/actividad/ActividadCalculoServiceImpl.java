package co.edu.unicauca.sed.api.service.actividad;

import co.edu.unicauca.sed.api.domain.Actividad;
import co.edu.unicauca.sed.api.domain.Fuente;
import co.edu.unicauca.sed.api.utils.MathUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Implementación del servicio para cálculos de actividades.
 */
@Service
public class ActividadCalculoServiceImpl implements ActividadCalculoService {

    @Override
    public float calcularTotalHoras(List<Actividad> actividades) {
        return (float) actividades.stream()
                .mapToDouble(Actividad::getHoras)
                .sum();
    }

    @Override
    public int calcularPorcentaje(float horasActividad, float horasTotales) {
        return horasTotales > 0 ? Math.round((horasActividad / horasTotales) * 100) : 0;
    }

    @Override
    public double calcularPromedio(List<Fuente> fuentes) {
        return MathUtils.redondearDecimal(
                fuentes.stream()
                    .filter(f -> f.getCalificacion() != null)
                    .mapToDouble(Fuente::getCalificacion)
                    .average()
                    .orElse(0),
                2).doubleValue();
    }

    @Override
    public double calcularAcumulado(double promedio, float porcentaje) {
        if (porcentaje <= 0) {
            return 0;
        }
        double acumulado = promedio * (porcentaje / 100);
        return Math.round(acumulado * 100.0) / 100.0;
    }

    @Override
    public double calcularTotalPorcentaje(Map<String, List<Map<String, Object>>> actividadesPorTipo) {
        double total = actividadesPorTipo.values().stream()
            .flatMap(List::stream)
            .mapToDouble(actividad -> ((Number) actividad.getOrDefault("porcentaje", 0)).doubleValue())
            .sum();
    
        return Math.min(Math.round(total), 100.0);
    }

    @Override
    public double calcularTotalAcumulado(Map<String, List<Map<String, Object>>> actividadesPorTipo) {
        return actividadesPorTipo.values().stream()
                .flatMap(List::stream)
                .mapToDouble(actividad -> ((Number) actividad.getOrDefault("acumulado", 0)).doubleValue())
                .sum();
    }
}
