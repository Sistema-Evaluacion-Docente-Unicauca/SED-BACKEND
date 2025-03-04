package co.edu.unicauca.sed.api.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import co.edu.unicauca.sed.api.dto.ApiResponse;
import co.edu.unicauca.sed.api.model.EstadoUsuario;
import co.edu.unicauca.sed.api.model.Rol;
import co.edu.unicauca.sed.api.model.Usuario;
import co.edu.unicauca.sed.api.repository.EstadoUsuarioRepository;
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
    private EstadoUsuarioRepository estadoUsuarioRepository;

    @Autowired
    private RolService rolService;

    @Autowired
    private UsuarioDetalleService usuarioDetalleService;

    /**
     * Encuentra usuarios filtrados y paginados.
     */
    public ApiResponse<Page<Usuario>> findAll(String identificacion, String nombre, String facultad,
            String departamento,
            String categoria, String contratacion, String dedicacion, String estudios,
            String rol, String estado, Pageable pageable) {
        try {
            Page<Usuario> usuarios = usuarioRepository
                .findAll(UsuarioSpecification.byFilters(identificacion, nombre, facultad, departamento,
                        categoria, contratacion, dedicacion, estudios, rol, estado), pageable);
            return new ApiResponse<>(200, "Usuarios encontrados correctamente.", usuarios);
        } catch (Exception e) {
            return new ApiResponse<>(500, "Error al recuperar los usuarios: " + e.getMessage(), null);
        }
    }

    /**
     * Encuentra un usuario por su ID.
     */
    public ApiResponse<Usuario> findByOid(Integer oid) {
        try {
            Usuario usuario = usuarioRepository.findById(oid)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + oid));
            return new ApiResponse<>(200, "Usuario encontrado correctamente.", usuario);
        } catch (RuntimeException e) {
            return new ApiResponse<>(404, "Usuario no encontrado: " + e.getMessage(), null);
        } catch (Exception e) {
            return new ApiResponse<>(500, "Error interno al recuperar el usuario: " + e.getMessage(), null);
        }
    }    

    /**
     * Guarda una lista de usuarios con sus detalles, roles y estado.
     */
    @Transactional
    public ApiResponse<List<Usuario>> save(List<Usuario> usuarios) {
        List<Usuario> usuariosGuardados = new ArrayList<>();

        try {
            for (Usuario usuario : usuarios) {
                validarUsuarioExistente(usuario);
                usuario.setNombres(usuario.getNombres().toUpperCase());
                usuario.setApellidos(usuario.getApellidos().toUpperCase());
                generarUsername(usuario);
                usuarioDetalleService.procesarUsuarioDetalle(usuario);
                procesarEstadoUsuario(usuario);
                List<Rol> rolesAsignados = procesarRoles(usuario, null);
                usuario.setRoles(rolesAsignados);
                usuariosGuardados.add(usuarioRepository.save(usuario));
            }

            return new ApiResponse<>(200, "Usuarios guardados correctamente.", usuariosGuardados);

        } catch (RuntimeException e) {
            return new ApiResponse<>(400, "Error en la validación: " + e.getMessage(), null);
        } catch (Exception e) {
            return new ApiResponse<>(500, "Error interno al guardar usuarios: " + e.getMessage(), null);
        }
    }

    /**
     * Actualiza un usuario existente con nuevos datos.
     */
    @Transactional
    public ApiResponse<Usuario> update(Integer id, Usuario usuarioActualizado) {
        try {
            Usuario usuarioExistente = usuarioRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
    
            if (!Objects.equals(usuarioActualizado.getIdentificacion(), usuarioExistente.getIdentificacion())) {
                validarUsuarioExistente(usuarioActualizado);
            }

            List<Rol> rolesAsignados = procesarRoles(usuarioActualizado, id);
            usuarioExistente.setRoles(rolesAsignados);
    
            usuarioExistente.setNombres(usuarioActualizado.getNombres().toUpperCase());
            usuarioExistente.setApellidos(usuarioActualizado.getApellidos().toUpperCase());
            usuarioExistente.setCorreo(usuarioActualizado.getCorreo());
    
            // Actualizar EstadoUsuario
            if (usuarioActualizado.getEstadoUsuario() != null && usuarioActualizado.getEstadoUsuario().getOidEstadoUsuario() != null) {
                EstadoUsuario estadoUsuario = estadoUsuarioRepository.findById(usuarioActualizado.getEstadoUsuario().getOidEstadoUsuario())
                        .orElseThrow(() -> new RuntimeException("Estado Usuario no encontrado con OID: " + usuarioActualizado.getEstadoUsuario().getOidEstadoUsuario()));
                usuarioExistente.setEstadoUsuario(estadoUsuario);
            }
    
            usuarioDetalleService.procesarUsuarioDetalle(usuarioActualizado);
    
            Usuario usuarioGuardado = usuarioRepository.save(usuarioExistente);
            return new ApiResponse<>(200, "Usuario actualizado correctamente.", usuarioGuardado);
        } catch (RuntimeException e) {
            return new ApiResponse<>(400, "Error en la actualización: " + e.getMessage(), null);
        } catch (Exception e) {
            return new ApiResponse<>(500, "Error interno al actualizar el usuario: " + e.getMessage(), null);
        }
    }

    /**
     * Elimina un usuario por su ID.
     */
    public ApiResponse<Void> delete(Integer oid) {
        try {
            if (!usuarioRepository.existsById(oid)) {
                return new ApiResponse<>(404, "Usuario no encontrado con ID: " + oid, null);
            }
            usuarioRepository.deleteById(oid);
            return new ApiResponse<>(200, "Usuario eliminado correctamente.", null);
        } catch (Exception e) {
            return new ApiResponse<>(500, "Error al eliminar el usuario: " + e.getMessage(), null);
        }
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

    private List<Rol> procesarRoles(Usuario usuario, Integer idUsuario) {
        List<Rol> rolesAsignados = rolService.processRoles(usuario.getRoles());
    
        for (Rol rol : rolesAsignados) {
            switch (rol.getNombre().toUpperCase()) {
                case "JEFE DE DEPARTAMENTO":
                    validarRolUnico(usuario, idUsuario, "JEFE DE DEPARTAMENTO");
                    break;
                case "COORDINADOR":
                    validarRolUnico(usuario, idUsuario, "COORDINADOR");
                    break;
                case "CPD":
                    validarRolUnico(usuario, idUsuario, "CPD");
                    break;
                case "DECANO":
                    validarRolUnico(usuario, idUsuario, "DECANO");
                    break;
                case "SECRETARIA/O FACULTAD":
                    validarRolUnico(usuario, idUsuario, "SECRETARIO/A FACULTAD");
                    break;
                default:
                    break;
            }
        }
    
        return rolesAsignados;
    }
    

    private void validarRolUnico(Usuario usuario, Integer idUsuario, String rolNombre) {
        long count;
    
        if ("JEFE DE DEPARTAMENTO".equals(rolNombre) || "COORDINADOR".equals(rolNombre) || "CPD".equals(rolNombre)) {
            count = usuarioRepository.countByUsuarioDetalle_DepartamentoAndRoles_NombreInExcludingUser(
                    usuario.getUsuarioDetalle().getDepartamento(), List.of(rolNombre), idUsuario);
        } else {
            count = usuarioRepository.countByUsuarioDetalle_FacultadAndRoles_NombreInExcludingUser(
                    usuario.getUsuarioDetalle().getFacultad(), List.of(rolNombre), idUsuario);
        }
    
        if (count > 0) {
            throw new RuntimeException("Ya existe un " + rolNombre + " registrado en " +
                    (esRolDeDepartamento(rolNombre) ? "este departamento." : "esta facultad."));
        }
    }
    
    private boolean esRolDeDepartamento(String rolNombre) {
        return List.of("JEFE DE DEPARTAMENTO", "COORDINADOR", "CPD").contains(rolNombre);
    }
    
}
