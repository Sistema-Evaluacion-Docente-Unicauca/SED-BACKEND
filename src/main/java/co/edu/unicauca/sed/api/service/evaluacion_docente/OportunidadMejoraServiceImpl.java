package co.edu.unicauca.sed.api.service.evaluacion_docente;

import co.edu.unicauca.sed.api.dto.OportunidadMejoraDTO;
import co.edu.unicauca.sed.api.domain.Autoevaluacion;
import co.edu.unicauca.sed.api.domain.OportunidadMejora;
import co.edu.unicauca.sed.api.repository.OportunidadMejoraRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OportunidadMejoraServiceImpl implements OportunidadMejoraService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OportunidadMejoraServiceImpl.class);
    private final OportunidadMejoraRepository oportunidadMejoraRepository;

    @Override
    public void guardar(List<OportunidadMejoraDTO> mejoras, Autoevaluacion autoevaluacion) {
        if (mejoras == null || mejoras.isEmpty()) {
            LOGGER.info("📌 No se recibieron oportunidades de mejora para guardar.");
            return;
        }

        mejoras.forEach(mejora -> {
            if (mejora.getDescripcion() != null && !mejora.getDescripcion().isBlank()) {
                OportunidadMejora entidad = new OportunidadMejora();
                entidad.setAutoevaluacion(autoevaluacion);
                entidad.setDescripcion(mejora.getDescripcion());
                oportunidadMejoraRepository.save(entidad);
                LOGGER.debug("✅ Oportunidad de mejora guardada: {}", mejora.getDescripcion());
            }
        });
    }
}