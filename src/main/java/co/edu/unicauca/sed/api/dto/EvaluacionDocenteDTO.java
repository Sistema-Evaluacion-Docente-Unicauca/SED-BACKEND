package co.edu.unicauca.sed.api.dto;

import lombok.Data;
import java.util.List;

@Data
public class EvaluacionDocenteDTO {
    private Integer oidFuente;
    private EncuestaDTO encuesta;
    private List<EncuestaPreguntaDTO> preguntas;
    private EvaluacionEstudianteDTO evaluacionEstudiante;
}
