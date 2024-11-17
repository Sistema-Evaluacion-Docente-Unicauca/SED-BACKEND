package co.edu.unicauca.sed.api.controller;

import java.nio.file.Paths;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import java.util.Optional;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Path;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import co.edu.unicauca.sed.api.dto.FuenteCreateDTO;
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
    public ResponseEntity<String> saveMultipleSources(
            @RequestParam("informeFuente") MultipartFile informeFuente,
            @RequestParam("sources") String sourcesJson,
            @RequestParam(value = "observation", required = false) String observation) 
    {
        try {
            // Convertir JSON de fuentes a lista de DTOs
            ObjectMapper mapper = new ObjectMapper();
            List<FuenteCreateDTO> sources = mapper.readValue(sourcesJson, new TypeReference<List<FuenteCreateDTO>>() {});

            fuenteService.saveMultipleSources(sources, informeFuente, observation);

            return ResponseEntity.ok("Fuentes guardadas exitosamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al guardar las fuentes: " + e.getMessage());
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
        return ResponseEntity.ok().build(); // Return 200 if deleted successfully
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> downloadFile(@PathVariable("id") Integer id) {
        try {
            Fuente fuente = fuenteService.findByOid(id);
            Optional<Fuente> fuenteOptional = Optional.ofNullable(fuente);

            if (fuenteOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            Path filePath = Paths.get(fuente.getRutaDocumentoFuente());
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + fuente.getNombreDocumentoFuente() + "\"")
                    .body(resource);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
