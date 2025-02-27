package co.edu.unicauca.sed.api.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
                procesarRoles(usuario);
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

    private void procesarRoles(Usuario usuario) {
        List<Rol> rolesAsignados = rolService.processRoles(usuario.getRoles());
        usuario.setRoles(rolesAsignados);
    
        for (Rol rol : rolesAsignados) {
            switch (rol.getNombre().toUpperCase()) {
                case "JEFE DE DEPARTAMENTO":
                    if (usuarioRepository.countByUsuarioDetalle_DepartamentoAndRoles_NombreIn(
                            usuario.getUsuarioDetalle().getDepartamento(), List.of("JEFE DE DEPARTAMENTO")) > 0) {
                        throw new RuntimeException("Ya existe un Jefe de Departamento registrado en este departamento.");
                    }
                    break;
                case "COORDINADOR":
                    if (usuarioRepository.countByUsuarioDetalle_DepartamentoAndRoles_NombreIn(
                            usuario.getUsuarioDetalle().getDepartamento(), List.of("COORDINADOR")) > 0) {
                        throw new RuntimeException("Ya existe un Coordinador registrado en este departamento.");
                    }
                    break;
                case "CPD":
                    if (usuarioRepository.countByUsuarioDetalle_DepartamentoAndRoles_NombreIn(
                            usuario.getUsuarioDetalle().getDepartamento(), List.of("CPD")) > 0) {
                        throw new RuntimeException("Ya existe un CPD registrado en este departamento.");
                    }
                    break;
                case "DECANO":
                    if (usuarioRepository.countByUsuarioDetalle_FacultadAndRoles_NombreIn(
                            usuario.getUsuarioDetalle().getFacultad(), List.of("DECANO")) > 0) {
                        throw new RuntimeException("Ya existe un Decano registrado en esta facultad.");
                    }
                    break;
                case "SECRETARIO/A FACULTAD":
                    if (usuarioRepository.countByUsuarioDetalle_FacultadAndRoles_NombreIn(
                            usuario.getUsuarioDetalle().getFacultad(), List.of("SECRETARIO/A FACULTAD")) > 0) {
                        throw new RuntimeException("Ya existe un Secretario/a de Facultad registrado en esta facultad.");
                    }
                    break;
                default:
                    break;
            }
        }
    }    
}
