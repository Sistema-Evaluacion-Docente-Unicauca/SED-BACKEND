package co.edu.unicauca.sed.api.service;

import co.edu.unicauca.sed.api.dto.DocenteEvaluacionDTO;
import co.edu.unicauca.sed.api.mapper.DocenteEvaluacionMapper;
import co.edu.unicauca.sed.api.model.Actividad;
import co.edu.unicauca.sed.api.model.Proceso;
import co.edu.unicauca.sed.api.model.Usuario;
import co.edu.unicauca.sed.api.repository.ProcesoRepository;
import co.edu.unicauca.sed.api.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public Page<DocenteEvaluacionDTO> obtenerEvaluacionDocentes(Integer idEvaluado, Integer idPeriodoAcademico,
            String departamento, Pageable pageable) {
        if (idPeriodoAcademico == null) {
            idPeriodoAcademico = periodoAcademicoService.obtenerPeriodoAcademicoActivo();
        }

        final Integer periodoFinal = idPeriodoAcademico;

        List<Usuario> evaluados = obtenerUsuariosEvaluados(idEvaluado, idPeriodoAcademico);

        if (departamento != null) {
            evaluados = filtrarPorDepartamento(evaluados, departamento);
            if (evaluados.isEmpty() && idEvaluado == null) {
                throw new IllegalArgumentException("No se encontraron docentes para el departamento: " + departamento);
            }
        }

        List<DocenteEvaluacionDTO> evaluacionDTOs = evaluados.stream()
                .map(evaluado -> {
                    List<Actividad> actividades = procesoRepository
                            .findByEvaluado_OidUsuarioAndOidPeriodoAcademico_OidPeriodoAcademico(
                                    evaluado.getOidUsuario(), periodoFinal)
                            .stream()
                            .flatMap(proceso -> proceso.getActividades().stream())
                            .collect(Collectors.toList());

                    return DocenteEvaluacionMapper.toDto(evaluado, actividades);
                })
                .collect(Collectors.toList());

        // Implementar paginación manual
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), evaluacionDTOs.size());
        List<DocenteEvaluacionDTO> paginatedList = evaluacionDTOs.subList(start, end);

        return new PageImpl<>(paginatedList, pageable, evaluacionDTOs.size());
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
