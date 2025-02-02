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
            @RequestParam(value = "oidUsuario", required = false) Integer oidUsuario) {

        // Validaciones de parámetros según la jerarquía establecida
        if (departamento != null && periodo == null) {
            return ResponseEntity.badRequest().body("Si se envía 'departamento', también se debe enviar 'periodo'.");
        }
        if (tipoContrato != null && (departamento == null || periodo == null)) {
            return ResponseEntity.badRequest().body("Si se envía 'tipoContrato', también se deben enviar 'periodo' y 'departamento'.");
        }
        if (oidUsuario != null && (tipoContrato == null || departamento == null || periodo == null)) {
            return ResponseEntity.badRequest().body("Si se envía 'oidUsuario', también se deben enviar 'periodo', 'departamento' y 'tipoContrato'.");
        }

        try {
            InputStream zipStream = fileDownloadService.createZipStream(periodo, departamento, tipoContrato, oidUsuario);
            InputStreamResource resource = new InputStreamResource(zipStream);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=descarga_" + (periodo != null ? periodo : "all") + ".zip");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } catch (IOException ex) {
            return ResponseEntity.status(500).body("Error al generar el archivo ZIP: " + ex.getMessage());
        }
    }
}
