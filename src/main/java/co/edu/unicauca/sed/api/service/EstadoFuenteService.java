package co.edu.unicauca.sed.api.service;

import org.springframework.stereotype.Service;
import co.edu.unicauca.sed.api.model.EstadoFuente;

@Service
public class EstadoFuenteService {

    /**
     * Crea una nueva instancia de `EstadoFuente` con el ID especificado.
     *
     * @param oidEstado ID del estado fuente.
     * @return Instancia de EstadoFuente.
     */
    public EstadoFuente createEstadoFuente(int oidEstado) {
        EstadoFuente stateSource = new EstadoFuente();
        stateSource.setOidEstadoFuente(oidEstado);
        return stateSource;
    }
}
