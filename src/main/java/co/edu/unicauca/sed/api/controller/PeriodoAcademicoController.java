package co.edu.unicauca.sed.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import co.edu.unicauca.sed.api.domain.PeriodoAcademico;
import co.edu.unicauca.sed.api.dto.ApiResponse;
import co.edu.unicauca.sed.api.service.periodo_academico.PeriodoAcademicoService;
import org.springframework.data.domain.Page;

/**
 * Controlador para la gestión de los períodos académicos.
 * Proporciona endpoints para realizar operaciones CRUD sobre los períodos
 * académicos.
 */
@Controller
@RequestMapping("api/periodos-academicos")
public class PeriodoAcademicoController {

    @Autowired
    private PeriodoAcademicoService periodoAcademicoService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<PeriodoAcademico>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        ApiResponse<Page<PeriodoAcademico>> response = periodoAcademicoService.obtenerTodos(PageRequest.of(page, size));
        return ResponseEntity.status(response.getCodigo()).body(response);
    }

    @GetMapping("/{oid}")
    public ResponseEntity<ApiResponse<PeriodoAcademico>> find(@PathVariable Integer oid) {
        ApiResponse<PeriodoAcademico> response = periodoAcademicoService.buscarPorId(oid);
        return ResponseEntity.status(response.getCodigo()).body(response);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PeriodoAcademico>> save(@RequestBody PeriodoAcademico periodoAcademico) {
        ApiResponse<PeriodoAcademico> response = periodoAcademicoService.guardar(periodoAcademico);
        return ResponseEntity.status(response.getCodigo()).body(response);
    }

    @PutMapping("/{oid}")
    public ResponseEntity<ApiResponse<Void>> update(@PathVariable Integer oid,
            @RequestBody PeriodoAcademico periodoAcademico) {
        ApiResponse<Void> response = periodoAcademicoService.actualizar(oid, periodoAcademico);
        return ResponseEntity.status(response.getCodigo()).body(response);
    }

    @DeleteMapping("/{oid}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Integer oid) {
        ApiResponse<Void> response = periodoAcademicoService.eliminar(oid);
        return ResponseEntity.status(response.getCodigo()).body(response);
    }

    @GetMapping("/activo")
    public ResponseEntity<ApiResponse<PeriodoAcademico>> obtenerPeriodoAcademicoActivo() {
        ApiResponse<PeriodoAcademico> response = periodoAcademicoService.obtenerPeriodoAcademicoActivo();
        return ResponseEntity.status(response.getCodigo()).body(response);
    }
}
