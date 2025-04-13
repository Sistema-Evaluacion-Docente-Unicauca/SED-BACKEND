package co.edu.unicauca.sed.api.service.consolidado;

import co.edu.unicauca.sed.api.domain.*;
import co.edu.unicauca.sed.api.dto.BaseConsolidadoDataDTO;
import co.edu.unicauca.sed.api.dto.ConsolidadoDTO;
import co.edu.unicauca.sed.api.dto.InformacionConsolidadoDTO;
import co.edu.unicauca.sed.api.repository.ConsolidadoRepository;
import co.edu.unicauca.sed.api.repository.ProcesoRepository;
import co.edu.unicauca.sed.api.repository.UsuarioRepository;
import co.edu.unicauca.sed.api.service.actividad.ActividadCalculoService;
import co.edu.unicauca.sed.api.service.actividad.ActividadQueryService;
import co.edu.unicauca.sed.api.service.actividad.ActividadTransformacionService;
import co.edu.unicauca.sed.api.service.periodo_academico.PeriodoAcademicoService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ConsolidadoHelper {

    private final ActividadCalculoService calculoService;
    private final ActividadTransformacionService transformacionService;
    private final UsuarioRepository usuarioRepository;
    private final ProcesoRepository procesoRepository;
    private final PeriodoAcademicoService periodoAcademicoService;
    private final ActividadQueryService actividadQueryService;
    private final ConsolidadoRepository consolidadoRepository;

    private static final Logger logger = LoggerFactory.getLogger(ConsolidadoServiceImpl.class);

    /**
     * Obtiene los datos base del consolidado sin actividades.
     */
    public BaseConsolidadoDataDTO obtenerBaseConsolidado(Integer idEvaluado, Integer idPeriodoAcademico) {
        try {
            Usuario evaluado = usuarioRepository.findById(idEvaluado)
                    .orElseThrow(() -> new EntityNotFoundException("Usuario con ID " + idEvaluado + " no encontrado."));

            idPeriodoAcademico = (idPeriodoAcademico != null) ? idPeriodoAcademico : periodoAcademicoService.obtenerIdPeriodoAcademicoActivo();

            List<Proceso> procesos = procesoRepository.findByEvaluadoAndOidPeriodoAcademico_OidPeriodoAcademico(evaluado, idPeriodoAcademico);

            if (procesos.isEmpty()) {
                throw new EntityNotFoundException("No hay procesos para el evaluado en el período académico.");
            }

            return new BaseConsolidadoDataDTO(
                evaluado,
                evaluado.getUsuarioDetalle(),
                procesos.get(0).getOidPeriodoAcademico(),
                procesos);
        } catch (EntityNotFoundException e) {
            logger.warn("⚠️ [ERROR] {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("❌ [ERROR] Error en obtenerBaseConsolidado: {}", e.getMessage(), e);
            throw new RuntimeException("Error al obtener los datos base del consolidado.", e);
        }
    }

    public ConsolidadoDTO generarConsolidadoConActividades(Integer idEvaluado, Integer idPeriodoAcademico,
            Pageable pageable) {
        BaseConsolidadoDataDTO baseData = obtenerBaseConsolidado(idEvaluado, idPeriodoAcademico);
        Page<Actividad> actividadPage = actividadQueryService.obtenerActividadesPorProcesosPaginadas(baseData.getProcesos(), pageable);
        return construirConsolidadoDesdeActividades(baseData, actividadPage);
    }

    /**
     * Aprobar un consolidado y generar el documento.
     */
    public Integer guardarConsolidado(Consolidado consolidadoExistente, String nombreDocumento, String rutaDocumento, String nota, Double totalAcumulado) {
        actualizarDatosConsolidado(consolidadoExistente, nombreDocumento, rutaDocumento, nota, totalAcumulado);
        return consolidadoExistente.getOidConsolidado();
    }

    public void actualizarDatosConsolidado(Consolidado consolidado, String nombreDocumento, String rutaDocumento, String nota, Double totalAcumulado) {
        consolidado.setNombredocumento(nombreDocumento);
        consolidado.setRutaDocumento(rutaDocumento);
        consolidado.setNota(nota.toUpperCase());
        consolidado.setCalificacion(totalAcumulado);
        consolidado.setFechaActualizacion(LocalDateTime.now());
        consolidadoRepository.save(consolidado);
    }

    public String generarNombreDocumento(ConsolidadoDTO consolidadoDTO) {
        return "Consolidado-" + consolidadoDTO.getPeriodoAcademico() + "-"
                + consolidadoDTO.getNombreDocente().replace(" ", "_");
    }

    public ConsolidadoDTO construirConsolidadoDesdeActividades(BaseConsolidadoDataDTO baseData,
            Page<Actividad> actividadPage) {
        List<Actividad> actividades = actividadPage.getContent();
        float totalHoras = calculoService.calcularTotalHoras(actividades);

        Map<String, List<Map<String, Object>>> actividadesPorTipo = transformacionService
                .agruparActividadesPorTipo(actividades, totalHoras);
        double totalPorcentaje = calculoService.calcularTotalPorcentaje(actividadesPorTipo);
        double totalAcumulado = calculoService.calcularTotalAcumulado(actividadesPorTipo);

        ConsolidadoDTO consolidado = construirConsolidado(baseData.getEvaluado(), baseData.getDetalleUsuario(),
                baseData.getPeriodoAcademico(),
                actividadesPorTipo, totalHoras, totalPorcentaje, totalAcumulado);

        consolidado.setCurrentPage(actividadPage.getNumber());
        consolidado.setPageSize(actividadPage.getSize());
        consolidado.setTotalItems((int) actividadPage.getTotalElements());
        consolidado.setTotalPages(actividadPage.getTotalPages());

        return consolidado;
    }

    public ConsolidadoDTO construirConsolidado(
            Usuario evaluado, UsuarioDetalle detalleUsuario, PeriodoAcademico periodoAcademico,
            Map<String, List<Map<String, Object>>> actividadesPorTipo, float totalHoras,
            double totalPorcentaje, double totalAcumulado) {

        ConsolidadoDTO consolidado = new ConsolidadoDTO();
        consolidado.setNombreDocente(evaluado.getNombres() + " " + evaluado.getApellidos());
        consolidado.setCorreoElectronico(evaluado.getCorreo());
        consolidado.setNumeroIdentificacion(evaluado.getIdentificacion());
        consolidado.setPeriodoAcademico(periodoAcademico.getIdPeriodo());
        consolidado.setFacultad(detalleUsuario.getFacultad());
        consolidado.setDepartamento(detalleUsuario.getDepartamento());
        consolidado.setCategoria(detalleUsuario.getCategoria());
        consolidado.setTipoContratacion(detalleUsuario.getContratacion());
        consolidado.setDedicacion(detalleUsuario.getDedicacion());
        consolidado.setActividades(actividadesPorTipo);
        consolidado.setHorasTotales(totalHoras);
        consolidado.setTotalPorcentaje(totalPorcentaje);
        consolidado.setTotalAcumulado(totalAcumulado);

        return consolidado;
    }

    public InformacionConsolidadoDTO convertirAInformacionDTO(Consolidado consolidado) {
        InformacionConsolidadoDTO dto = new InformacionConsolidadoDTO();

        Usuario evaluado = consolidado.getProceso().getEvaluado();
        UsuarioDetalle detalle = evaluado.getUsuarioDetalle();

        dto.setOidUsuario(evaluado.getOidUsuario());
        dto.setNombreDocente(evaluado.getNombres() + " " + evaluado.getApellidos());
        dto.setNumeroIdentificacion(evaluado.getIdentificacion());
        dto.setFacultad(detalle.getFacultad());
        dto.setDepartamento(detalle.getDepartamento());
        dto.setCategoria(detalle.getCategoria());
        dto.setTipoContratacion(detalle.getContratacion());
        dto.setDedicacion(detalle.getDedicacion());
        dto.setCalificacion(consolidado.getCalificacion());
        dto.setNombreArchivo(consolidado.getNombredocumento());
        dto.setRutaArchivo(consolidado.getRutaDocumento());

        return dto;
    }
}
