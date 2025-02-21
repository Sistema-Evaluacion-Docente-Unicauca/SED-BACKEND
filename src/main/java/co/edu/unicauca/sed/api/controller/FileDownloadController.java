package co.edu.unicauca.sed.api.controller;

import co.edu.unicauca.sed.api.service.FileDownloadService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStream;

@RestController
@RequestMapping("api/download")
public class FileDownloadController {

    private final FileDownloadService fileDownloadService;

    public FileDownloadController(FileDownloadService fileDownloadService) {
        this.fileDownloadService = fileDownloadService;
    }

    @GetMapping("/zip")
    public ResponseEntity<?> downloadZip(
            @RequestParam(value = "periodo", required = false) String periodo,
            @RequestParam(value = "departamento", required = false) String departamento,
            @RequestParam(value = "tipoContrato", required = false) String tipoContrato,
            @RequestParam(value = "oidUsuario", required = false) Integer oidUsuario,
            @RequestParam(defaultValue = "false") boolean esConsolidado) {
    
        try {
            InputStream fileStream = fileDownloadService.createZipStream(periodo, esConsolidado, departamento, tipoContrato, oidUsuario);
            InputStreamResource resource = new InputStreamResource(fileStream);
    
            HttpHeaders headers = new HttpHeaders();
            String filename = definirNombreArchivo(periodo, esConsolidado, oidUsuario);
    
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } catch (IOException ex) {
            return ResponseEntity.status(500).body("Error al generar el archivo: " + ex.getMessage());
        }
    }
    
    /**
     * Define el nombre del archivo de salida según el tipo de descarga.
     */
    private String definirNombreArchivo(String periodo, boolean esConsolidado, Integer oidUsuario) {
        if (esConsolidado && oidUsuario != null) {
            return "Consolidado-" + periodo + ".xlsx";
        }
        return esConsolidado ? "Consolidados-" + periodo + ".zip" : "descarga_" + (periodo != null ? periodo : "all") + ".zip";
    }
}
