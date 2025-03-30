package co.edu.unicauca.sed.api.controller;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import co.edu.unicauca.sed.api.domain.Fuente;
import co.edu.unicauca.sed.api.service.fuente.FuenteService;

/**
 * Controlador para la gestión de las fuentes (Fuentes).
 * Proporciona endpoints para operaciones CRUD y manejo de archivos asociados.
 */
@Controller
@RequestMapping("api/fuente")
public class FuenteController {

    private static final Logger logger = LoggerFactory.getLogger(FuenteController.class);

    @Autowired
    private FuenteService fuenteService;

    /**
     * Recupera todas las fuentes y las devuelve en la respuesta.
     *
     * @return Lista de todas las fuentes o un error en caso de falla.
     */
    @GetMapping
    public ResponseEntity<?> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<Fuente> fuentes = fuenteService.obtenerTodos(PageRequest.of(page, size));
            if (fuentes.hasContent()) {
                return ResponseEntity.ok().body(fuentes);
            } else {
                logger.warn("No se encontraron fuentes");
                return ResponseEntity.noContent().build();
            }
        } catch (Exception e) {
            logger.error("Error al obtener las fuentes: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    /**
     * Encuentra una fuente específica por su ID.
     *
     * @param oid El ID de la fuente.
     * @return La fuente si es encontrada, o un error 404 si no lo es.
     */
    @GetMapping("/{oid}")
    public ResponseEntity<?> find(@PathVariable Integer oid) {
        Fuente resultado = fuenteService.buscarPorId(oid);
        if (resultado != null) {
            return ResponseEntity.ok().body(resultado);
        }
        logger.warn("Fuente con ID {} no encontrada", oid);
        return ResponseEntity.notFound().build();
    }

    /**
     * Guarda una nueva fuente junto con un archivo asociado.
     *
     * @param informeFuente El archivo a asociar con la fuente.
     * @param observation   Observaciones relacionadas con la fuente.
     * @param sourcesJson   Información de las fuentes en formato JSON.
     * @param allFiles      Map opcional de archivos adicionales.
     * @return Mensaje de éxito o error en caso de problemas al procesar.
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> saveFuente(
            @RequestParam("informeFuente") MultipartFile informeFuente,
            @RequestParam("observation") String observation,
            @RequestParam(required = false) String tipoCalificacion,
            @RequestParam("sources") String sourcesJson,
            @RequestParam(required = false) Map<String, MultipartFile> allFiles) {
        try {
            if (allFiles != null) {
                allFiles.forEach((key, file) -> logger.debug("   ➝ Archivo '{}' con tamaño {} bytes",
                        file.getOriginalFilename(), file.getSize()));
            } else {
                logger.debug("📌 Parámetro [allFiles]: No se recibieron archivos adicionales.");
            }
            fuenteService.guardarFuente(sourcesJson, informeFuente, observation, allFiles);
            return ResponseEntity.ok("Archivos procesados correctamente");
        } catch (Exception e) {
            logger.debug("Error al procesar los archivos: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error procesando los archivos");
        }
    }

    /**
     * Elimina una fuente por su ID.
     *
     * @param oid El ID de la fuente a eliminar.
     * @return Mensaje de confirmación si se elimina, o error en caso de conflictos.
     */
    @DeleteMapping("/{oid}")
    public ResponseEntity<?> delete(@PathVariable Integer oid) {
        logger.info("Solicitud recibida para eliminar la fuente con ID: {}", oid);
        Fuente fuente = null;
        try {
            fuente = fuenteService.buscarPorId(oid);
            if (fuente == null) {
                logger.warn("Fuente con ID {} no encontrada", oid);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Fuente no encontrada");
            }
        } catch (Exception e) {
            logger.error("Error al buscar la fuente con ID {}: {}", oid, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Fuente no encontrada");
        }

        try {
            fuenteService.eliminar(oid);
            logger.info("Fuente con ID {} eliminada exitosamente", oid);
        } catch (Exception e) {
            logger.error("Error al eliminar la fuente con ID {}: {}", oid, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.CONFLICT).body("No se puede borrar por conflictos con otros datos");
        }
        return ResponseEntity.ok().build();
    }

    /**
     * Endpoint para descargar un archivo asociado a una fuente.
     *
     * @param id       El ID de la fuente.
     * @param isReport Bandera para determinar si se debe descargar el informe
     *                 (true) o el documento fuente (false).
     * @return El archivo solicitado como recurso descargable.
     */
    @GetMapping("/download/{id}")
    public ResponseEntity<?> downloadFile(
            @PathVariable("id") Integer id,
            @RequestParam(name = "report", defaultValue = "false") boolean isReport) {
        return fuenteService.obtenerArchivo(id, isReport);
    }
}
