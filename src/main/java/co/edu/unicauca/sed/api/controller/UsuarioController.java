package co.edu.unicauca.sed.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;
import co.edu.unicauca.sed.api.dto.ApiResponse;
import co.edu.unicauca.sed.api.dto.DocenteEvaluacionDTO;
import co.edu.unicauca.sed.api.model.Usuario;
import co.edu.unicauca.sed.api.service.DocenteEvaluacionService;
import co.edu.unicauca.sed.api.service.UsuarioService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;

/**
 * Controlador para gestionar las operaciones relacionadas con los usuarios en
 * el sistema.
 * Incluye funcionalidades como la creación, actualización, eliminación y
 * obtención de usuarios,
 * así como la evaluación de docentes.
 */
@Controller
@RequestMapping("api/usuarios")
public class UsuarioController {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioController.class);

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private DocenteEvaluacionService docenteEvaluacionService;

    /**
     * Obtiene todos los usuarios registrados en el sistema con soporte de
     * paginación.
     *
     * @param page Número de página (opcional, por defecto 0).
     * @param size Tamaño de la página (opcional, por defecto 10).
     * @return Lista paginada de usuarios o un mensaje de error si ocurre algún
     *         problema.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<Usuario>>> findAll(
            @RequestParam(required = false) String identificacion,
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String facultad,
            @RequestParam(required = false) String departamento,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) String contratacion,
            @RequestParam(required = false) String dedicacion,
            @RequestParam(required = false) String estudios,
            @RequestParam(required = false) String rol,
            @RequestParam(required = false) String estado,
            Pageable pageable) {
        ApiResponse<Page<Usuario>> response = usuarioService.findAll(identificacion, nombre, facultad, departamento,
                categoria, contratacion, dedicacion, estudios, rol, estado, pageable);
        return ResponseEntity.status(response.getCodigo()).body(response);
    }

    /**
     * Busca un usuario específico por su ID.
     * 
     * @param oid ID del usuario a buscar.
     * @return Información del usuario encontrado o un mensaje de error si no
     *         existe.
     */
    @GetMapping("/{oid}")
    public ResponseEntity<ApiResponse<Usuario>> findByOid(@PathVariable Integer oid) {
        ApiResponse<Usuario> response = usuarioService.findByOid(oid);
        return ResponseEntity.status(response.getCodigo()).body(response);
    }

    /**
     * Guarda uno o varios usuarios en el sistema.
     * 
     * @param usuarios Lista de objetos Usuario a guardar.
     * @return Lista de usuarios guardados o un mensaje de error si ocurre algún problema.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<List<Usuario>>> save(@RequestBody List<Usuario> usuarios) {
        ApiResponse<List<Usuario>> response = usuarioService.save(usuarios);
        return ResponseEntity.status(response.getCodigo()).body(response);
    }

    /**
     * Actualiza un usuario existente en el sistema.
     * 
     * @param idUsuario          ID del usuario a actualizar.
     * @param usuarioActualizado Objeto que contiene los datos actualizados del usuario.
     * @return Usuario actualizado o un mensaje de error si no se encuentra o ocurre un problema.
     */
    @PutMapping("/{idUsuario}")
    public ResponseEntity<ApiResponse<Usuario>> update(@PathVariable Integer idUsuario, @RequestBody Usuario usuarioActualizado) {
        ApiResponse<Usuario> response = usuarioService.update(idUsuario, usuarioActualizado);
        return ResponseEntity.status(response.getCodigo()).body(response);
    }

    /**
     * Elimina un usuario del sistema por su ID.
     * 
     * @param oid ID del usuario a eliminar.
     * @return Respuesta de éxito o un mensaje de error si el usuario no existe o no
     *         puede ser eliminado.
     */
    @DeleteMapping("/{oid}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Integer oid) {
        ApiResponse<Void> response = usuarioService.delete(oid);
        return ResponseEntity.status(response.getCodigo()).body(response);
    }

    /**
     * Obtiene las evaluaciones de docentes según los filtros proporcionados.
     * 
     * @param idEvaluado         ID del docente evaluado (opcional).
     * @param idPeriodoAcademico ID del período académico (opcional).
     * @param departamento       Departamento del docente (opcional).
     * @return Lista de evaluaciones de docentes o un mensaje de error.
     */
    @GetMapping("/obtenerEvaluacionDocente")
    public ResponseEntity<ApiResponse<Page<DocenteEvaluacionDTO>>> obtenerEvaluacionDocentes(
            @RequestParam(required = false) Integer idEvaluado,
            @RequestParam(required = false) Integer idPeriodoAcademico,
            @RequestParam(required = false) String departamento,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            ApiResponse<Page<DocenteEvaluacionDTO>> response = docenteEvaluacionService.obtenerEvaluacionDocentes(idEvaluado,
                    idPeriodoAcademico, departamento, PageRequest.of(page, size));
            return ResponseEntity.status(response.getCodigo()).body(response);
        } catch (IllegalArgumentException e) {
            logger.warn("Error en los parámetros proporcionados: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ApiResponse<>(400, e.getMessage(), null));
        } catch (Exception e) {
            logger.error("Error inesperado al obtener evaluaciones de docentes: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(new ApiResponse<>(500, "Error inesperado: " + e.getMessage(), null));
        }
    }
}
