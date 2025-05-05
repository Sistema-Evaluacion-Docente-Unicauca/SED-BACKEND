package co.edu.unicauca.sed.api.controller;

import co.edu.unicauca.sed.api.domain.LaborDocente;
import co.edu.unicauca.sed.api.dto.ApiResponse;
import co.edu.unicauca.sed.api.dto.LaborDocenteRequestDTO;
import co.edu.unicauca.sed.api.service.LaborDocenteService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;

@RestController
@RequestMapping("/api/labor-docente")
@RequiredArgsConstructor
public class LaborDocenteController {

    private final LaborDocenteService laborDocenteService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<LaborDocente>>> listar(Pageable pageable) {
        return ResponseEntity.ok(laborDocenteService.listarTodos(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<LaborDocente>> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(laborDocenteService.buscarPorId(id));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Void>> guardarLaborDocente(
            @RequestPart("data") LaborDocenteRequestDTO dto,
            @RequestPart("documento") MultipartFile documento) {
    
        dto.setDocumento(documento);
        ApiResponse<Void> respuesta = laborDocenteService.guardar(dto);
        return ResponseEntity.status(respuesta.getCodigo()).body(respuesta);
    }

    @PutMapping(path = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<LaborDocente>> actualizar(
            @PathVariable Integer id,
            @RequestPart("datos") LaborDocenteRequestDTO dto,
            @RequestPart("documento") MultipartFile documento) {
    
        dto.setDocumento(documento);
        ApiResponse<LaborDocente> response = laborDocenteService.actualizar(id, dto);
        return ResponseEntity.status(response.getCodigo()).body(response);
    }    

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Integer id) {
        return ResponseEntity.ok(laborDocenteService.eliminar(id));
    }
}