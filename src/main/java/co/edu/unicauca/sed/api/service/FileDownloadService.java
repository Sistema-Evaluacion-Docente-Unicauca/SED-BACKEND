package co.edu.unicauca.sed.api.service;

import co.edu.unicauca.sed.api.model.Usuario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.*;
import java.nio.file.*;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class FileDownloadService {

    @Value("${DOCUMENT_UPLOAD_DIR}")
    private String documentUploadDir;

    public InputStream createZipStream(String periodo, String departamento, String tipoContrato, Integer oidUsuario) throws IOException {
        Path basePath = Paths.get(documentUploadDir);

        if (periodo != null) {
            basePath = basePath.resolve(periodo);
        }
        if (departamento != null) {
            basePath = basePath.resolve(departamento);
        }
        if (tipoContrato != null) {
            basePath = basePath.resolve(tipoContrato);
        }
        if (oidUsuario != null) {
            Usuario usuario = getUsuarioById(oidUsuario);
            if (usuario == null) {
                throw new FileNotFoundException("No se encontró el usuario con ID: " + oidUsuario);
            }
            String nombreUsuario = formatUsuarioFolder(usuario);
            basePath = basePath.resolve(nombreUsuario);
        }

        if (!Files.exists(basePath)) {
            throw new FileNotFoundException("No se encontró la ruta: " + basePath);
        }

        // 🔹 Definir `finalBasePath` para evitar el error en la expresión lambda
        final Path finalBasePath = basePath;

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            try (Stream<Path> paths = Files.walk(finalBasePath)) {
                paths.filter(Files::isRegularFile)
                     .forEach(path -> {
                        try {
                            Path relativePath = finalBasePath.relativize(path);
                            ZipEntry zipEntry = new ZipEntry(relativePath.toString());
                            zos.putNextEntry(zipEntry);
                            Files.copy(path, zos);
                            zos.closeEntry();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
            }
        }
        return new ByteArrayInputStream(baos.toByteArray());
    }

    private Usuario getUsuarioById(Integer oidUsuario) {
        // Aquí iría la consulta a la base de datos para obtener el usuario
        return new Usuario(oidUsuario);
    }

    private String formatUsuarioFolder(Usuario usuario) {
        return usuario.getNombres().toUpperCase().replace(" ", "_") + "_" + usuario.getApellidos().toUpperCase().replace(" ", "_");
    }
}
