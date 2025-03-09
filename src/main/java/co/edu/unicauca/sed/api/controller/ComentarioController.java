package co.edu.unicauca.sed.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import co.edu.unicauca.sed.api.model.Comentario;
import co.edu.unicauca.sed.api.dto.ApiResponse;
import co.edu.unicauca.sed.api.service.ComentarioService;

/**
 * Controlador para gestionar las operaciones relacionadas con los comentarios.
 */
@RestController
@RequestMapping("api/comentario")
public class ComentarioController {

    @Autowired
    private ComentarioService comentarioService;

    /**
     * Obtiene todos los comentarios con paginación.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<Comentario>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "true") Boolean ascendingOrder) {

        ApiResponse<Page<Comentario>> response = comentarioService.findAll(PageRequest.of(page, size), ascendingOrder);
        return ResponseEntity.status(response.getCodigo()).body(response);
    }

    /**
     * Busca un comentario por su ID.
     */
    @GetMapping("/{oid}")
    public ResponseEntity<ApiResponse<Comentario>> find(@PathVariable Integer oid) {
        ApiResponse<Comentario> response = comentarioService.findByOid(oid);
        return ResponseEntity.status(response.getCodigo()).body(response);
    }

    /**
     * Guarda un nuevo comentario.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Comentario>> save(@RequestBody Comentario comentario) {
        ApiResponse<Comentario> response = comentarioService.save(comentario);
        return ResponseEntity.status(response.getCodigo()).body(response);
    }

    /**
     * Elimina un comentario por su ID.
     */
    @DeleteMapping("/{oid}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Integer oid) {
        ApiResponse<Void> response = comentarioService.delete(oid);
        return ResponseEntity.status(response.getCodigo()).body(response);
    }
}
