package co.edu.unicauca.sed.api.service.evaluacion_docente;

import co.edu.unicauca.sed.api.domain.EvaluacionEstudiante;
import co.edu.unicauca.sed.api.dto.ApiResponse;
import co.edu.unicauca.sed.api.dto.EvaluacionDocenteDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface EvaluacionEstudianteService {
    
    ApiResponse<Page<EvaluacionEstudiante>> buscarTodos(Pageable pageable);

    ApiResponse<EvaluacionEstudiante> buscarPorId(Integer oid);

    ApiResponse<EvaluacionEstudiante> guardar(EvaluacionEstudiante evaluacionEstudiante);

    ApiResponse<Void> guardarEvaluacionDocente(EvaluacionDocenteDTO dto, MultipartFile documentoFuente, MultipartFile firmaEstudiante);

    ApiResponse<Void> eliminar(Integer oid);

    ApiResponse<Object> buscarPorFuente(Integer oidFuente);
}

