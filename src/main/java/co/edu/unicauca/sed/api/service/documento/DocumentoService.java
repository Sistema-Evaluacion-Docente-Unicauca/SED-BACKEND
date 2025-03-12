package co.edu.unicauca.sed.api.service.documento;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class DocumentoService {

    @Value("${files.basePath}")
    private String basePath;

    public Boolean upload(String name, MultipartFile archivo) {

        Path carpeta = Paths.get(basePath);
        try {
            Files.copy(archivo.getInputStream(), carpeta.resolve(name + ".pdf"));
        } catch (Exception e2) {
            try {
                Files.delete(carpeta.resolve(name + ".pdf"));
                Files.copy(archivo.getInputStream(), carpeta.resolve(name + ".pdf"));
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    public Resource getResource(String name) {
        Resource resource = null;
        try {
            Path carpeta = Paths.get(basePath);
            Path archivo = carpeta.resolve(name + ".pdf");
            resource = new UrlResource(archivo.toUri());
        } catch (Exception e) {
            e.printStackTrace();
            return resource;
        }
        return resource;
    }
}
