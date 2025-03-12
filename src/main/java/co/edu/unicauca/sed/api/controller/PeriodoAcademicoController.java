package co.edu.unicauca.sed.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import co.edu.unicauca.sed.api.domain.PeriodoAcademico;
import co.edu.unicauca.sed.api.dto.ApiResponse;
import co.edu.unicauca.sed.api.service.periodo_academico.PeriodoAcademicoService;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;

/**
 * Controlador para la gestión de los períodos académicos.
 * Proporciona endpoints para realizar operaciones CRUD sobre los períodos
 * académicos.
 */
@Controller
@RequestMapping("api/periodos-academicos")
public class PeriodoAcademicoController {

    private static final Logger logger = LoggerFactory.getLogger(PeriodoAcademicoController.class);

    @Autowired
    private PeriodoAcademicoService periodoAcademicoService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<PeriodoAcademico>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        ApiResponse<Page<PeriodoAcademico>> response = periodoAcademicoService.findAll(PageRequest.of(page, size));
        return ResponseEntity.status(response.getCodigo()).body(response);
    }

    @GetMapping("/{oid}")
    public ResponseEntity<ApiResponse<PeriodoAcademico>> find(@PathVariable Integer oid) {
        ApiResponse<PeriodoAcademico> response = periodoAcademicoService.findByOid(oid);
        return ResponseEntity.status(response.getCodigo()).body(response);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PeriodoAcademico>> save(@RequestBody PeriodoAcademico periodoAcademico) {
        ApiResponse<PeriodoAcademico> response = periodoAcademicoService.save(periodoAcademico);
        return ResponseEntity.status(response.getCodigo()).body(response);
    }

    @PutMapping("/{oid}")
    public ResponseEntity<ApiResponse<Void>> update(@PathVariable Integer oid,
            @RequestBody PeriodoAcademico periodoAcademico) {
        ApiResponse<Void> response = periodoAcademicoService.update(oid, periodoAcademico);
        return ResponseEntity.status(response.getCodigo()).body(response);
    }

    @DeleteMapping("/{oid}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Integer oid) {
        ApiResponse<Void> response = periodoAcademicoService.delete(oid);
        return ResponseEntity.status(response.getCodigo()).body(response);
    }

    /**
     * Obtiene el período académico activo.
     *
     * @return El período académico activo si existe, o un mensaje de error si no
     *         hay ninguno activo.
     */
    @GetMapping("/activo")
    public ResponseEntity<ApiResponse<PeriodoAcademico>> obtenerPeriodoAcademicoActivo() {
        ApiResponse<PeriodoAcademico> response = periodoAcademicoService.getPeriodoAcademicoActivo();
        return ResponseEntity.status(response.getCodigo()).body(response);
    }
}
