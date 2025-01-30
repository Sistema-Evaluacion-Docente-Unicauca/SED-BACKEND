package co.edu.unicauca.sed.api.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import co.edu.unicauca.sed.api.model.EstadoUsuario;
import co.edu.unicauca.sed.api.model.Rol;
import co.edu.unicauca.sed.api.model.Usuario;
import co.edu.unicauca.sed.api.model.UsuarioDetalle;
import co.edu.unicauca.sed.api.repository.EstadoUsuarioRepository;
import co.edu.unicauca.sed.api.repository.RolRepository;
import co.edu.unicauca.sed.api.repository.UsuarioDetalleRepository;
import co.edu.unicauca.sed.api.repository.UsuarioRepository;
import co.edu.unicauca.sed.api.specification.UsuarioSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Objects;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private UsuarioDetalleRepository usuarioDetalleRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private EstadoUsuarioRepository estadoUsuarioRepository;

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
            procesarUsuarioDetalle(usuario);
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
        if (usuarioActualizado.getEstadoUsuario() != null
                && usuarioActualizado.getEstadoUsuario().getOidEstadoUsuario() != null) {
            EstadoUsuario estadoUsuario = estadoUsuarioRepository
                    .findById(usuarioActualizado.getEstadoUsuario().getOidEstadoUsuario())
                    .orElseThrow(() -> new RuntimeException("EstadoUsuario no encontrado con OID: "
                            + usuarioActualizado.getEstadoUsuario().getOidEstadoUsuario()));
            usuarioExistente.setEstadoUsuario(estadoUsuario);
        }

        // Actualizar UsuarioDetalle
        if (usuarioActualizado.getUsuarioDetalle() != null) {
            UsuarioDetalle usuarioDetalle = processUsuarioDetalle(usuarioActualizado.getUsuarioDetalle());
            usuarioExistente.setUsuarioDetalle(usuarioDetalle);
        }

        // Actualizar roles
        List<Rol> rolesActualizados = processRoles(usuarioActualizado.getRoles());
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
     * Procesa y persiste un UsuarioDetalle.
     */
    private UsuarioDetalle processUsuarioDetalle(UsuarioDetalle usuarioDetalle) {
        usuarioDetalle.setFacultad(safeToUpperCase(usuarioDetalle.getFacultad()));
        usuarioDetalle.setDepartamento(safeToUpperCase(usuarioDetalle.getDepartamento()));
        usuarioDetalle.setCategoria(safeToUpperCase(usuarioDetalle.getCategoria()));
        usuarioDetalle.setContratacion(safeToUpperCase(usuarioDetalle.getContratacion()));
        usuarioDetalle.setDedicacion(safeToUpperCase(usuarioDetalle.getDedicacion()));
        usuarioDetalle.setEstudios(safeToUpperCase(usuarioDetalle.getEstudios()));

        if (usuarioDetalle.getOidUsuarioDetalle() != null) {
            return usuarioDetalleRepository.findById(usuarioDetalle.getOidUsuarioDetalle())
                    .orElseThrow(() -> new RuntimeException(
                            "UsuarioDetalle no encontrado con OID: " + usuarioDetalle.getOidUsuarioDetalle()));
        } else {
            return usuarioDetalleRepository.save(usuarioDetalle);
        }
    }

    /**
     * Procesa y persiste una lista de roles.
     */
    private List<Rol> processRoles(List<Rol> roles) {
        List<Rol> rolesPersistidos = new ArrayList<>();
        for (Rol rol : roles) {
            if (rol.getOid() != null) {
                Rol rolExistente = rolRepository.findById(rol.getOid())
                        .orElseThrow(() -> new RuntimeException("Rol no encontrado con OID: " + rol.getOid()));
                rolesPersistidos.add(rolExistente);
            } else {
                rolesPersistidos.add(rolRepository.save(rol));
            }
        }
        return rolesPersistidos;
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

    private void procesarUsuarioDetalle(Usuario usuario) {
        if (usuario.getUsuarioDetalle() != null) {
            UsuarioDetalle usuarioDetalle = usuario.getUsuarioDetalle();
            usuarioDetalle = processUsuarioDetalle(usuarioDetalle);
            usuario.setUsuarioDetalle(usuarioDetalle);
        }
    }

    private void procesarEstadoUsuario(Usuario usuario) {
        if (usuario.getEstadoUsuario() != null && usuario.getEstadoUsuario().getOidEstadoUsuario() != null) {
            EstadoUsuario estadoUsuario = estadoUsuarioRepository
                    .findById(usuario.getEstadoUsuario().getOidEstadoUsuario())
                    .orElseThrow(() -> new RuntimeException("EstadoUsuario no encontrado con OID: "
                            + usuario.getEstadoUsuario().getOidEstadoUsuario()));
            usuario.setEstadoUsuario(estadoUsuario);
        }
    }

    private void procesarRoles(Usuario usuario) {
        List<Rol> rolesPersistidos = processRoles(usuario.getRoles());
        usuario.setRoles(rolesPersistidos);
    }

    private String safeToUpperCase(String value) {
        return (value != null && !value.isBlank()) ? value.toUpperCase() : value;
    }
}
