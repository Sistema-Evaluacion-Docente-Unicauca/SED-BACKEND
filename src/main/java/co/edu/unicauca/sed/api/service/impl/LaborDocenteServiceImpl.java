package co.edu.unicauca.sed.api.service.impl;

import co.edu.unicauca.sed.api.domain.LaborDocente;
import co.edu.unicauca.sed.api.domain.PeriodoAcademico;
import co.edu.unicauca.sed.api.domain.Usuario;
import co.edu.unicauca.sed.api.dto.ApiResponse;
import co.edu.unicauca.sed.api.dto.LaborDocenteRequestDTO;
import co.edu.unicauca.sed.api.repository.LaborDocenteRepository;
import co.edu.unicauca.sed.api.service.LaborDocenteService;
import co.edu.unicauca.sed.api.service.periodo_academico.PeriodoAcademicoService;
import co.edu.unicauca.sed.api.service.documento.FileService;
import co.edu.unicauca.sed.api.utils.ArchivoUtils;
import co.edu.unicauca.sed.api.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class LaborDocenteServiceImpl implements LaborDocenteService {

    private final LaborDocenteRepository laborDocenteRepository;
    private final co.edu.unicauca.sed.api.service.usuario.UsuarioService usuarioService;
    private final PeriodoAcademicoService periodoAcademicoService;

    private final FileService fileService;

    @Override
    public ApiResponse<Page<LaborDocente>> listarTodos(Pageable pageable) {
        Page<LaborDocente> pagina = laborDocenteRepository.findAll(pageable);
        return new ApiResponse<>(200, "Consulta realizada correctamente", pagina);
    }

    @Override
    public ApiResponse<LaborDocente> buscarPorId(Integer id) {
        Optional<LaborDocente> laborOpt = laborDocenteRepository.findById(id);
        return laborOpt.map(labor -> new ApiResponse<>(200, "Labor docente encontrada", labor))
                .orElseGet(() -> new ApiResponse<>(404, "Labor docente no encontrada", null));
    }

    @Override
    public ApiResponse<Void> eliminar(Integer id) {
        if (laborDocenteRepository.existsById(id)) {
            laborDocenteRepository.deleteById(id);
            return new ApiResponse<>(200, "Labor docente eliminada correctamente", null);
        } else {
            return new ApiResponse<>(404, "Labor docente no encontrada", null);
        }
    }

    @Override
    public ApiResponse<Void> guardar(LaborDocenteRequestDTO dto) {
        try {
            Usuario usuario = obtenerUsuario(dto.getOidUsuario());
            PeriodoAcademico periodo = obtenerPeriodoAcademico(dto.getOidPeriodoAcademico());
            Path rutaArchivo = guardarArchivo(dto.getDocumento(), periodo, usuario);
            LaborDocente laborDocente = construirLaborDocente(usuario, periodo, rutaArchivo);
            laborDocenteRepository.save(laborDocente);
            return new ApiResponse<>(200, "Labor docente guardada correctamente", null);
        } catch (Exception e) {
            log.error("❌ Error al guardar labor docente", e);
            return new ApiResponse<>(500, "Error al guardar la labor docente", null);
        }
    }

    @Override
    public ApiResponse<LaborDocente> actualizar(Integer id, LaborDocenteRequestDTO dto) {
        try {
            LaborDocente laborDocente = laborDocenteRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Labor docente no encontrada"));

            // Eliminar el archivo anterior si existe
            String rutaAnterior = laborDocente.getRutaDocumento();
            if (rutaAnterior != null && !rutaAnterior.isEmpty()) {
                fileService.eliminarArchivo(rutaAnterior);
            }

            // Obtener usuario y período académico
            Usuario usuario = obtenerUsuario(dto.getOidUsuario());
            PeriodoAcademico periodo = obtenerPeriodoAcademico(dto.getOidPeriodoAcademico());

            // Guardar el nuevo archivo
            Path rutaArchivo = guardarArchivo(dto.getDocumento(), periodo, usuario);

            // Actualizar la entidad LaborDocente
            actualizarCamposLaborDocente(laborDocente, usuario, periodo, rutaArchivo);
            laborDocenteRepository.save(laborDocente);

            return new ApiResponse<>(200, "Labor docente actualizada correctamente", laborDocente);
        } catch (IllegalArgumentException e) {
            log.warn("⚠️ {}", e.getMessage());
            return new ApiResponse<>(404, e.getMessage(), null);
        } catch (Exception e) {
            log.error("❌ Error al actualizar labor docente", e);
            return new ApiResponse<>(500, "Error al actualizar la labor docente", null);
        }
    }

    private Usuario obtenerUsuario(Integer oidUsuario) {
        ApiResponse<Usuario> response = usuarioService.buscarPorId(oidUsuario);
        if (response.getData() == null) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }
        return response.getData();
    }

    private PeriodoAcademico obtenerPeriodoAcademico(Integer oidPeriodoAcademico) {
        if (oidPeriodoAcademico != null) {
            ApiResponse<PeriodoAcademico> response = periodoAcademicoService.buscarPorId(oidPeriodoAcademico);
            if (response.getData() == null) {
                throw new IllegalArgumentException("Período académico no encontrado");
            }
            return response.getData();
        } else {
            ApiResponse<PeriodoAcademico> response = periodoAcademicoService.obtenerPeriodoAcademicoActivo();
            if (response.getData() == null) {
                throw new IllegalArgumentException("No se encontró un período académico activo");
            }
            return response.getData();
        }
    }

    private Path guardarArchivo(MultipartFile documento, PeriodoAcademico periodo, Usuario usuario) throws IOException {
        String prefijo = "LABORDOCENTE-" + System.currentTimeMillis();
        String nombres = StringUtils.formatearCadena(usuario.getNombres());
        String apellidos = StringUtils.formatearCadena(usuario.getApellidos());
        String nombreEvaluado = nombres + "_" + apellidos;
        return fileService.guardarArchivo(
                documento, periodo.getIdPeriodo(), nombreEvaluado, usuario.getUsuarioDetalle().getContratacion(),
                usuario.getUsuarioDetalle().getDepartamento(), null, null, prefijo);
    }

    private LaborDocente construirLaborDocente(Usuario usuario, PeriodoAcademico periodo, Path rutaArchivo) {
        LaborDocente laborDocente = new LaborDocente();
        actualizarCamposLaborDocente(laborDocente, usuario, periodo, rutaArchivo);
        laborDocente.setFechaCreacion(LocalDateTime.now());
        return laborDocente;
    }

    private void actualizarCamposLaborDocente(LaborDocente laborDocente, Usuario usuario, PeriodoAcademico periodo, Path rutaArchivo) {
        laborDocente.setUsuario(usuario);
        laborDocente.setPeriodoAcademico(periodo);
        laborDocente.setRutaDocumento(rutaArchivo.toString());
        laborDocente.setNombreDocumento(ArchivoUtils.extraerNombreArchivo(rutaArchivo.toString()));
        laborDocente.setFechaActualizacion(LocalDateTime.now());
    }
}
