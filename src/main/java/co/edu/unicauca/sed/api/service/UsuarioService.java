package co.edu.unicauca.sed.api.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.unicauca.sed.api.dto.DocenteEvaluacionDTO;
import co.edu.unicauca.sed.api.model.Actividad;
import co.edu.unicauca.sed.api.model.Proceso;
import co.edu.unicauca.sed.api.model.Rol;
import co.edu.unicauca.sed.api.model.Usuario;
import co.edu.unicauca.sed.api.repository.ActividadRepository;
import co.edu.unicauca.sed.api.repository.ProcesoRepository;
import co.edu.unicauca.sed.api.repository.RolRepository;
import co.edu.unicauca.sed.api.repository.UsuarioRepository;

@Service
public class UsuarioService {
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PeriodoAcademicoService periodoAcademicoService;

    @Autowired
    private ProcesoRepository procesoRepository;

    @Autowired
    private RolRepository rolRepository;

    public List<Usuario> findAll() {
        List<Usuario> list = new ArrayList<>();
        this.usuarioRepository.findAll().forEach(list::add);
        return list;
    }

    public Usuario findByOid(Integer oid) {
        Optional<Usuario> resultado = this.usuarioRepository.findById(oid);

        if (resultado.isPresent()) {
            return resultado.get();
        }

        return null;
    }

    @Transactional
    public Usuario save(Usuario usuario) {
        List<Rol> rolesPersistidos = new ArrayList<>();

        for (Rol rol : usuario.getRoles()) {
            if (rol.getOid() != null) {
                Rol rolExistente = rolRepository.findById(rol.getOid())
                        .orElseThrow(() -> new RuntimeException("Rol no encontrado con OID: " + rol.getOid()));
                rolesPersistidos.add(rolExistente);
            } else {
                Rol nuevoRol = rolRepository.save(rol);
                rolesPersistidos.add(nuevoRol);
            }
        }

        usuario.setRoles(rolesPersistidos);
        return usuarioRepository.save(usuario);
    }

    public void delete(Integer oid) {
        this.usuarioRepository.deleteById(oid);
    }

    /**
     * Retrieves a list of evaluation data for teachers based on optional filters.
     *
     * @param idEvaluado          Optional ID of the teacher to filter.
     * @param idPeriodoAcademico  Optional ID of the academic period to filter.
     * @return List of DocenteEvaluacionDTO containing teacher evaluation data.
     */
    public List<DocenteEvaluacionDTO> obtenerEvaluacionDocentes(Integer idEvaluado, Integer idPeriodoAcademico) {
        if (idPeriodoAcademico == null) {
            Integer periodoActivo = periodoAcademicoService.obtenerPeriodoAcademicoActivo();
            if (periodoActivo == null) {
                throw new IllegalStateException("No se encontró un período académico activo.");
            }
            idPeriodoAcademico = periodoActivo;
        }
        
        // Crear una variable efectivamente final
        final Integer idPeriodoAcademicoFinal = idPeriodoAcademico;
    
        List<Usuario> evaluados = (idEvaluado != null)
        ? List.of(usuarioRepository.findById(idEvaluado)
            .orElseThrow(() -> new IllegalArgumentException("Evaluado no encontrado.")))
        : procesoRepository.findByOidPeriodoAcademico_OidPeriodoAcademico(idPeriodoAcademico).stream()
            .map(Proceso::getEvaluado)
            .distinct()
            .collect(Collectors.toList());
    
        return evaluados.stream()
            .map(evaluado -> construirEvaluacionDocente(evaluado, idPeriodoAcademicoFinal))
            .collect(Collectors.toList());
    }

    /**
     * Constructs the evaluation data for a specific teacher.
     *
     * @param evaluado            The teacher (Usuario) to process.
     * @param idPeriodoAcademico  The academic period ID for filtering.
     * @return A DocenteEvaluacionDTO containing the teacher's evaluation data.
     */
    private DocenteEvaluacionDTO construirEvaluacionDocente(Usuario evaluado, Integer idPeriodoAcademico) {
        List<Actividad> actividades = procesoRepository
            .findByEvaluado_OidUsuarioAndOidPeriodoAcademico_OidPeriodoAcademico(evaluado.getOidUsuario(), idPeriodoAcademico)
            .stream()
            .flatMap(proceso -> proceso.getActividades().stream())
            .collect(Collectors.toList());
    
        int totalFuentes = actividades.stream().mapToInt(actividad -> actividad.getFuentes().size()).sum();
        int fuentesCompletadas = actividades.stream()
            .flatMap(actividad -> actividad.getFuentes().stream())
            .filter(fuente -> fuente.getEstadoFuente().getNombreEstado().equalsIgnoreCase("Diligenciado"))
            .mapToInt(fuente -> 1)
            .sum();
    
        float porcentajeCompletado = totalFuentes > 0 ? (fuentesCompletadas / (float) totalFuentes) * 100 : 0;
        String estadoConsolidado = porcentajeCompletado == 100 ? "Completo" : "En progreso";
    
        return new DocenteEvaluacionDTO(
            evaluado.getNombres() + " " + evaluado.getApellidos(),
            evaluado.getUsuarioDetalle().getIdentificacion(),
            evaluado.getUsuarioDetalle().getContratacion(),
            Math.round(porcentajeCompletado * 100.0) / 100.0f,
            estadoConsolidado
        );
    }
}