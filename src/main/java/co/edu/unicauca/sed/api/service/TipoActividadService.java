package co.edu.unicauca.sed.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import co.edu.unicauca.sed.api.model.TipoActividad;
import co.edu.unicauca.sed.api.repository.TipoActividadRepository;
import org.springframework.data.domain.Pageable;

@Service
public class TipoActividadService {

    @Autowired
    private TipoActividadRepository tipoActividadRepository;

    /**
     * Recupera todas las actividades disponibles con soporte de paginación.
     *
     * @param pageable Configuración de la paginación.
     * @return Página de actividades disponibles.
     */
    public Page<TipoActividad> findAll(Pageable pageable) {
        return tipoActividadRepository.findAll(pageable);
    }

    /**
     * Busca un tipo de actividad por su identificador único (OID).
     *
     * @param oid El identificador de la actividad.
     * @return La actividad si es encontrada, o null si no existe.
     */
    public TipoActividad findByOid(Integer oid) {
        return tipoActividadRepository.findById(oid).orElse(null);
    }

    /**
     * Guarda un nuevo tipo de actividad en la base de datos.
     *
     * @param tipoActividad El objeto TipoActividad que se desea guardar.
     * @return El objeto TipoActividad guardado, o null si ocurre un error.
     */
    public TipoActividad save(TipoActividad tipoActividad) {
        try {
            // Convertir los campos a mayúsculas
            tipoActividad.setNombre(tipoActividad.getNombre().toUpperCase());
            tipoActividad.setDescripcion(tipoActividad.getDescripcion().toUpperCase());
            return this.tipoActividadRepository.save(tipoActividad);
        } catch (Exception e) {
            throw new RuntimeException("Error al guardar el tipo de actividad: " + e.getMessage(), e);
        }
    }

    /**
     * Actualiza un tipo de actividad existente.
     *
     * @param oid           El identificador del tipo de actividad a actualizar.
     * @param tipoActividad Datos actualizados del tipo de actividad.
     * @return El objeto actualizado si existe, o lanza una excepción si no se
     *         encuentra.
     */
    public TipoActividad update(Integer oid, TipoActividad tipoActividad) {
        return tipoActividadRepository.findById(oid).map(existingTipoActividad -> {
            if (tipoActividad.getDescripcion() != null) {
                tipoActividad.setDescripcion(tipoActividad.getDescripcion().toUpperCase());
            }
            if (tipoActividad.getNombre() != null) {
                tipoActividad.setNombre(tipoActividad.getNombre().toUpperCase());
            }
            tipoActividad.setOidTipoActividad(oid);
            return tipoActividadRepository.save(tipoActividad);
        }).orElseThrow(() -> new RuntimeException("TipoActividad con ID " + oid + " no encontrado."));
    }

    /**
     * Elimina un tipo de actividad por su identificador único (OID).
     *
     * @param oid El identificador de la actividad que se desea eliminar.
     */
    public void delete(Integer oid) {
        tipoActividadRepository.deleteById(oid);
    }
}
