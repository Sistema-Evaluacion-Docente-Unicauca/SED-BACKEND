package co.edu.unicauca.sed.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import co.edu.unicauca.sed.api.service.DocumentoService;

@RestController
@RequestMapping("api/documento")
public class DocumentoController {
    @Autowired
    private DocumentoService documentoService;

    @PostMapping(value = "/upload/{name}", headers = "content-type=multipart/*", consumes = "application/*")
    public ResponseEntity<?> uploadFile(@PathVariable String name, @RequestParam("archivo") MultipartFile archivo) {
        if (documentoService.upload(name, archivo)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.internalServerError().body("error cargando el documento");
    }

    @GetMapping("download")
    public ResponseEntity<?> downloadDocumento(@RequestParam String name) {
        Resource resource = documentoService.getResource(name);
        if (resource.isFile() || resource.isReadable()) {
            return ResponseEntity.ok().contentType(MediaType.MULTIPART_FORM_DATA)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body("ExeptionMessage:No es archivo o el archivo no pudo ser leido");
    }
}