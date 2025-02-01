package co.edu.unicauca.sed.api.service.fuente;

import java.util.List;
import org.springframework.core.io.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import co.edu.unicauca.sed.api.dto.FuenteCreateDTO;
import co.edu.unicauca.sed.api.model.Actividad;
import co.edu.unicauca.sed.api.model.EstadoFuente;
import co.edu.unicauca.sed.api.model.Fuente;
import co.edu.unicauca.sed.api.repository.EstadoFuenteRepository;
import co.edu.unicauca.sed.api.repository.FuenteRepository;
import co.edu.unicauca.sed.api.service.FileService;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class FuenteService {

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

    private static final Logger logger = LoggerFactory.getLogger(FuenteService.class);

    @Value("${document.upload-dir}")
    private String uploadDir;

    public static final String PREFIJO_INFORME = "informe";
    public static final String PREFIJO_FUENTE = "fuente";
    public static final int ESTADO_DILIGENCIADO = 2;
    public static final int ESTADO_PENDIENTE = 1;

    /**
     * Recupera todas las fuentes desde el repositorio con soporte de paginación.
     *
     * @param pageable Parámetro para definir la paginación (número de página y tamaño de página).
     * @return Página de entidades Fuente.
     */
    public Page<Fuente> findAll(Pageable pageable) {
        return fuenteRepository.findAll(pageable);
    }

    /**
     * Busca una fuente por su identificador único.
     *
     * @param oid El ID de la fuente a buscar.
     * @return La entidad Fuente si se encuentra, null en caso contrario.
     */
    public Fuente findByOid(Integer oid) {
        return fuenteRepository.findById(oid).orElse(null);
    }

    /**
     * Busca todas las fuentes asociadas a una actividad específica.
     *
     * @param oidActividad El ID de la actividad.
     * @return Lista de entidades Fuente vinculadas a la actividad.
     */
    public List<Fuente> findByActividadOid(Integer oidActividad) {
        return fuenteRepository.findByActividadOid(oidActividad);
    }

    /**
     * Guarda una fuente y sube su archivo asociado.
     *
     * @param fuente  La entidad Fuente a guardar.
     * @param archivo El archivo a subir.
     * @return La entidad Fuente guardada.
     */
    public Fuente save(Fuente fuente) {
        Fuente response = fuenteRepository.save(fuente);
        return response;
    }

    /**
     * Elimina una fuente por su identificador único.
     *
     * @param oid El ID de la fuente a eliminar.
     */
    public void delete(Integer oid) {
        fuenteRepository.deleteById(oid);
    }

    /**
     * Guarda múltiples fuentes junto con sus archivos asociados.
     *
     * @param sourcesJson   JSON que contiene los datos de las fuentes.
     * @param informeFuente Archivo común asociado a las fuentes.
     * @param observation   Observación general.
     * @param allFiles      Archivos adicionales para manejar.
     */
    public void saveSource(String sourcesJson, MultipartFile informeFuente, String observation,
            Map<String, MultipartFile> allFiles) {
        try {
            List<FuenteCreateDTO> sources = integrationService.parseSourcesJson(sourcesJson);
            Map<String, MultipartFile> informeEjecutivoFiles = null;
            if (sources.stream().anyMatch(source -> "1".equals(source.getTipoFuente()))) {
                informeEjecutivoFiles = integrationService.filterExecutiveFiles(allFiles);
            } else {
                logger.info("No se encontraron fuentes tipo 1, no se procesarán archivos adicionales");
            }
            for (FuenteCreateDTO sourceDTO : sources) {
                if (sourceDTO.getCalificacion() == null || String.valueOf(sourceDTO.getCalificacion()).isBlank()) {
                    throw new IllegalStateException("Calificación es un campo requerido y no puede estar vacío.");
                }                
                if (observation != null) {
                    observation.toUpperCase();
                }
                businessService.processSource(sourceDTO, informeFuente, observation, informeEjecutivoFiles);
            }
        } catch (Exception e) {
            logger.error("Error al guardar fuentes", e);
            throw new RuntimeException("Error durante la operación de guardar fuentes: " + e.getMessage(), e);
        }
    }

    /**
     * Recupera un archivo asociado a una fuente.
     *
     * @param id       ID de la fuente.
     * @param isReport Indica si se debe recuperar el informe (true) o la fuente
     * @return Respuesta con el archivo como recurso descargable.
     */
    public ResponseEntity<?> getFile(Integer id, boolean isReport) {
        try {
            // Busca la fuente por ID
            Fuente fuente = fuenteRepository.findById(id).orElseThrow(() -> new RuntimeException("Fuente con ID " + id + " no encontrada."));

            // Determina el archivo y la ruta según el flag
            String filePath = isReport ? fuente.getRutaDocumentoInforme() : fuente.getRutaDocumentoFuente();
            String fileName = isReport ? fuente.getNombreDocumentoInforme() : fuente.getNombreDocumentoFuente();

            // Validar que la ruta no sea nula ni vacía
            if (filePath == null || filePath.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("El archivo solicitado no está disponible para esta fuente.");
            }

            // Recupera el recurso utilizando FileService
            Resource resource = fileService.getFileResource(filePath);

            // Retorna el archivo como respuesta
            return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"").body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ocurrió un error al procesar la solicitud. Error: " + e.getMessage());
        }
    }

    public void saveSource(Actividad actividad) {
        EstadoFuente estadoFuente = estadoFuenteRepository.findByNombreEstado("PENDIENTE")
                .orElseThrow(() -> new IllegalArgumentException("Estado de fuente no válido."));
    
        // Crear y guardar las fuentes
        createAndSaveFuente(actividad, "1", estadoFuente);
        createAndSaveFuente(actividad, "2", estadoFuente);
    }

    /**
     * Método auxiliar para crear y guardar una fuente.
     *
     * @param actividad    La actividad asociada.
     * @param tipoFuente   El tipo de la fuente (1 o 2).
     * @param estadoFuente El estado de la fuente.
     */
    private void createAndSaveFuente(Actividad actividad, String tipoFuente, EstadoFuente estadoFuente) {
        Fuente fuente = new Fuente();
        fuente.setActividad(actividad);
        fuente.setTipoFuente(tipoFuente);
        fuente.setEstadoFuente(estadoFuente);
        fuente.setCalificacion(null);
        fuenteRepository.save(fuente);

    }
}
