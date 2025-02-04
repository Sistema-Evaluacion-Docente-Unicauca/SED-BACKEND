package co.edu.unicauca.sed.api.service.actividad;

import org.springframework.stereotype.Service;

import co.edu.unicauca.sed.api.model.Fuente;
import co.edu.unicauca.sed.api.utils.MathUtils;
import co.edu.unicauca.sed.api.model.Actividad;

import java.util.List;

@Service
public class ActividadCalculoService {

    /**
     * Calcula el total de horas de una lista de actividades.
     *
     * @param actividades Lista de actividades.
     * @return Total de horas.
     */
    public float calcularTotalHoras(List<Actividad> actividades) {
        return (float) actividades.stream()
                .mapToDouble(actividad -> actividad.getHoras())
                .sum();
    }

    /**
     * Calcula el porcentaje de horas de una actividad respecto al total.
     *
     * @param horasActividad Horas de la actividad.
     * @param horasTotales   Horas totales.
     * @return Porcentaje calculado redondeado a 2 decimales.
     */
    public int calcularPorcentaje(float horasActividad, float horasTotales) {
        return horasTotales > 0 ? Math.round((horasActividad / horasTotales) * 100) : 0;
    }

    /**
     * Calcula el promedio de calificaciones de una lista de fuentes.
     *
     * @param fuentes Lista de fuentes.
     * @return Promedio redondeado a 2 decimales.
     */
    public double calcularPromedio(List<Fuente> fuentes) {
        return MathUtils.redondearDecimal(
                fuentes.stream()
                    .filter(f -> f.getCalificacion() != null)
                    .mapToDouble(Fuente::getCalificacion)
                    .average()
                    .orElse(0),
                2).doubleValue();
    }

    /**
     * Calcula el valor acumulado de una actividad.
     *
     * @param promedio   Promedio de calificaciones.
     * @param porcentaje Porcentaje asociado.
     * @return Valor acumulado redondeado a 2 decimales.
     */
    public double calcularAcumulado(double promedio, float porcentaje) {
        if (porcentaje <= 0) {
            return 0;
        }
        double acumulado = promedio * (porcentaje / 100);
        return Math.round(acumulado * 100.0) / 100.0;
    }
}
