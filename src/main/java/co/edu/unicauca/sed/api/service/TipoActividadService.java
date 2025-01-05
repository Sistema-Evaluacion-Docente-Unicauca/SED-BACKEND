package co.edu.unicauca.sed.api.service;

import java.util.Optional;
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
        Optional<TipoActividad> resultado = this.tipoActividadRepository.findById(oid);
        return resultado.orElse(null);
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
            convertirCamposAMayusculas(tipoActividad);
    
            return this.tipoActividadRepository.save(tipoActividad);
        } catch (Exception e) {
            throw new RuntimeException("Error al guardar el tipo de actividad: " + e.getMessage(), e);
        }
    }

    /**
    * Actualiza un tipo de actividad existente.
    *
    * @param oid           El identificador de la actividad a actualizar.
    * @param tipoActividad Datos actualizados de la actividad.
    * @return true si la actualización fue exitosa, false si la actividad no se encontró.
    */
    public boolean update(Integer oid, TipoActividad tipoActividad) {
        Optional<TipoActividad> existingTipoActividad = tipoActividadRepository.findById(oid);
        if (existingTipoActividad.isPresent()) {
            // Convertir los campos a mayúsculas
            convertirCamposAMayusculas(tipoActividad);
            tipoActividad.setOidTipoActividad(oid);
            tipoActividadRepository.save(tipoActividad);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Elimina un tipo de actividad por su identificador único (OID).
     *
     * @param oid El identificador de la actividad que se desea eliminar.
     */
    public void delete(Integer oid) {
        tipoActividadRepository.deleteById(oid);
    }

    /**
     * Convierte los campos `descripcion` y `nombre` de un TipoActividad a mayúsculas.
     *
     * @param tipoActividad El objeto TipoActividad a procesar.
     */
    private void convertirCamposAMayusculas(TipoActividad tipoActividad) {
        if (tipoActividad.getDescripcion() != null) {
            tipoActividad.setDescripcion(tipoActividad.getDescripcion().toUpperCase());
        }
        if (tipoActividad.getNombre() != null) {
            tipoActividad.setNombre(tipoActividad.getNombre().toUpperCase());
        }
    }
}
