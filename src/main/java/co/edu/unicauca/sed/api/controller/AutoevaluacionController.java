package co.edu.unicauca.sed.api.controller;

import co.edu.unicauca.sed.api.dto.AutoevaluacionDTO;
import co.edu.unicauca.sed.api.dto.ApiResponse;
import co.edu.unicauca.sed.api.service.evaluacion_docente.AutoevaluacionService;
import co.edu.unicauca.sed.api.service.fuente.FuenteIntegrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/autoevaluacion")
@RequiredArgsConstructor
public class AutoevaluacionController {

    private final AutoevaluacionService autoevaluacionService;

    private final FuenteIntegrationService integrationService;

    /**
     * Guarda una autoevaluación con los archivos de firma, screenshot y documento de notas.
     *
     * @param dto            Objeto con los datos de la autoevaluación.
     * @param firma          Archivo con la firma digital.
     * @param screenshotSimca Captura de pantalla del SIMCA.
     * @param documentoNotas Documento con las notas diligenciadas.
     * @return ApiResponse con el resultado de la operación.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> guardarAutoevaluacion(
            @RequestParam("data") String autoevaluacionJson,
            @RequestParam(value = "firma", required = false) MultipartFile firma,
            @RequestParam(value = "screenshotSimca", required = false) MultipartFile screenshotSimca,
            @RequestParam(value = "documentoNotas", required = false) MultipartFile documentoNotas) {
    
        AutoevaluacionDTO dto = integrationService.convertirJsonAAutoevaluacion(autoevaluacionJson);
        ApiResponse<Void> response = autoevaluacionService.guardarAutoevaluacion(dto, firma, screenshotSimca, documentoNotas);
        return ResponseEntity.status(response.getCodigo()).body(response);
    }
    

    /**
     * Consulta la información de una autoevaluación asociada a una fuente.
     *
     * @param oidFuente ID de la fuente.
     * @return ApiResponse con la información encontrada o error.
     */
    @GetMapping("/fuente/{oidFuente}")
    public ResponseEntity<ApiResponse<Object>> buscarPorFuente(@PathVariable Integer oidFuente) {
        ApiResponse<Object> response = autoevaluacionService.buscarPorFuente(oidFuente);
        return ResponseEntity.status(response.getCodigo()).body(response);
    }
}
