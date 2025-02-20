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

    /**
     * Genera un stream ZIP con los archivos correspondientes a la solicitud.
     */
    public InputStream createZipStream(String periodo, boolean esConsolidado, String departamento, String tipoContrato,
            Integer oidUsuario) throws IOException {
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

        // ✅ Manejo de consolidado: Buscar archivo en lugar de carpeta
        if (oidUsuario != null) {
            Usuario usuario = getUsuarioById(oidUsuario);

            if (esConsolidado) {
                // 📌 Formato esperado del archivo Consolidado-[Periodo]-[NombreUsuario].xlsx
                String nombreArchivo = String.format("Consolidado-%s-%s", periodo, formatUsuarioFolder(usuario));
                if (!nombreArchivo.endsWith(".xlsx")) {
                    nombreArchivo += ".xlsx";
                }
                basePath = basePath.resolve(nombreArchivo);

                if (!Files.exists(basePath) || !Files.isRegularFile(basePath)) {
                    throw new FileNotFoundException("No se encontró el archivo de consolidado: " + basePath);
                }
            } else {
                // 📌 Si no es consolidado, buscar carpeta del usuario
                String nombreUsuario = formatUsuarioFolder(usuario);
                basePath = basePath.resolve(nombreUsuario);
            }
        }

        if (!Files.exists(basePath)) {
            throw new FileNotFoundException("No se encontró la ruta: " + basePath);
        }

        return generateZipStream(basePath);
    }

    /**
     * Genera un ZIP a partir de un archivo o directorio.
     */
    private InputStream generateZipStream(Path path) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            if (Files.isRegularFile(path)) {
                // ✅ Agregar un único archivo al ZIP
                addFileToZip(zos, path);
            } else {
                // ✅ Agregar todos los archivos de la carpeta al ZIP
                try (Stream<Path> paths = Files.walk(path)) {
                    paths.filter(Files::isRegularFile).forEach(filePath -> addFileToZip(zos, filePath));
                }
            }
        }
        return new ByteArrayInputStream(baos.toByteArray());
    }

    /**
     * Agrega un archivo individual a un ZIP.
     */
    private void addFileToZip(ZipOutputStream zos, Path filePath) {
        try {
            ZipEntry zipEntry = new ZipEntry(filePath.getFileName().toString());
            zos.putNextEntry(zipEntry);
            Files.copy(filePath, zos);
            zos.closeEntry();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Obtiene un usuario por su ID.
     */
    private Usuario getUsuarioById(Integer oidUsuario) {
        return usuarioRepository.findById(oidUsuario)
                .orElseThrow(() -> new EntityNotFoundException("Usuario con ID " + oidUsuario + " no encontrado"));
    }

    /**
     * Formatea el nombre de usuario en formato "NOMBRE_APELLIDO".
     */
    private String formatUsuarioFolder(Usuario usuario) {
        return usuario.getNombres().toUpperCase().replace(" ", "_") + "_"
                + usuario.getApellidos().toUpperCase().replace(" ", "_");
    }
}
