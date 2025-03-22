package co.edu.unicauca.sed.api.service.evaluacion_docente;

import co.edu.unicauca.sed.api.dto.AutoevaluacionDTO;
import co.edu.unicauca.sed.api.dto.ApiResponse;
import org.springframework.web.multipart.MultipartFile;

public interface AutoevaluacionService {
    ApiResponse<Void> guardarAutoevaluacion(
        AutoevaluacionDTO dto,
        MultipartFile firma,
        MultipartFile screenshotSimca,
        MultipartFile documentoNotas
    );

    ApiResponse<Object> buscarPorFuente(Integer oidFuente);
}
