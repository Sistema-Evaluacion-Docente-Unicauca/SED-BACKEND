package co.edu.unicauca.sed.api.controller;

import co.edu.unicauca.sed.api.dto.AutoevaluacionDTO;
import co.edu.unicauca.sed.api.dto.ApiResponse;
import co.edu.unicauca.sed.api.service.evaluacion_docente.AutoevaluacionService;
import co.edu.unicauca.sed.api.service.fuente.FuenteIntegrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.MediaType;

@RestController
@RequestMapping("/api/autoevaluacion")
@RequiredArgsConstructor
public class AutoevaluacionController {

    private final AutoevaluacionService autoevaluacionService;

    private final FuenteIntegrationService integrationService;

    /**
     * Guarda o actualiza una autoevaluación con sus respectivos documentos
     * adjuntos.
     *
     * @param autoevaluacionJson JSON plano con la información de la autoevaluación.
     * @param firma              Archivo de firma del docente.
     * @param screenshotSimca    Captura de pantalla del sistema SIMCA.
     * @param documentoNotas     Documento con las calificaciones diligenciadas.
     * @param archivosOds        Archivos de evidencia por cada ODS (clave:
     *                           ods-<oidOds>).
     * @return ApiResponse con el resultado de la operación.
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Void>> guardarAutoevaluacion(
            @RequestParam("data") String autoevaluacionJson,
            @RequestParam(value = "firma", required = false) MultipartFile firma,
            @RequestParam(value = "screenshotSimca", required = false) MultipartFile screenshotSimca,
            @RequestParam(value = "documentoAutoevaluacion", required = false) MultipartFile documentoAutoevaluacion,
            @RequestParam Map<String, MultipartFile> allFiles) {

        Map<String, MultipartFile> archivosOds = allFiles.entrySet().stream()
            .filter(entry -> entry.getKey().startsWith("ods-"))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        AutoevaluacionDTO dto = integrationService.convertirJsonAAutoevaluacion(autoevaluacionJson);
        ApiResponse<Void> response = autoevaluacionService.guardarAutoevaluacion(dto, firma, screenshotSimca, documentoAutoevaluacion, archivosOds);
        return ResponseEntity.status(response.getCodigo()).body(response);
    }

    /**
     * Consulta la información de una autoevaluación asociada a una fuente.
     *
     * @param oidFuente ID de la fuente.
     * @return ApiResponse con la información encontrada o error.
     */
    @GetMapping("/{oidFuente}")
    public ResponseEntity<ApiResponse<Object>> buscarPorFuente(@PathVariable Integer oidFuente) {
        ApiResponse<Object> response = autoevaluacionService.buscarPorFuente(oidFuente);
        return ResponseEntity.status(response.getCodigo()).body(response);
    }
}
