package co.edu.unicauca.sed.api.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.edu.unicauca.sed.api.dto.ConsolidadoDTO;
import co.edu.unicauca.sed.api.dto.FuenteDTO;
import co.edu.unicauca.sed.api.model.Actividad;
import co.edu.unicauca.sed.api.model.Consolidado;
import co.edu.unicauca.sed.api.model.Fuente;
import co.edu.unicauca.sed.api.model.PeriodoAcademico;
import co.edu.unicauca.sed.api.model.Proceso;
import co.edu.unicauca.sed.api.model.Usuario;
import co.edu.unicauca.sed.api.model.UsuarioDetalle;
import co.edu.unicauca.sed.api.repository.ConsolidadoRepository;
import co.edu.unicauca.sed.api.repository.ProcesoRepository;
import co.edu.unicauca.sed.api.repository.UsuarioRepository;

import java.util.Map;

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

    public List<Consolidado> findAll() {
        List<Consolidado> list = new ArrayList<>();
        this.consolidadoRepository.findAll().forEach(list::add);
        return list;
    }

    public Consolidado findByOid(Integer oid) {
        Optional<Consolidado> resultado = this.consolidadoRepository.findById(oid);

        if (resultado.isPresent()) {
            return resultado.get();
        }

        return null;
    }

    public Consolidado save(Consolidado consolidado) {
        try {
            return this.consolidadoRepository.save(consolidado);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void delete(Integer oid) {
        this.consolidadoRepository.deleteById(oid);
    }

    /**
     * Genera un consolidado para un usuario evaluado.
     */
    public ConsolidadoDTO generarConsolidado(Integer idEvaluado, Integer idPeriodoAcademico) {
        Usuario evaluado = obtenerEvaluado(idEvaluado);
        if (idPeriodoAcademico == null) {
            idPeriodoAcademico = obtenerPeriodoAcademicoActivo();
        }

        List<Proceso> procesosEvaluados = obtenerProcesosEvaluados(evaluado, idPeriodoAcademico);

        PeriodoAcademico periodoAcademico = obtenerPeriodoAcademico(procesosEvaluados);
        UsuarioDetalle detalleUsuario = evaluado.getUsuarioDetalle();

        List<Actividad> actividades = obtenerActividades(procesosEvaluados);

        float totalHoras = calcularTotalHoras(actividades);

        Map<String, List<Map<String, Object>>> actividadesPorTipo = agruparActividadesPorTipo(actividades, totalHoras);

        float totalPorcentaje = calcularTotalPorcentaje(actividadesPorTipo);
        double totalAcumulado = calcularTotalAcumulado(actividadesPorTipo);

        return construirConsolidado(evaluado, detalleUsuario, periodoAcademico, actividadesPorTipo, totalHoras,
                totalPorcentaje, totalAcumulado);
    }

    /**
     * Obtiene un usuario evaluado por su ID.
     */
    private Usuario obtenerEvaluado(Integer idEvaluado) {
        return usuarioRepository.findById(idEvaluado)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
    }

    /**
     * Obtiene los procesos asociados a un evaluado en un período académico.
     */
    private List<Proceso> obtenerProcesosEvaluados(Usuario evaluado, Integer idPeriodoAcademico) {
        List<Proceso> procesos = procesoRepository.findByEvaluadoAndOidPeriodoAcademico_OidPeriodoAcademico(evaluado,
                idPeriodoAcademico);
        if (procesos.isEmpty()) {
            throw new IllegalArgumentException("No hay procesos asociados con el evaluado y el período académico.");
        }
        return procesos;
    }

    /**
     * Obtiene el período académico desde los procesos evaluados.
     */
    private PeriodoAcademico obtenerPeriodoAcademico(List<Proceso> procesosEvaluados) {
        return procesosEvaluados.get(0).getOidPeriodoAcademico();
    }

    /**
     * Obtiene todas las actividades desde los procesos evaluados.
     */
    private List<Actividad> obtenerActividades(List<Proceso> procesosEvaluados) {
        return procesosEvaluados.stream()
                .flatMap(proceso -> proceso.getActividades().stream())
                .collect(Collectors.toList());
    }

    /**
     * Calcula el total de horas de las actividades.
     */
    private float calcularTotalHoras(List<Actividad> actividades) {
        return actividades.stream()
                .map(Actividad::getHoras)
                .reduce(0f, Float::sum);
    }

    /**
     * Agrupa las actividades por tipo y transforma cada actividad a un mapa de
     * datos.
     */
    private Map<String, List<Map<String, Object>>> agruparActividadesPorTipo(List<Actividad> actividades,
            float totalHoras) {
        return actividades.stream()
                .collect(Collectors.groupingBy(
                        actividad -> actividad.getTipoActividad().getNombre(),
                        Collectors.mapping(actividad -> transformarActividad(actividad, totalHoras),
                                Collectors.toList())));
    }

    /**
     * Transforma una actividad a un mapa de datos con cálculos específicos.
     */
    private Map<String, Object> transformarActividad(Actividad actividad, float horasTotales) {
        float porcentaje = calcularPorcentaje(actividad.getHoras(), horasTotales);
        double promedio = calcularPromedio(actividad.getFuentes());
        double acumulado = calcularAcumulado(promedio, porcentaje);

        return Map.of(
            "oidActividad", actividad.getOidActividad(),
            "codigoActividad", actividad.getCodigoActividad(),
            "nombre", actividad.getNombre(),
            "horas", actividad.getHoras(),
            "fuentes", transformarFuentes(actividad.getFuentes()),
            "porcentaje", porcentaje,
            "promedio", promedio,
            "acumulado", acumulado
        );
    }

    /**
     * Calcula el porcentaje de horas de una actividad respecto al total.
     *
     * @param horasActividad Horas de la actividad.
     * @param horasTotales   Horas totales de todas las actividades.
     * @return Porcentaje calculado redondeado a 2 decimales.
     */
    private float calcularPorcentaje(float horasActividad, float horasTotales) {
        float porcentaje = horasTotales > 0 ? (horasActividad / horasTotales) * 100 : 0;
        return redondearDecimal(porcentaje, 2).floatValue();
    }

    /**
     * Calcula el promedio de las calificaciones de las fuentes.
     *
     * @param fuentes Lista de fuentes asociadas a la actividad.
     * @return Promedio de las calificaciones redondeado a 2 decimales.
     */
    private double calcularPromedio(List<Fuente> fuentes) {
        double promedio = fuentes.stream()
                .mapToDouble(fuente -> Double.valueOf(fuente.getCalificacion()))
                .average()
                .orElse(0);
        return redondearDecimal(promedio, 2).doubleValue();
    }

    /**
     * Calcula el valor acumulado de una actividad.
     *
     * @param promedio   Promedio de las calificaciones de las fuentes.
     * @param porcentaje Porcentaje calculado para la actividad.
     * @return Valor acumulado calculado redondeado a 2 decimales.
     */
    private double calcularAcumulado(double promedio, float porcentaje) {
        double acumulado = promedio * (porcentaje / 100);
        return redondearDecimal(acumulado, 2).doubleValue();
    }

    /**
     * Redondea un número a un número específico de decimales.
     *
     * @param valor   Número a redondear.
     * @param digitos Número de decimales.
     * @return Número redondeado.
     */
    private BigDecimal redondearDecimal(double valor, int digitos) {
        return BigDecimal.valueOf(valor).setScale(digitos, RoundingMode.HALF_UP);
    }

    /**
     * Transforma las fuentes de una actividad a una lista de FuenteDTO.
     *
     * @param fuentes Lista de fuentes asociadas a la actividad.
     * @return Lista de FuenteDTO con los campos necesarios.
     */
    private List<FuenteDTO> transformarFuentes(List<Fuente> fuentes) {
        return fuentes.stream()
                .map(fuente -> new FuenteDTO(
                        fuente.getOidFuente(),
                        fuente.getEstadoFuente() != null ? fuente.getEstadoFuente().getNombreEstado() : null,
                        fuente.getCalificacion()))
                .collect(Collectors.toList());
    }

    /**
     * Calcula el total del porcentaje de las actividades.
     *
     * @param actividadesPorTipo Mapa que agrupa actividades por tipo.
     * @return Total del porcentaje de todas las actividades.
     */
    private float calcularTotalPorcentaje(Map<String, List<Map<String, Object>>> actividadesPorTipo) {
        return (float) actividadesPorTipo.values().stream()
                .flatMap(List::stream)
                .mapToDouble(actividad -> {
                    Object porcentaje = actividad.get("porcentaje");
                    return porcentaje instanceof Number ? ((Number) porcentaje).doubleValue() : 0.0;
                })
                .sum();
    }

    /**
     * Calcula el total acumulado de las actividades.
     */
    private double calcularTotalAcumulado(Map<String, List<Map<String, Object>>> actividadesPorTipo) {
        return actividadesPorTipo.values().stream()
                .flatMap(List::stream)
                .mapToDouble(actividad -> (double) actividad.get("acumulado"))
                .sum();
    }

    /**
     * Construye el DTO del consolidado.
     */
    private ConsolidadoDTO construirConsolidado(Usuario evaluado, UsuarioDetalle detalleUsuario,
            PeriodoAcademico periodoAcademico,
            Map<String, List<Map<String, Object>>> actividadesPorTipo,
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
        consolidado.setTotalHorasSemanales(totalHoras);
        consolidado.setTotalPorcentaje(totalPorcentaje);
        consolidado.setTotalAcumulado(totalAcumulado);
        return consolidado;
    }

    private Integer obtenerPeriodoAcademicoActivo() {
        return periodoAcademicoService.getPeriodoAcademicoActivo()
                .map(PeriodoAcademico::getOidPeriodoAcademico)
                .orElseThrow(() -> new IllegalStateException("No se encontró un período académico activo."));
    }

}
