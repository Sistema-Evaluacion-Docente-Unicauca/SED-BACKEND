package co.edu.unicauca.sed.api.service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.edu.unicauca.sed.api.dto.ConsolidadoDTO;
import co.edu.unicauca.sed.api.dto.FuenteDTO;
import co.edu.unicauca.sed.api.model.*;
import co.edu.unicauca.sed.api.repository.ConsolidadoRepository;
import co.edu.unicauca.sed.api.repository.ProcesoRepository;
import co.edu.unicauca.sed.api.repository.UsuarioRepository;
import co.edu.unicauca.sed.api.utils.MathUtils;

/**
 * Servicio para la generación y manejo de consolidados.
 */
@Service
public class ConsolidadoService {

    @Autowired
    private ConsolidadoRepository consolidadoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ProcesoRepository procesoRepository;

    @Autowired
    private PeriodoAcademicoService periodoAcademicoService;

    @Autowired
    private ActividadCalculoService calculoService;

    @Autowired
    private ActividadTransformacionService transformacionService;

    // Métodos CRUD básicos
    /**
     * Encuentra todos los consolidados disponibles.
     *
     * @return Lista de consolidados.
     */
    public List<Consolidado> findAll() {
        List<Consolidado> list = new ArrayList<>();
        consolidadoRepository.findAll().forEach(list::add);
        return list;
    }

    /**
     * Encuentra un consolidado por su ID.
     *
     * @param oid ID del consolidado.
     * @return Consolidado encontrado o null si no existe.
     */
    public Consolidado findByOid(Integer oid) {
        return consolidadoRepository.findById(oid).orElse(null);
    }

    /**
     * Guarda un consolidado.
     *
     * @param consolidado Consolidado a guardar.
     * @return Consolidado guardado.
     */
    public Consolidado save(Consolidado consolidado) {
        return consolidadoRepository.save(consolidado);
    }

    /**
     * Elimina un consolidado por su ID.
     *
     * @param oid ID del consolidado.
     */
    public void delete(Integer oid) {
        consolidadoRepository.deleteById(oid);
    }

    // Lógica de negocio específica
    /**
     * Genera un listado de consolidado para un usuario evaluado en un período académico.
     *
     * @param idEvaluado         ID del usuario evaluado.
     * @param idPeriodoAcademico ID del período académico.
     * @return Consolidado generado.
     */
    public ConsolidadoDTO generarConsolidado(Integer idEvaluado, Integer idPeriodoAcademico) {
        Usuario evaluado = obtenerEvaluado(idEvaluado);
        if (idPeriodoAcademico == null) {
            idPeriodoAcademico = periodoAcademicoService.obtenerPeriodoAcademicoActivo();
        }
        List<Proceso> procesosEvaluados = obtenerProcesosEvaluados(evaluado, idPeriodoAcademico);
        PeriodoAcademico periodoAcademico = obtenerPeriodoAcademico(procesosEvaluados);
        UsuarioDetalle detalleUsuario = evaluado.getUsuarioDetalle();
        List<Actividad> actividades = obtenerActividades(procesosEvaluados);
        // Cálculo de totales
        float totalHoras = calculoService.calcularTotalHoras(actividades);

        // Agrupación y transformación de actividades
        Map<String, List<Map<String, Object>>> actividadesPorTipo = actividades.stream()
                .sorted(Comparator.comparing(a -> a.getTipoActividad().getNombre())) // Ordenar por tipo de actividad
                .collect(Collectors.groupingBy(
                        actividad -> actividad.getTipoActividad().getNombre(),
                        Collectors.mapping(
                                actividad -> transformacionService.transformarActividad(actividad, totalHoras),
                                Collectors.toList())));
        float totalPorcentaje = (float) actividadesPorTipo.values().stream()
                .flatMap(List::stream)
                .mapToDouble(actividad -> (float) actividad.get("porcentaje"))
                .sum();
        double totalAcumulado = actividadesPorTipo.values().stream()
                .flatMap(List::stream)
                .mapToDouble(actividad -> (double) actividad.get("acumulado"))
                .sum();
        // Construcción del consolidado
        return construirConsolidado(evaluado, detalleUsuario, periodoAcademico, actividadesPorTipo, totalHoras,
                totalPorcentaje, totalAcumulado);
    }

    /**
     * Obtiene un usuario evaluado por su ID.
     */
    private Usuario obtenerEvaluado(Integer idEvaluado) {
        return usuarioRepository.findById(idEvaluado)
                .orElseThrow(() -> new IllegalArgumentException("Usuario con ID " + idEvaluado + " no encontrado."));
    }

    /**
     * Obtiene los procesos evaluados de un usuario en un período académico.
     */
    private List<Proceso> obtenerProcesosEvaluados(Usuario evaluado, Integer idPeriodoAcademico) {
        List<Proceso> procesos = procesoRepository.findByEvaluadoAndOidPeriodoAcademico_OidPeriodoAcademico(evaluado, idPeriodoAcademico);
        if (procesos.isEmpty()) {
            throw new IllegalArgumentException("No hay procesos para el evaluado en el período académico.");
        }
        return procesos;
    }

    /**
     * Obtiene el período académico de los procesos evaluados.
     */
    private PeriodoAcademico obtenerPeriodoAcademico(List<Proceso> procesosEvaluados) {
        return procesosEvaluados.get(0).getOidPeriodoAcademico();
    }

    /**
     * Obtiene todas las actividades de los procesos evaluados.
     */
    private List<Actividad> obtenerActividades(List<Proceso> procesosEvaluados) {
        return procesosEvaluados.stream()
                .flatMap(proceso -> proceso.getActividades().stream())
                .collect(Collectors.toList());
    }

    /**
     * Construye el DTO del consolidado.
     */
    public ConsolidadoDTO construirConsolidado(Usuario evaluado, UsuarioDetalle detalleUsuario,
            PeriodoAcademico periodoAcademico, Map<String, List<Map<String, Object>>> actividadesPorTipo,
            float totalHoras, float totalPorcentaje, double totalAcumulado) {
        ConsolidadoDTO consolidado = new ConsolidadoDTO();

        consolidado.setNombreDocente(evaluado.getNombres() + " " + evaluado.getApellidos());
        consolidado.setNumeroIdentificacion(detalleUsuario.getIdentificacion());
        consolidado.setPeriodoAcademico(periodoAcademico.getIdPeriodo());
        consolidado.setFacultad(detalleUsuario.getFacultad());
        consolidado.setDepartamento(detalleUsuario.getDepartamento());
        consolidado.setCategoria(detalleUsuario.getCategoria());
        consolidado.setTipoContratacion(detalleUsuario.getContratacion());
        consolidado.setDedicacion(detalleUsuario.getDedicacion());
        consolidado.setActividades(actividadesPorTipo);
        consolidado.setTotalHoras(totalHoras);
        consolidado.setTotalPorcentaje(totalPorcentaje);
        consolidado.setTotalAcumulado(totalAcumulado);

        // Calcular `totalFuentes` y `fuentesCompletadas`
        int totalFuentes = actividadesPorTipo.values().stream().flatMap(List::stream).mapToInt(actividad -> (int) actividad.get("totalFuentes")).sum();

        int fuentesCompletadas = actividadesPorTipo.values().stream()
                .flatMap(List::stream) // Stream<Map<String, Object>>
                .flatMap(map -> {
                    Object fuentes = map.get("fuentes"); // Obtener el valor asociado a "fuentes"
                    if (fuentes instanceof List<?>) { // Validar que sea una lista
                        @SuppressWarnings("unchecked")
                        List<FuenteDTO> fuentesList = (List<FuenteDTO>) fuentes; // Cambiar a FuenteDTO
                        return fuentesList.stream(); // Stream<FuenteDTO>
                    }
                    return Stream.empty(); // Si no es una lista, devuelve un stream vacío
                })
                .mapToInt(fuente -> {
                    // Validar y contar las fuentes con estado "Diligenciado"
                    if ("Diligenciado".equalsIgnoreCase(fuente.getEstadoFuente())) {
                        return 1;
                    }
                    return 0;
                })
                .sum();

        // Calcular porcentaje completado
        float porcentajeCompletado = MathUtils.calcularPorcentajeCompletado(totalFuentes, fuentesCompletadas);
        consolidado.setPorcentajeEvaluacionCompletado(porcentajeCompletado);

        return consolidado;
    }
}
