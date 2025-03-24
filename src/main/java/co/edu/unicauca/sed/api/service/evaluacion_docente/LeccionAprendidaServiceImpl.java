package co.edu.unicauca.sed.api.service.evaluacion_docente;

import co.edu.unicauca.sed.api.dto.LeccionDTO;
import co.edu.unicauca.sed.api.domain.Autoevaluacion;
import co.edu.unicauca.sed.api.domain.LeccionAprendida;
import co.edu.unicauca.sed.api.repository.LeccionAprendidaRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LeccionAprendidaServiceImpl implements LeccionAprendidaService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LeccionAprendidaServiceImpl.class);
    private final LeccionAprendidaRepository leccionAprendidaRepository;

    @Override
    public void guardar(List<LeccionDTO> lecciones, Autoevaluacion autoevaluacion) {
        if (lecciones == null || lecciones.isEmpty()) {
            LOGGER.info("📌 No se recibieron lecciones para guardar.");
            return;
        }

        lecciones.forEach(leccion -> {
            if (leccion.getDescripcion() != null && !leccion.getDescripcion().isBlank()) {
                LeccionAprendida entidad = new LeccionAprendida();
                entidad.setAutoevaluacion(autoevaluacion);
                entidad.setDescripcion(leccion.getDescripcion());
                leccionAprendidaRepository.save(entidad);
                LOGGER.debug("✅ Lección guardada: {}", leccion.getDescripcion());
            }
        });
    }
}
