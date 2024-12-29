package co.edu.unicauca.sed.api.controller;

import co.edu.unicauca.sed.api.model.Autoevaluacion;
import co.edu.unicauca.sed.api.service.AutoevaluacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Controlador para la gestión de las Autoevaluaciones.
 * Proporciona endpoints para realizar operaciones CRUD sobre Autoevaluaciones.
 */
@Controller
@RequestMapping("autoevaluacion")
public class AutoevaluacionController {

    private static final Logger logger = LoggerFactory.getLogger(AutoevaluacionController.class);

    @Autowired
    private AutoevaluacionService service;

    /**
     * Recupera todas las autoevaluaciones.
     *
     * @return Lista de autoevaluaciones o un error si ocurre algún problema.
     */
    @GetMapping("all")
    public ResponseEntity<?> findAll() {
        try {
            List<Autoevaluacion> list = service.findAll();
            if (list != null && !list.isEmpty()) {
                logger.info("Se recuperaron {} autoevaluaciones exitosamente", list.size());
                return ResponseEntity.ok().body(list);
            }
        } catch (Exception e) {
            logger.error("Error al recuperar las autoevaluaciones: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
        logger.warn("No se encontraron autoevaluaciones");
        return ResponseEntity.notFound().build();
    }

    /**
     * Recupera una autoevaluación específica por su ID.
     *
     * @param oid ID de la autoevaluación a buscar.
     * @return La autoevaluación encontrada o un error 404 si no existe.
     */
    @GetMapping("find/{oid}")
    public ResponseEntity<?> findById(@PathVariable Integer oid) {
        Autoevaluacion autoevaluacion = service.findByOid(oid);
        if (autoevaluacion != null) {
            logger.info("Autoevaluación con ID {} encontrada", oid);
            return ResponseEntity.ok().body(autoevaluacion);
        }
        logger.warn("Autoevaluación con ID {} no encontrada", oid);
        return ResponseEntity.notFound().build();
    }

    /**
     * Guarda una nueva autoevaluación.
     *
     * @param autoevaluacion Objeto Autoevaluacion a guardar.
     * @return La autoevaluación guardada o un error si ocurre algún problema.
     */
    @PostMapping("save")
    public ResponseEntity<?> save(@RequestBody Autoevaluacion autoevaluacion) {
        try {
            Autoevaluacion resultado = service.save(autoevaluacion);
            if (resultado != null) {
                return ResponseEntity.ok().body(resultado);
            }
        } catch (Exception e) {
            logger.error("Error al guardar la autoevaluación: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
        logger.error("La operación de guardado devolvió una respuesta nula");
        return ResponseEntity.internalServerError().body("Error: Resultado nulo");
    }

    /**
     * Elimina una autoevaluación por su ID.
     *
     * @param oid ID de la autoevaluación a eliminar.
     * @return Confirmación de eliminación o un error si ocurre un problema.
     */
    @DeleteMapping("delete/{oid}")
    public ResponseEntity<?> delete(@PathVariable Integer oid) {
        logger.info("Solicitud recibida para eliminar la autoevaluación con ID: {}", oid);
        Autoevaluacion autoevaluacion = null;
        try {
            autoevaluacion = service.findByOid(oid);
            if (autoevaluacion == null) {
                logger.warn("Autoevaluación con ID {} no encontrada", oid);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Autoevaluación no encontrada");
            }
        } catch (Exception e) {
            logger.error("Error al buscar la autoevaluación con ID {}: {}", oid, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Autoevaluación no encontrada");
        }

        try {
            service.delete(oid);
            logger.info("Autoevaluación con ID {} eliminada exitosamente", oid);
        } catch (Exception e) {
            logger.error("Error al eliminar la autoevaluación con ID {}: {}", oid, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.CONFLICT).body("No se puede borrar por conflictos con otros datos");
        }
        return ResponseEntity.ok().build();
    }
}
