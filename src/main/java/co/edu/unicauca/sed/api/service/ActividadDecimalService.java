package co.edu.unicauca.sed.api.service;

import co.edu.unicauca.sed.api.domain.ActividadDecimal;
import co.edu.unicauca.sed.api.dto.ApiResponse;
import co.edu.unicauca.sed.api.repository.ActividadDecimalRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Servicio para gestionar ActividadDecimal con soporte para CRUD y paginación.
 */
@Service
@RequiredArgsConstructor
public class ActividadDecimalService {

    private final ActividadDecimalRepository actividadDecimalRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(ActividadDecimalService.class);

    /**
     * Obtiene una lista paginada de registros en ACTIVIDADDECIMAL.
     */
    public ResponseEntity<ApiResponse<Page<ActividadDecimal>>> obtenerTodos(int page, int size) {
        try {
            Page<ActividadDecimal> actividadDecimals = actividadDecimalRepository.findAll(PageRequest.of(page, size));
            return ResponseEntity.ok(new ApiResponse<>(200, "Registros obtenidos correctamente", actividadDecimals));
        } catch (Exception e) {
            LOGGER.error("❌ Error al obtener registros ACTIVIDADDECIMAL", e);
            return ResponseEntity.status(500).body(new ApiResponse<>(500, "Error al obtener registros", null));
        }
    }

    /**
     * Obtiene un registro por su ID.
     */
    public ResponseEntity<ApiResponse<ActividadDecimal>> obtenerPorId(Integer id) {
        try {
            Optional<ActividadDecimal> actividadDecimal = actividadDecimalRepository.findById(id);
            return actividadDecimal.map(value -> ResponseEntity.ok(new ApiResponse<>(200, "Registro encontrado", value)))
                    .orElseGet(() -> ResponseEntity.status(404).body(new ApiResponse<>(404, "Registro no encontrado", null)));
        } catch (Exception e) {
            LOGGER.error("❌ Error al obtener el registro ACTIVIDADDECIMAL", e);
            return ResponseEntity.status(500).body(new ApiResponse<>(500, "Error al obtener el registro", null));
        }
    }

    /**
     * Crea un nuevo registro en ACTIVIDADDECIMAL.
     */
    public ResponseEntity<ApiResponse<ActividadDecimal>> crear(ActividadDecimal actividadDecimal) {
        try {
            ActividadDecimal nuevoRegistro = actividadDecimalRepository.save(actividadDecimal);
            LOGGER.info("✅ Registro ACTIVIDADDECIMAL creado correctamente: {}", nuevoRegistro);
            return ResponseEntity.ok(new ApiResponse<>(201, "Registro creado exitosamente", nuevoRegistro));
        } catch (Exception e) {
            LOGGER.error("❌ Error al crear el registro ACTIVIDADDECIMAL", e);
            return ResponseEntity.status(500).body(new ApiResponse<>(500, "Error al crear el registro", null));
        }
    }

    /**
     * Actualiza un registro existente.
     */
    public ResponseEntity<ApiResponse<ActividadDecimal>> actualizar(Integer id, ActividadDecimal actividadDecimal) {
        try {
            if (!actividadDecimalRepository.existsById(id)) {
                return ResponseEntity.status(404).body(new ApiResponse<>(404, "Registro no encontrado", null));
            }
            actividadDecimal.setOidActividadDecimal(id);
            ActividadDecimal actualizado = actividadDecimalRepository.save(actividadDecimal);
            LOGGER.info("✅ Registro ACTIVIDADDECIMAL actualizado correctamente: {}", actualizado);
            return ResponseEntity.ok(new ApiResponse<>(200, "Registro actualizado correctamente", actualizado));
        } catch (Exception e) {
            LOGGER.error("❌ Error al actualizar el registro ACTIVIDADDECIMAL", e);
            return ResponseEntity.status(500).body(new ApiResponse<>(500, "Error al actualizar el registro", null));
        }
    }

    /**
     * Elimina un registro por su ID.
     */
    public ResponseEntity<ApiResponse<Void>> eliminar(Integer id) {
        try {
            if (!actividadDecimalRepository.existsById(id)) {
                return ResponseEntity.status(404).body(new ApiResponse<>(404, "Registro no encontrado", null));
            }
            actividadDecimalRepository.deleteById(id);
            LOGGER.info("✅ Registro ACTIVIDADDECIMAL eliminado con ID: {}", id);
            return ResponseEntity.ok(new ApiResponse<>(200, "Registro eliminado correctamente", null));
        } catch (Exception e) {
            LOGGER.error("❌ Error al eliminar el registro ACTIVIDADDECIMAL", e);
            return ResponseEntity.status(500).body(new ApiResponse<>(500, "Error al eliminar el registro", null));
        }
    }
}
