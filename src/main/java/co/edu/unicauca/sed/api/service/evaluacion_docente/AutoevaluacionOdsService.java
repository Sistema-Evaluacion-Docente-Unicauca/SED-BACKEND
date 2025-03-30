package co.edu.unicauca.sed.api.service.evaluacion_docente;

import java.util.List;
import java.util.Map;
import org.springframework.web.multipart.MultipartFile;
import co.edu.unicauca.sed.api.domain.Autoevaluacion;
import co.edu.unicauca.sed.api.domain.Fuente;
import co.edu.unicauca.sed.api.dto.ArchivoDTO;
import co.edu.unicauca.sed.api.dto.OdsDTO;

public interface AutoevaluacionOdsService {
    void guardarOds(List<OdsDTO> odsList, Autoevaluacion autoevaluacion, Map<Integer, MultipartFile> archivosOds, Fuente fuente);
    ArchivoDTO obtenerArchivoPorId(Integer idOds);
    List<Map<String, Object>> obtenerOds(Autoevaluacion autoevaluacion);
    Map<Integer, MultipartFile> mapearArchivoODS(List<OdsDTO> odsSeleccionados, Map<String, MultipartFile> archivos);
}