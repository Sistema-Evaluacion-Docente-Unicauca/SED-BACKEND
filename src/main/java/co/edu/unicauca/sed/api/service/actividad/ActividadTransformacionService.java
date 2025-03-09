package co.edu.unicauca.sed.api.service.actividad;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.edu.unicauca.sed.api.domain.Actividad;
import co.edu.unicauca.sed.api.domain.Fuente;
import co.edu.unicauca.sed.api.dto.FuenteDTO;
import co.edu.unicauca.sed.api.dto.actividad.ActividadPaginadaDTO;

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
		float porcentaje = calculoService.calcularPorcentaje(actividad.getHoras(), horasTotales);
		double promedio = calculoService.calcularPromedio(actividad.getFuentes());
		double acumulado = calculoService.calcularAcumulado(promedio, porcentaje);

		int totalFuentes = Optional.ofNullable(actividad.getFuentes())
				.orElse(Collections.emptyList())
				.stream()
				.filter(fuente -> fuente.getCalificacion() != null)
				.mapToInt(f -> 1)
				.sum();

		return Map.of(
				"oidActividad", actividad.getOidActividad(),
				"nombre", actividad.getNombreActividad(),
				"horas", actividad.getHoras(),
				"fuentes", transformarFuentes(actividad.getFuentes()),
				"porcentaje", porcentaje,
				"promedio", promedio,
				"acumulado", acumulado,
				"totalFuentes", totalFuentes);
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
						fuente.getEstadoFuente() != null
								? fuente.getEstadoFuente().getNombreEstado()
								: null,
						fuente.getCalificacion(),
						fuente.getTipoFuente() != null ? fuente.getTipoFuente() : "Sin tipo"))
				.collect(Collectors.toList());
	}

	public ActividadPaginadaDTO construirActividadPaginadaDTO(Page<Actividad> actividadPage) {
		List<Actividad> actividades = actividadPage.getContent();
		float totalHoras = calculoService.calcularTotalHoras(actividades);

		Map<String, List<Map<String, Object>>> actividadesPorTipo = agruparActividadesPorTipo(actividades, totalHoras);

		ActividadPaginadaDTO actividadPaginadaDTO = new ActividadPaginadaDTO();
		actividadPaginadaDTO.setActividades(actividadesPorTipo);
		actividadPaginadaDTO.setCurrentPage(actividadPage.getNumber());
		actividadPaginadaDTO.setPageSize(actividadPage.getSize());
		actividadPaginadaDTO.setTotalItems((int) actividadPage.getTotalElements());
		actividadPaginadaDTO.setTotalPages(actividadPage.getTotalPages());

		return actividadPaginadaDTO;
	}

	public Map<String, List<Map<String, Object>>> agruparActividadesPorTipo(List<Actividad> actividades, float totalHoras) {
		return actividades.stream().sorted(Comparator.comparing(a -> a.getTipoActividad().getNombre()))
			.collect(Collectors.groupingBy(
				actividad -> String.valueOf(actividad.getTipoActividad().getNombre()),
				Collectors.mapping(
					actividad -> (Map<String, Object>) transformarActividad(actividad, totalHoras),
					Collectors.toList())));
	}
}
