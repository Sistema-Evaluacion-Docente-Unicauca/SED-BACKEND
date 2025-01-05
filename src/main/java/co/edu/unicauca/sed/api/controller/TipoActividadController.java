package co.edu.unicauca.sed.api.controller;

import co.edu.unicauca.sed.api.model.TipoActividad;
import co.edu.unicauca.sed.api.service.TipoActividadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("tipoactividad")
public class TipoActividadController {

    @Autowired
    private TipoActividadService service;

    /**
     * Recupera todas las actividades disponibles con soporte de paginación.
     *
     * @param page Número de página a recuperar (por defecto 0).
     * @param size Cantidad de elementos por página (por defecto 10).
     * @return Lista de actividades disponibles o un error si ocurre algún problema.
     */
    @GetMapping("all")
    public ResponseEntity<?> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<TipoActividad> tiposActividad = service.findAll(PageRequest.of(page, size));
            if (tiposActividad.hasContent()) {
                return ResponseEntity.ok().body(tiposActividad);
            } else {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No hay tipos de actividad disponibles.");
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    /**
     * Busca un tipo de actividad por su identificador único (OID).
     *
     * @param oid El identificador de la actividad.
     * @return La actividad si es encontrada, o un error 404 si no lo es.
     */
    @GetMapping("find/{oid}")
    public ResponseEntity<?> findById(@PathVariable Integer oid) {
        try {
            TipoActividad tipoActividad = service.findByOid(oid);
            if (tipoActividad != null) {
                return ResponseEntity.ok().body(tipoActividad);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Tipo de actividad no encontrado.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    /**
     * Guarda un nuevo tipo de actividad en la base de datos.
     *
     * @param tipoActividad El objeto TipoActividad a guardar.
     * @return El objeto guardado o un mensaje de error.
     */
    @PostMapping("save")
    public ResponseEntity<?> save(@RequestBody TipoActividad tipoActividad) {
        try {
            TipoActividad resultado = service.save(tipoActividad);
            if (resultado != null) {
                return ResponseEntity.ok().body(resultado);
            }
            return ResponseEntity.internalServerError().body("Error: Resultado nulo.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    /**
     * Actualiza un tipo de actividad existente.
     *
     * @param oid           El identificador de la actividad a actualizar.
     * @param tipoActividad Datos actualizados de la actividad.
     * @return Mensaje de éxito o error si ocurre algún problema.
     */
    @PutMapping("update/{oid}")
    public ResponseEntity<?> update(@PathVariable Integer oid, @RequestBody TipoActividad tipoActividad) {
        try {
            boolean updated = service.update(oid, tipoActividad);
            if (updated) {
                return ResponseEntity.ok("Tipo de actividad actualizado correctamente.");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Tipo de actividad no encontrado.");
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    /**
     * Elimina un tipo de actividad por su identificador único (OID).
     *
     * @param oid El identificador de la actividad a eliminar.
     * @return Mensaje de éxito o error en caso de conflictos.
     */
    @DeleteMapping("delete/{oid}")
    public ResponseEntity<?> delete(@PathVariable Integer oid) {
        try {
            TipoActividad tipoActividad = service.findByOid(oid);
            if (tipoActividad == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Tipo de actividad no encontrado.");
            }
            service.delete(oid);
            return ResponseEntity.ok("Tipo de actividad eliminado correctamente.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Error al eliminar el tipo de actividad: " + e.getMessage());
        }
    }
}
