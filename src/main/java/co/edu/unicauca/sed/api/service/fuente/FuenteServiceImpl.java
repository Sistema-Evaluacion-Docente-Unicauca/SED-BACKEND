package co.edu.unicauca.sed.api.service.fuente;

import co.edu.unicauca.sed.api.domain.Actividad;
import co.edu.unicauca.sed.api.domain.EstadoFuente;
import co.edu.unicauca.sed.api.domain.Fuente;
import co.edu.unicauca.sed.api.dto.FuenteCreateDTO;
import co.edu.unicauca.sed.api.repository.EstadoFuenteRepository;
import co.edu.unicauca.sed.api.repository.FuenteRepository;
import co.edu.unicauca.sed.api.service.documento.FileService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Implementación del servicio para el manejo de fuentes y sus archivos
 * asociados.
 */
@Service
public class FuenteServiceImpl implements FuenteService {

    @Autowired
    private FuenteRepository fuenteRepository;

    @Autowired
    private FileService fileService;

    @Autowired
    private FuenteBusinessService businessService;

    @Autowired
    private FuenteIntegrationService integrationService;

    @Autowired
    private EstadoFuenteRepository estadoFuenteRepository;

    private static final Logger logger = LoggerFactory.getLogger(FuenteServiceImpl.class);

    @Value("${document.upload-dir}")
    private String rutaSubida;

    @Override
    public Page<Fuente> obtenerTodos(Pageable pageable) {
        return fuenteRepository.findAll(pageable);
    }

    @Override
    public Fuente buscarPorId(Integer oid) {
        return fuenteRepository.findById(oid).orElse(null);
    }

    @Override
    public List<Fuente> buscarPorActividadId(Integer oidActividad) {
        return fuenteRepository.findByActividadOid(oidActividad);
    }

    @Override
    public Fuente guardar(Fuente fuente) {
        return fuenteRepository.save(fuente);
    }

    @Override
    public void eliminar(Integer oid) {
        fuenteRepository.deleteById(oid);
    }

    @Override
    public void guardarFuente(String fuentesJson, MultipartFile informeFuente, String observacion,
            Map<String, MultipartFile> archivos) {
        try {
            List<FuenteCreateDTO> fuentes = integrationService.convertirJsonAFuentes(fuentesJson);
            Map<String, MultipartFile> archivosEjecutivos = null;

            if (fuentes.stream().anyMatch(fuente -> "1".equals(fuente.getTipoFuente()))) {
                archivosEjecutivos = integrationService.filtrarArchivosEjecutivos(archivos);
            } else {
                logger.info("No se encontraron fuentes tipo 1, no se procesarán archivos adicionales");
            }

            for (FuenteCreateDTO fuenteDTO : fuentes) {
                if (fuenteDTO.getCalificacion() == null || String.valueOf(fuenteDTO.getCalificacion()).isBlank()) {
                    throw new IllegalStateException("Calificación es un campo requerido y no puede estar vacío.");
                }

                if (observacion != null) {
                    observacion = observacion.toUpperCase();
                }

                businessService.procesarFuente(fuenteDTO, informeFuente, observacion, archivosEjecutivos);
            }
        } catch (Exception e) {
            logger.error("Error al guardar fuentes", e);
            throw new RuntimeException("Error durante la operación de guardar fuentes: " + e.getMessage(), e);
        }
    }

    @Override
    public ResponseEntity<?> obtenerArchivo(Integer id, boolean esInforme) {
        try {
            Fuente fuente = fuenteRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Fuente con ID " + id + " no encontrada."));

            String rutaArchivo = esInforme ? fuente.getRutaDocumentoInforme() : fuente.getRutaDocumentoFuente();
            String nombreArchivo = esInforme ? fuente.getNombreDocumentoInforme() : fuente.getNombreDocumentoFuente();

            if (rutaArchivo == null || rutaArchivo.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("El archivo solicitado no está disponible para esta fuente.");
            }

            Resource recurso = fileService.getFileResource(rutaArchivo);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nombreArchivo + "\"")
                    .body(recurso);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ocurrió un error al procesar la solicitud. Error: " + e.getMessage());
        }
    }

    @Override
    public void guardarFuente(Actividad actividad) {
        EstadoFuente estadoFuente = estadoFuenteRepository.findByNombreEstado("PENDIENTE")
                .orElseThrow(() -> new IllegalArgumentException("Estado de fuente no válido."));

        crearYGuardarFuente(actividad, "1", estadoFuente);
        crearYGuardarFuente(actividad, "2", estadoFuente);
    }

    /**
     * Método auxiliar para crear y guardar una fuente.
     *
     * @param actividad    La actividad asociada.
     * @param tipoFuente   El tipo de la fuente (1 o 2).
     * @param estadoFuente El estado de la fuente.
     */
    private void crearYGuardarFuente(Actividad actividad, String tipoFuente, EstadoFuente estadoFuente) {
        Fuente fuente = new Fuente();
        fuente.setActividad(actividad);
        fuente.setTipoFuente(tipoFuente);
        fuente.setEstadoFuente(estadoFuente);
        fuente.setCalificacion(null);
        fuenteRepository.save(fuente);
    }
}
