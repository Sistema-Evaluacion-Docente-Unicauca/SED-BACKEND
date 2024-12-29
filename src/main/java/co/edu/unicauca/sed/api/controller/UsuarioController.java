package co.edu.unicauca.sed.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
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

import co.edu.unicauca.sed.api.dto.DocenteEvaluacionDTO;
import co.edu.unicauca.sed.api.model.Usuario;
import co.edu.unicauca.sed.api.service.DocenteEvaluacionService;
import co.edu.unicauca.sed.api.service.UsuarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;

/**
 * Controlador para gestionar las operaciones relacionadas con los usuarios en el sistema.
 * Incluye funcionalidades como la creación, actualización, eliminación y obtención de usuarios,
 * así como la evaluación de docentes.
 */
@Controller
@RequestMapping("usuario")
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
     * @return Lista paginada de usuarios o un mensaje de error si ocurre algún problema.
     */
    @GetMapping("all")
    public ResponseEntity<?> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<Usuario> paginatedList = usuarioService.findAll(PageRequest.of(page, size));
            if (paginatedList.hasContent()) {
                return ResponseEntity.ok().body(paginatedList);
            } else {
                logger.warn("No se encontraron usuarios en la página solicitada.");
                return ResponseEntity.noContent().build();
            }
        } catch (Exception e) {
            logger.error("Error al obtener todos los usuarios con paginación: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Error:" + e.getMessage());
        }
    }


    /**
     * Busca un usuario específico por su ID.
     * 
     * @param oid ID del usuario a buscar.
     * @return Información del usuario encontrado o un mensaje de error si no existe.
     */
    @GetMapping("find/{oid}")
    public ResponseEntity<?> find(@PathVariable Integer oid) {
        try {
            Usuario resultado = usuarioService.findByOid(oid);
            if (resultado != null) {
                return ResponseEntity.ok().body(resultado);
            } else {
                logger.warn("Usuario con ID {} no encontrado.", oid);
            }
        } catch (Exception e) {
            logger.error("Error al buscar usuario con ID {}: {}", oid, e.getMessage(), e);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Guarda un nuevo usuario en el sistema.
     * 
     * @param usuario Objeto que contiene los datos del usuario a guardar.
     * @return Usuario guardado o un mensaje de error si ocurre algún problema.
     */
    @PostMapping("save")
    public ResponseEntity<?> save(@RequestBody Usuario usuario) {
        try {
            Usuario resultado = usuarioService.save(usuario);

            if (resultado != null) {
                return ResponseEntity.ok().body(resultado);
            }
        } catch (Exception e) {
            logger.error("Error al guardar el usuario: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Error:" + e.getMessage());
        }
        logger.warn("El usuario no se pudo guardar. Resultado nulo.");
        return ResponseEntity.internalServerError().body("Error: Resultado nulo");
    }

    /**
     * Actualiza un usuario existente en el sistema.
     * 
     * @param idUsuario ID del usuario a actualizar.
     * @param usuarioActualizado Objeto que contiene los datos actualizados del usuario.
     * @return Usuario actualizado o un mensaje de error si no se encuentra o ocurre un problema.
     */
    @PutMapping("update/{idUsuario}")
    public ResponseEntity<?> update(@PathVariable Integer idUsuario, @RequestBody Usuario usuarioActualizado) {
        try {
            Usuario usuario = usuarioService.update(idUsuario, usuarioActualizado);
            logger.info("Usuario con ID {} actualizado correctamente.", idUsuario);
            return ResponseEntity.ok(usuario);
        } catch (RuntimeException e) {
            logger.warn("No se pudo actualizar el usuario con ID {}: {}", idUsuario, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error al actualizar el usuario con ID {}: {}", idUsuario, e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Error al actualizar el usuario: " + e.getMessage());
        }
    }

    /**
     * Elimina un usuario del sistema por su ID.
     * 
     * @param oid ID del usuario a eliminar.
     * @return Respuesta de éxito o un mensaje de error si el usuario no existe o no puede ser eliminado.
     */
    @DeleteMapping("delete/{oid}")
    public ResponseEntity<?> delete(@PathVariable Integer oid) {
        try {
            Usuario usuario = usuarioService.findByOid(oid);
            if (usuario == null) {
                logger.warn("Usuario con ID {} no encontrado para eliminación.", oid);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
            }

            usuarioService.delete(oid);
            logger.info("Usuario con ID {} eliminado correctamente.", oid);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Error al eliminar el usuario con ID {}: {}", oid, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.CONFLICT).body("No se puede borrar por conflictos con otros datos");
        }
    }

    /**
     * Obtiene las evaluaciones de docentes según los filtros proporcionados.
     * 
     * @param idEvaluado         ID del docente evaluado (opcional).
     * @param idPeriodoAcademico ID del período académico (opcional).
     * @param departamento       Departamento del docente (opcional).
     * @return Lista de evaluaciones de docentes o un mensaje de error.
     */
    @GetMapping("obtenerDocentes")
    public ResponseEntity<?> obtenerEvaluacionDocentes(
            @RequestParam(required = false) Integer idEvaluado,
            @RequestParam(required = false) Integer idPeriodoAcademico,
            @RequestParam(required = false) String departamento) {
        try {
            List<DocenteEvaluacionDTO> evaluaciones = docenteEvaluacionService.obtenerEvaluacionDocentes(
                    idEvaluado, idPeriodoAcademico, departamento);

            if (evaluaciones.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(evaluaciones);
        } catch (IllegalStateException e) {
            logger.warn("Error en los parámetros proporcionados: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error inesperado al obtener evaluaciones de docentes: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Error inesperado: " + e.getMessage());
        }
    }
}
