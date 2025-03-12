package co.edu.unicauca.sed.api.service.consolidado;

import co.edu.unicauca.sed.api.domain.EstadoConsolidado;
import co.edu.unicauca.sed.api.dto.ApiResponse;
import co.edu.unicauca.sed.api.repository.EstadoConsolidadoRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Servicio para gestionar EstadoConsolidado con soporte para CRUD y paginación.
 */
@Service
@RequiredArgsConstructor
public class EstadoConsolidadoService {

    private final EstadoConsolidadoRepository estadoConsolidadoRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(EstadoConsolidadoService.class);

    public ResponseEntity<ApiResponse<Page<EstadoConsolidado>>> obtenerTodos(int page, int size) {
        try {
            Page<EstadoConsolidado> estados = estadoConsolidadoRepository.findAll(PageRequest.of(page, size));
            return ResponseEntity.ok(new ApiResponse<>(200, "Registros obtenidos correctamente", estados));
        } catch (Exception e) {
            LOGGER.error("❌ Error al obtener registros ESTADOCONSOLIDADO", e);
            return ResponseEntity.status(500).body(new ApiResponse<>(500, "Error al obtener registros", null));
        }
    }

    public ResponseEntity<ApiResponse<EstadoConsolidado>> buscarPorId(Integer id) {
        try {
            Optional<EstadoConsolidado> estado = estadoConsolidadoRepository.findById(id);
            return estado.map(value -> ResponseEntity.ok(new ApiResponse<>(200, "Registro encontrado", value)))
                    .orElseGet(() -> ResponseEntity.status(404)
                            .body(new ApiResponse<>(404, "Registro no encontrado", null)));
        } catch (Exception e) {
            LOGGER.error("❌ Error al obtener el registro ESTADOCONSOLIDADO", e);
            return ResponseEntity.status(500).body(new ApiResponse<>(500, "Error al obtener el registro", null));
        }
    }

    public ResponseEntity<ApiResponse<EstadoConsolidado>> crear(EstadoConsolidado estado) {
        try {
            EstadoConsolidado nuevoEstado = estadoConsolidadoRepository.save(estado);
            LOGGER.info("✅ Registro ESTADOCONSOLIDADO creado correctamente: {}", nuevoEstado);
            return ResponseEntity.ok(new ApiResponse<>(201, "Registro creado exitosamente", nuevoEstado));
        } catch (Exception e) {
            LOGGER.error("❌ Error al crear el registro ESTADOCONSOLIDADO", e);
            return ResponseEntity.status(500).body(new ApiResponse<>(500, "Error al crear el registro", null));
        }
    }

    public ResponseEntity<ApiResponse<Void>> eliminar(Integer id) {
        try {
            if (!estadoConsolidadoRepository.existsById(id)) {
                return ResponseEntity.status(404).body(new ApiResponse<>(404, "Registro no encontrado", null));
            }
            estadoConsolidadoRepository.deleteById(id);
            LOGGER.info("✅ Registro ESTADOCONSOLIDADO eliminado con ID: {}", id);
            return ResponseEntity.ok(new ApiResponse<>(200, "Registro eliminado correctamente", null));
        } catch (Exception e) {
            LOGGER.error("❌ Error al eliminar el registro ESTADOCONSOLIDADO", e);
            return ResponseEntity.status(500).body(new ApiResponse<>(500, "Error al eliminar el registro", null));
        }
    }
}
