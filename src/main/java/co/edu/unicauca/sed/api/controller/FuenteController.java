package co.edu.unicauca.sed.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

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
                return ResponseEntity.ok().body(list); // Return the list of sources
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getStackTrace());
        }
        return ResponseEntity.notFound().build(); // Return 404 if no sources found
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
            return ResponseEntity.ok().body(resultado); // Return the source if found
        }
        return ResponseEntity.notFound().build(); // Return 404 if source not found
    }

    /**
     * Saves a new source (fuente) along with an associated file.
     * 
     * @param fuente  The source to save
     * @param archivo The file to associate with the source
     * @return The saved source, or an error if something went wrong
     */
    @PostMapping(value = "/save", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> save(@ModelAttribute Fuente fuente,
            @RequestParam("archivo") MultipartFile archivo) {
        try {
            Fuente resultado = fuenteService.save(fuente, archivo);
            if (resultado != null) {
                return ResponseEntity.ok().body(resultado);
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
        return ResponseEntity.internalServerError().body("Error: Resultado nulo");
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
        return ResponseEntity.ok().build(); // Return 200 if deleted successfully
    }
}
