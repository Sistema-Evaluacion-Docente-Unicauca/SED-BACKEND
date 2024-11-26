package co.edu.unicauca.sed.api.controller;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import co.edu.unicauca.sed.api.model.Fuente;
import co.edu.unicauca.sed.api.service.FuenteService;

@Controller
@RequestMapping("fuente")
public class FuenteController {

    @Autowired
    private FuenteService fuenteService;

    /**
     * Retrieves all sources (fuentes) and returns them in the response.
     * 
     * @return List of all sources
     */
    @GetMapping("all")
    public ResponseEntity<?> findAll() {
        try {
            List<Fuente> list = (List<Fuente>) fuenteService.findAll();
            if (list != null && !list.isEmpty()) {
                return ResponseEntity.ok().body(list);
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Finds a specific source by its ID.
     * 
     * @param oid The ID of the source
     * @return The source if found, or 404 if not
     */
    @GetMapping("find/{oid}")
    public ResponseEntity<?> find(@PathVariable Integer oid) {
        Fuente resultado = fuenteService.findByOid(oid);
        if (resultado != null) {
            return ResponseEntity.ok().body(resultado);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Saves a new source (fuente) along with an associated file.
     * 
     * @param fuente  The source to save
     * @param archivo The file to associate with the source
     * @return The saved source, or an error if something went wrong
     */
    @PostMapping(value = "/save", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> saveFuente(
            @RequestParam("informeFuente") MultipartFile informeFuente,
            @RequestParam("observation") String observation,
            @RequestParam("sources") String sourcesJson,
            @RequestParam(required = false) Map<String, MultipartFile> allFiles) {
        try {
            fuenteService.saveSource(sourcesJson, informeFuente, observation, allFiles);
            return ResponseEntity.ok("Archivos procesados correctamente");
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error procesando los archivos");
        }
    }

    /**
     * Deletes a source by its ID.
     * 
     * @param oid The ID of the source to delete
     * @return A confirmation message if deleted, or an error if there are conflicts
     */
    @DeleteMapping("delete/{oid}")
    public ResponseEntity<?> delete(@PathVariable Integer oid) {
        Fuente fuente = null;
        try {
            fuente = fuenteService.findByOid(oid);
            if (fuente == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Fuente no encontrada");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Fuente no encontrada");
        }

        try {
            fuenteService.delete(oid); // Attempt to delete the source
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.CONFLICT).body("No se puede borrar por conflictos con otros datos");
        }
        return ResponseEntity.ok().build();
    }

    /**
     * Endpoint to download a file (RUTADOCUMENTOFUENTE by default or RUTADOCUMENTOINFORME if requested).
     *
     * @param id The ID of the Fuente entity
     * @param informe A flag to determine if RUTADOCUMENTOINFORME should be downloaded (optional, default false)
     * @return The requested file as a downloadable resource
     */
    @GetMapping("/download/{id}")
    public ResponseEntity<?> downloadFile(
            @PathVariable("id") Integer id,
            @RequestParam(name = "report", defaultValue = "false") boolean isReport) {
        return fuenteService.getFile(id, isReport);
    }
}
