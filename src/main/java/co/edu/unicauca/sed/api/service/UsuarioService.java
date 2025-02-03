package co.edu.unicauca.sed.api.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import co.edu.unicauca.sed.api.model.EstadoUsuario;
import co.edu.unicauca.sed.api.model.Rol;
import co.edu.unicauca.sed.api.model.Usuario;
import co.edu.unicauca.sed.api.repository.EstadoUsuarioRepository;
import co.edu.unicauca.sed.api.repository.UsuarioDetalleRepository;
import co.edu.unicauca.sed.api.repository.UsuarioRepository;
import co.edu.unicauca.sed.api.specification.UsuarioSpecification;
import co.edu.unicauca.sed.api.utils.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Objects;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EstadoUsuarioRepository estadoUsuarioRepository;

    @Autowired
    private RolService rolService;

    @Autowired
    private UsuarioDetalleService usuarioDetalleService;

    /**
     * Encuentra usuarios filtrados y paginados.
     */
    public Page<Usuario> findAll(String identificacion, String nombre, String facultad, String departamento,
            String categoria, String contratacion, String dedicacion, String estudios,
            String rol, String estado, Pageable pageable) {
        return usuarioRepository.findAll(UsuarioSpecification.byFilters(identificacion, nombre, facultad, departamento,
                categoria, contratacion, dedicacion, estudios, rol, estado), pageable);
    }

    /**
     * Encuentra un usuario por su ID.
     */
    public Usuario findByOid(Integer oid) {
        return usuarioRepository.findById(oid).orElse(null);
    }

    /**
     * Guarda una lista de usuarios con sus detalles, roles y estado.
     */
    @Transactional
    public List<Usuario> save(List<Usuario> usuarios) {
        List<Usuario> usuariosGuardados = new ArrayList<>();

        for (Usuario usuario : usuarios) {
            validarUsuarioExistente(usuario);
            usuario.setNombres(usuario.getNombres().toUpperCase());
            usuario.setApellidos(usuario.getApellidos().toUpperCase());
            generarUsername(usuario);
            usuarioDetalleService.procesarUsuarioDetalle(usuario);
            procesarEstadoUsuario(usuario);
            procesarRoles(usuario);
            usuariosGuardados.add(usuarioRepository.save(usuario));
        }
        return usuariosGuardados;
    }

    /**
     * Actualiza un usuario existente con nuevos datos.
     */
    @Transactional
    public Usuario update(Integer id, Usuario usuarioActualizado) {
        Usuario usuarioExistente = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
        if (!Objects.equals(usuarioActualizado.getIdentificacion(), usuarioExistente.getIdentificacion())) {
            validarUsuarioExistente(usuarioActualizado);
        }
        usuarioExistente.setNombres(usuarioActualizado.getNombres().toUpperCase());
        usuarioExistente.setApellidos(usuarioActualizado.getApellidos().toUpperCase());
        usuarioExistente.setCorreo(usuarioActualizado.getCorreo());

        // Actualizar EstadoUsuario
        if (usuarioActualizado.getEstadoUsuario() != null && usuarioActualizado.getEstadoUsuario().getOidEstadoUsuario() != null) {
            EstadoUsuario estadoUsuario = estadoUsuarioRepository.findById(usuarioActualizado.getEstadoUsuario().getOidEstadoUsuario())
                    .orElseThrow(() -> new RuntimeException("EstadoUsuario no encontrado con OID: " + usuarioActualizado.getEstadoUsuario().getOidEstadoUsuario()));
            usuarioExistente.setEstadoUsuario(estadoUsuario);
        }

        usuarioDetalleService.procesarUsuarioDetalle(usuarioActualizado);

        // Actualizar roles
        List<Rol> rolesActualizados = rolService.processRoles(usuarioActualizado.getRoles());
        usuarioExistente.setRoles(rolesActualizados);

        // Guardar cambios
        return usuarioRepository.save(usuarioExistente);
    }

    /**
     * Elimina un usuario por su ID.
     */
    public void delete(Integer oid) {
        usuarioRepository.deleteById(oid);
    }

    /**
     * Configura el campo `username` del usuario basado en su correo.
     *
     * @param usuario El objeto Usuario al que se configurará el `username`.
     */
    private void generarUsername(Usuario usuario) {
        if (usuario.getCorreo() != null && (usuario.getUsername() == null || usuario.getUsername().isEmpty())) {
            String correo = usuario.getCorreo();
            usuario.setUsername(correo.split("@")[0]);
        }
    }

    private void validarUsuarioExistente(Usuario usuario) {
        if (usuarioRepository.findByIdentificacion(usuario.getIdentificacion()) != null) {
            throw new IllegalArgumentException(
                    "Ya existe un usuario con la identificación: " + usuario.getIdentificacion());
        }
    }

    private void procesarEstadoUsuario(Usuario usuario) {
        if (usuario.getEstadoUsuario() != null && usuario.getEstadoUsuario().getOidEstadoUsuario() != null) {
            EstadoUsuario estadoUsuario = estadoUsuarioRepository
                    .findById(usuario.getEstadoUsuario().getOidEstadoUsuario())
                    .orElseThrow(() -> new RuntimeException("Estado Usuario no encontrado con OID: "
                            + usuario.getEstadoUsuario().getOidEstadoUsuario()));
            usuario.setEstadoUsuario(estadoUsuario);
        }
    }

    private void procesarRoles(Usuario usuario) {
        List<Rol> rolesAsignados = rolService.processRoles(usuario.getRoles());
    
        boolean usuarioTieneRolJefeDepartamento = rolesAsignados.stream()
                .anyMatch(rol -> "JEFE DE DEPARTAMENTO".equalsIgnoreCase(rol.getNombre()));
    
        boolean existeOtroJefeDepartamento = usuarioRepository.existsByRolesNombre("JEFE DE DEPARTAMENTO");
    
        if (usuarioTieneRolJefeDepartamento && existeOtroJefeDepartamento) {
            throw new RuntimeException("Ya existe un Jefe de Departamento registrado en el sistema. No se permite más de uno.");
        }
    
        usuario.setRoles(rolesAsignados);
    }    
}
