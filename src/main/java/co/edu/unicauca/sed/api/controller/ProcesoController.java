package co.edu.unicauca.sed.api.controller;

import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import co.edu.unicauca.sed.api.domain.Proceso;
import co.edu.unicauca.sed.api.dto.ApiResponse;
import co.edu.unicauca.sed.api.service.ProcesoService;

@Controller
@RequestMapping("api/proceso")
public class ProcesoController {
    @Autowired
    private ProcesoService procesoService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<Proceso>>> findAll(
            @RequestParam(required = false) Integer idEvaluador,
            @RequestParam(required = false) Integer idEvaluado,
            @RequestParam(required = false) Integer idPeriodo,
            @RequestParam(required = false) String nombreProceso,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaCreacion,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaActualizacion,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        ApiResponse<Page<Proceso>> response = procesoService.findAll(
                idEvaluador, idEvaluado, idPeriodo, nombreProceso, fechaCreacion, fechaActualizacion, PageRequest.of(page, size));
        
        return ResponseEntity.status(response.getCodigo()).body(response);
    }    

    @GetMapping("/{oid}")
    public ResponseEntity<ApiResponse<Proceso>> findById(@PathVariable Integer oid) {
        ApiResponse<Proceso> response = procesoService.findByOid(oid);
        return ResponseEntity.status(response.getCodigo()).body(response);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Proceso>> save(@RequestBody Proceso proceso) {
        ApiResponse<Proceso> response = procesoService.save(proceso);
        return ResponseEntity.status(response.getCodigo()).body(response);
    }

    @PutMapping("/{oid}")
    public ResponseEntity<ApiResponse<Proceso>> update(@PathVariable Integer oid, @RequestBody Proceso proceso) {
        ApiResponse<Proceso> response = procesoService.update(oid, proceso);
        return ResponseEntity.status(response.getCodigo()).body(response);
    }

    @DeleteMapping("/{oid}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Integer oid) {
        ApiResponse<Void> response = procesoService.delete(oid);
        return ResponseEntity.status(response.getCodigo()).body(response);
    }
}
