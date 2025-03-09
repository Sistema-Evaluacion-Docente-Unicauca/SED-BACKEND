package co.edu.unicauca.sed.api.service;

import co.edu.unicauca.sed.api.domain.Fuente;
import co.edu.unicauca.sed.api.dto.FuenteDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

/**
 * Servicio para la conversión de entidades Fuente a DTO.
 */
@Service
public class FuenteDTOService {

    /**
     * Convierte una entidad Fuente en un FuenteDTO.
     *
     * @param fuente La entidad Fuente a convertir.
     * @return El objeto FuenteDTO resultante.
     */
    public FuenteDTO convertToDTO(Fuente fuente) {
        if (fuente == null) {
            return null;
        }

        return new FuenteDTO(
                fuente.getOidFuente(),
                fuente.getTipoFuente(),
                fuente.getCalificacion(),
                fuente.getNombreDocumentoFuente(),
                fuente.getNombreDocumentoInforme(),
                fuente.getObservacion(),
                fuente.getFechaCreacion(),
                fuente.getFechaActualizacion(),
                fuente.getEstadoFuente() != null ? fuente.getEstadoFuente().getNombreEstado() : null);
    }

    /**
     * Convierte una entidad Fuente en un FuenteDTO básico con campos específicos.
     *
     * @param fuente La entidad Fuente a convertir.
     * @return El objeto FuenteDTO básico resultante.
     */
    public FuenteDTO convertToBasicDTO(Fuente fuente) {
        if (fuente == null) {
            return null;
        }

        return new FuenteDTO(
                fuente.getOidFuente(),
                fuente.getEstadoFuente() != null ? fuente.getEstadoFuente().getNombreEstado() : null,
                fuente.getCalificacion(),
                fuente.getTipoFuente());
    }

    /**
     * Convierte una lista de entidades Fuente en una lista de FuenteDTO.
     *
     * @param fuentes La lista de entidades Fuente a convertir.
     * @return La lista de objetos FuenteDTO resultantes.
     */
    public List<FuenteDTO> convertToFuenteDTOList(List<Fuente> fuentes) {
        if (fuentes == null || fuentes.isEmpty()) {
            return new ArrayList<>();
        }

        return fuentes.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

}
