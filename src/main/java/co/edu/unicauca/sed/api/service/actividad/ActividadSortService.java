package co.edu.unicauca.sed.api.service.actividad;

import co.edu.unicauca.sed.api.dto.actividad.ActividadBaseDTO;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para ordenar listas de actividades por diferentes criterios.
 */
@Service
public class ActividadSortService {

    public static final boolean DEFAULT_ASCENDING_ORDER = true;

    /**
     * Ordena una lista de actividades por el nombre del tipo de actividad.
     *
     * @param actividades    Lista de actividades en formato DTO (heredados de  ActividadBaseDTO).
     * @param ascendingOrder Indica si el orden debe ser ascendente (true) o descendente (false).
     * @return Lista de actividades ordenadas según el criterio especificado.
     */
    public List<ActividadBaseDTO> sortActivitiesByType(List<ActividadBaseDTO> actividades, Boolean ascendingOrder) {
        // Crear un Comparator explícito para ActividadBaseDTO
        Comparator<ActividadBaseDTO> comparator = Comparator.comparing(actividad -> actividad.getTipoActividad().getNombre());

        // Invertir el orden si no es ascendente
        if (ascendingOrder != null && !ascendingOrder) {
            comparator = comparator.reversed();
        }

        // Ordenar y retornar la lista de actividades
        return actividades.stream().sorted(comparator).collect(Collectors.toList());
    }
}
