package co.edu.unicauca.sed.api.service;

import co.edu.unicauca.sed.api.dto.ApiResponse;
import co.edu.unicauca.sed.api.model.ActividadBoolean;
import co.edu.unicauca.sed.api.repository.ActividadBooleanRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Servicio para gestionar ActividadBoolean con soporte para CRUD y paginación.
 */
@Service
@RequiredArgsConstructor
public class ActividadBooleanService {

    private final ActividadBooleanRepository actividadBooleanRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(ActividadBooleanService.class);

    /**
     * Obtiene una lista paginada de registros en ACTIVIDADBOOLEAN.
     */
    public ResponseEntity<ApiResponse<Page<ActividadBoolean>>> obtenerTodos(int page, int size) {
        try {
            Page<ActividadBoolean> actividadBooleans = actividadBooleanRepository.findAll(PageRequest.of(page, size));
            return ResponseEntity.ok(new ApiResponse<>(200, "Registros obtenidos correctamente", actividadBooleans));
        } catch (Exception e) {
            LOGGER.error("❌ Error al obtener registros ACTIVIDADBOOLEAN", e);
            return ResponseEntity.status(500).body(new ApiResponse<>(500, "Error al obtener registros", null));
        }
    }

    /**
     * Obtiene un registro por su ID.
     */
    public ResponseEntity<ApiResponse<ActividadBoolean>> obtenerPorId(Integer id) {
        try {
            Optional<ActividadBoolean> actividadBoolean = actividadBooleanRepository.findById(id);
            return actividadBoolean.map(value -> ResponseEntity.ok(new ApiResponse<>(200, "Registro encontrado", value)))
                    .orElseGet(() -> ResponseEntity.status(404).body(new ApiResponse<>(404, "Registro no encontrado", null)));
        } catch (Exception e) {
            LOGGER.error("❌ Error al obtener el registro ACTIVIDADBOOLEAN", e);
            return ResponseEntity.status(500).body(new ApiResponse<>(500, "Error al obtener el registro", null));
        }
    }

    /**
     * Crea un nuevo registro en ACTIVIDADBOOLEAN.
     */
    public ResponseEntity<ApiResponse<ActividadBoolean>> crear(ActividadBoolean actividadBoolean) {
        try {
            ActividadBoolean nuevoRegistro = actividadBooleanRepository.save(actividadBoolean);
            LOGGER.info("✅ Registro ACTIVIDADBOOLEAN creado correctamente: {}", nuevoRegistro);
            return ResponseEntity.ok(new ApiResponse<>(201, "Registro creado exitosamente", nuevoRegistro));
        } catch (Exception e) {
            LOGGER.error("❌ Error al crear el registro ACTIVIDADBOOLEAN", e);
            return ResponseEntity.status(500).body(new ApiResponse<>(500, "Error al crear el registro", null));
        }
    }

    /**
     * Actualiza un registro existente.
     */
    public ResponseEntity<ApiResponse<ActividadBoolean>> actualizar(Integer id, ActividadBoolean actividadBoolean) {
        try {
            if (!actividadBooleanRepository.existsById(id)) {
                return ResponseEntity.status(404).body(new ApiResponse<>(404, "Registro no encontrado", null));
            }
            actividadBoolean.setOidActividadBoolean(id);
            ActividadBoolean actualizado = actividadBooleanRepository.save(actividadBoolean);
            LOGGER.info("✅ Registro ACTIVIDADBOOLEAN actualizado correctamente: {}", actualizado);
            return ResponseEntity.ok(new ApiResponse<>(200, "Registro actualizado correctamente", actualizado));
        } catch (Exception e) {
            LOGGER.error("❌ Error al actualizar el registro ACTIVIDADBOOLEAN", e);
            return ResponseEntity.status(500).body(new ApiResponse<>(500, "Error al actualizar el registro", null));
        }
    }

    /**
     * Elimina un registro por su ID.
     */
    public ResponseEntity<ApiResponse<Void>> eliminar(Integer id) {
        try {
            if (!actividadBooleanRepository.existsById(id)) {
                return ResponseEntity.status(404).body(new ApiResponse<>(404, "Registro no encontrado", null));
            }
            actividadBooleanRepository.deleteById(id);
            LOGGER.info("✅ Registro ACTIVIDADBOOLEAN eliminado con ID: {}", id);
            return ResponseEntity.ok(new ApiResponse<>(200, "Registro eliminado correctamente", null));
        } catch (Exception e) {
            LOGGER.error("❌ Error al eliminar el registro ACTIVIDADBOOLEAN", e);
            return ResponseEntity.status(500).body(new ApiResponse<>(500, "Error al eliminar el registro", null));
        }
    }
}
