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
            return;
        }

        mejoras.forEach(mejora -> {
            if (mejora.getDescripcion() == null || mejora.getDescripcion().isBlank()) {
                return;
            }

            OportunidadMejora entidad;

            // Actualizar si viene el ID, sino crear nueva
            if (mejora.getOidOportunidadMejora() != null) {
                entidad = oportunidadMejoraRepository.findById(mejora.getOidOportunidadMejora())
                .orElseGet(() -> {
                    return new OportunidadMejora();
                });
            } else {
                entidad = new OportunidadMejora();
            }

            entidad.setAutoevaluacion(autoevaluacion);
            entidad.setDescripcion(mejora.getDescripcion());

            oportunidadMejoraRepository.save(entidad);
        });
    }
}