package co.edu.unicauca.sed.api.service;

import co.edu.unicauca.sed.api.dto.DocenteEvaluacionDTO;
import co.edu.unicauca.sed.api.mapper.DocenteEvaluacionMapper;
import co.edu.unicauca.sed.api.model.Actividad;
import co.edu.unicauca.sed.api.model.Proceso;
import co.edu.unicauca.sed.api.model.Usuario;
import co.edu.unicauca.sed.api.repository.ProcesoRepository;
import co.edu.unicauca.sed.api.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DocenteEvaluacionService {

    @Autowired
    private ProcesoRepository procesoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PeriodoAcademicoService periodoAcademicoService;

    /**
     * Obtener evaluaciones de docentes con filtros opcionales.
     *
     * @param idEvaluado         ID del docente (opcional).
     * @param idPeriodoAcademico ID del período académico (opcional).
     * @param departamento       Departamento del docente (opcional).
     * @return Lista de evaluaciones de docentes.
     */
    public List<DocenteEvaluacionDTO> obtenerEvaluacionDocentes(Integer idEvaluado, Integer idPeriodoAcademico,
            String departamento) {
        if (idPeriodoAcademico == null) {
            idPeriodoAcademico = periodoAcademicoService.obtenerPeriodoAcademicoActivo();
        }

        final Integer periodoFinal = idPeriodoAcademico; // Hacerlo efectivamente final

        // Obtener lista de evaluados según los filtros
        List<Usuario> evaluados = obtenerUsuariosEvaluados(idEvaluado, idPeriodoAcademico);

        // Aplicar filtro por departamento si es proporcionado
        if (departamento != null) {
            evaluados = filtrarPorDepartamento(evaluados, departamento);

            // Si no hay evaluados y es el único filtro proporcionado
            if (evaluados.isEmpty() && idEvaluado == null) {
                throw new IllegalArgumentException("No se encontraron docentes para el departamento: " + departamento);
            }
        }

        // Transformar los usuarios en DocenteEvaluacionDTO
        return evaluados.stream()
                .map(evaluado -> {
                    // Obtener actividades asociadas al evaluado
                    List<Actividad> actividades = procesoRepository
                            .findByEvaluado_OidUsuarioAndOidPeriodoAcademico_OidPeriodoAcademico(
                                    evaluado.getOidUsuario(), periodoFinal)
                            .stream()
                            .flatMap(proceso -> proceso.getActividades().stream())
                            .collect(Collectors.toList());

                    // Transformar Usuario y Actividades a DTO
                    return DocenteEvaluacionMapper.toDto(evaluado, actividades);
                })
                .collect(Collectors.toList());
    }

    /**
     * Filtra la lista de evaluados según el departamento proporcionado.
     *
     * @param evaluados    Lista de evaluados.
     * @param departamento Departamento para filtrar.
     * @return Lista de evaluados filtrados por departamento.
     */
    private List<Usuario> filtrarPorDepartamento(List<Usuario> evaluados, String departamento) {
        return evaluados.stream()
                .filter(evaluado -> evaluado.getUsuarioDetalle() != null &&
                        departamento.equalsIgnoreCase(evaluado.getUsuarioDetalle().getDepartamento()))
                .collect(Collectors.toList());
    }

    /**
     * Obtener la lista de usuarios evaluados según los filtros de ID y período
     * académico.
     *
     * @param idEvaluado         ID del docente (opcional).
     * @param idPeriodoAcademico ID del período académico.
     * @return Lista de usuarios evaluados.
     */
    private List<Usuario> obtenerUsuariosEvaluados(Integer idEvaluado, Integer idPeriodoAcademico) {
        if (idEvaluado != null) {
            return List.of(usuarioRepository.findById(idEvaluado)
                    .orElseThrow(() -> new IllegalArgumentException("Evaluado no encontrado.")));
        }

        return procesoRepository.findByOidPeriodoAcademico_OidPeriodoAcademico(idPeriodoAcademico).stream()
                .map(Proceso::getEvaluado)
                .distinct()
                .collect(Collectors.toList());
    }
}
