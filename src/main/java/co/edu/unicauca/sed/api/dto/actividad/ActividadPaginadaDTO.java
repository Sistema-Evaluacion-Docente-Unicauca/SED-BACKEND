package co.edu.unicauca.sed.api.dto.actividad;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class ActividadPaginadaDTO {
    private Map<String, List<Map<String, Object>>> actividades;
    private int paginaActual;
    private int tamanoPagina;
    private int totalElementos;
    private int totalPaginas;
}
