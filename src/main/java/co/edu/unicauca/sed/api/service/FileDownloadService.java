package co.edu.unicauca.sed.api.service;

import co.edu.unicauca.sed.api.model.Usuario;
import co.edu.unicauca.sed.api.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private UsuarioRepository usuarioRepository;

    public InputStream createZipStream(String periodo, boolean esConsolidado, String departamento, String tipoContrato, Integer oidUsuario) throws IOException {
        Path basePath = Paths.get(documentUploadDir);

        if (periodo != null) {
            basePath = basePath.resolve(periodo);
        }
        if (esConsolidado) {
            basePath = basePath.resolve("Consolidados");
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
        return usuarioRepository.findById(oidUsuario)
                .orElseThrow(() -> new EntityNotFoundException("Usuario con ID " + oidUsuario + " no encontrado"));
    }

    private String formatUsuarioFolder(Usuario usuario) {
        return usuario.getNombres().toUpperCase().replace(" ", "_") + "_" + usuario.getApellidos().toUpperCase().replace(" ", "_");
    }
}
