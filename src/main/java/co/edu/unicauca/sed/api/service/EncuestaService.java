package co.edu.unicauca.sed.api.service;

import co.edu.unicauca.sed.api.domain.Encuesta;
import co.edu.unicauca.sed.api.repository.EncuestaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Servicio para la gestión de encuestas.
 */
@Service
public class EncuestaService {

    @Autowired
    private EncuestaRepository encuestaRepository;

    /**
     * Recupera todas las encuestas con soporte de paginación.
     *
     * @param pageable Objeto que define la paginación (número de página y tamaño de página).
     * @return Página de encuestas encontradas.
     */
    public Page<Encuesta> findAll(Pageable pageable) {
        return encuestaRepository.findAll(pageable);
    }

    /**
     * Busca una encuesta por su OID.
     *
     * @param oid Identificador único de la encuesta.
     * @return Encuesta encontrada o null si no existe.
     */
    public Encuesta findByOid(Integer oid) {
        Optional<Encuesta> resultado = this.encuestaRepository.findById(oid);

        if (resultado.isPresent()) {
            return resultado.get();
        }

        return null;
    }

    /**
     * Guarda una encuesta en la base de datos.
     *
     * @param encuesta Objeto Encuesta a guardar.
     * @return Encuesta guardada o null en caso de error.
     */
    public Encuesta save(Encuesta encuesta) {
        Encuesta result = null;
        try {
            if (encuesta.getNombre() != null) {
                encuesta.setNombre(encuesta.getNombre().toUpperCase());
            }
            result = this.encuestaRepository.save(encuesta);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return result;
    }

    /**
     * Elimina una encuesta por su OID.
     *
     * @param oid Identificador único de la encuesta a eliminar.
     */
    public void delete(Integer oid) {
        this.encuestaRepository.deleteById(oid);
    }
}
