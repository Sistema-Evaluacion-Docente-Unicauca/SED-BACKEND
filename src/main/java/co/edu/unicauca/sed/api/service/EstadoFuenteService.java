package co.edu.unicauca.sed.api.service;

import co.edu.unicauca.sed.api.model.EstadoFuente;
import co.edu.unicauca.sed.api.repository.EstadoFuenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EstadoFuenteService {

    @Autowired
    private EstadoFuenteRepository estadoFuenteRepository;

    /**
     * Listar todos los registros de EstadoFuente con paginación.
     *
     * @param pageable objeto de paginación
     * @return Página de EstadoFuente
     */
    @Transactional(readOnly = true)
    public Page<EstadoFuente> findAll(Pageable pageable) {
        return estadoFuenteRepository.findAll(pageable);
    }

    /**
     * Buscar EstadoFuente por ID.
     *
     * @param id ID del EstadoFuente.
     * @return Objeto EstadoFuente o excepción si no se encuentra.
     */
    @Transactional(readOnly = true)
    public EstadoFuente findById(Integer id) {
        return estadoFuenteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("EstadoFuente no encontrado con ID: " + id));
    }

    /**
     * Guardar o actualizar un EstadoFuente.
     *
     * @param estadoFuente objeto EstadoFuente.
     * @return EstadoFuente guardado.
     */
    @Transactional
    public EstadoFuente save(EstadoFuente estadoFuente) {
        return estadoFuenteRepository.save(estadoFuente);
    }

    /**
     * Eliminar un EstadoFuente por ID.
     *
     * @param id ID del EstadoFuente a eliminar.
     */
    @Transactional
    public void deleteById(Integer id) {
        if (!estadoFuenteRepository.existsById(id)) {
            throw new RuntimeException("EstadoFuente no encontrado con ID: " + id);
        }
        estadoFuenteRepository.deleteById(id);
    }

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
