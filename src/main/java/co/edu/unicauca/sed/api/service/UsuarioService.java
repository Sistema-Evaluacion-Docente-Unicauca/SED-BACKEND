package co.edu.unicauca.sed.api.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import co.edu.unicauca.sed.api.model.Rol;
import co.edu.unicauca.sed.api.model.Usuario;
import co.edu.unicauca.sed.api.model.UsuarioDetalle;
import co.edu.unicauca.sed.api.repository.RolRepository;
import co.edu.unicauca.sed.api.repository.UsuarioRepository;
import co.edu.unicauca.sed.api.specification.UsuarioSpecification;
import co.edu.unicauca.sed.api.repository.UsuarioDetalleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
public class UsuarioService {
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private UsuarioDetalleRepository usuarioDetalleRepository;

    @Autowired
    private RolRepository rolRepository;

    public Page<Usuario> findAll(String identificacion, String nombre, String facultad, String departamento, String categoria, String contratacion, String dedicacion, String estudios, String rol, Short estado, Pageable pageable) {
        return usuarioRepository.findAll(UsuarioSpecification.byFilters(identificacion, nombre, facultad, departamento, categoria, contratacion, dedicacion, estudios, rol, estado), pageable);
    }

    public Usuario findByOid(Integer oid) {
        Optional<Usuario> resultado = this.usuarioRepository.findById(oid);

        if (resultado.isPresent()) {
            return resultado.get();
        }

        return null;
    }

    @Transactional
    public List<Usuario> save(List<Usuario> usuarios) {
        List<Usuario> usuariosGuardados = new ArrayList<>();

        for (Usuario usuario : usuarios) {
            // Convertir nombres y apellidos a mayúsculas
            if (usuario.getNombres() != null) {
                usuario.setNombres(usuario.getNombres().toUpperCase());
            }
            if (usuario.getApellidos() != null) {
                usuario.setApellidos(usuario.getApellidos().toUpperCase());
            }
            // Verificar si UsuarioDetalle ya existe
            // Verificar y convertir los campos de UsuarioDetalle
            if (usuario.getUsuarioDetalle() != null) {
                UsuarioDetalle usuarioDetalle = usuario.getUsuarioDetalle();

                // Convertir los campos de UsuarioDetalle a mayúsculas
                if (usuarioDetalle.getFacultad() != null) {
                    usuarioDetalle.setFacultad(usuarioDetalle.getFacultad().toUpperCase());
                }
                if (usuarioDetalle.getDepartamento() != null) {
                    usuarioDetalle.setDepartamento(usuarioDetalle.getDepartamento().toUpperCase());
                }
                if (usuarioDetalle.getCategoria() != null) {
                    usuarioDetalle.setCategoria(usuarioDetalle.getCategoria().toUpperCase());
                }
                if (usuarioDetalle.getContratacion() != null) {
                    usuarioDetalle.setContratacion(usuarioDetalle.getContratacion().toUpperCase());
                }
                if (usuarioDetalle.getDedicacion() != null) {
                    usuarioDetalle.setDedicacion(usuarioDetalle.getDedicacion().toUpperCase());
                }
                if (usuarioDetalle.getEstudios() != null) {
                    usuarioDetalle.setEstudios(usuarioDetalle.getEstudios().toUpperCase());
                }

                // Verificar si UsuarioDetalle ya existe
                UsuarioDetalle usuarioDetalleExistente = usuarioDetalleRepository.findByIdentificacion(usuarioDetalle.getIdentificacion());

                if (usuarioDetalleExistente != null) {
                    usuario.setUsuarioDetalle(usuarioDetalleExistente);
                } else {
                    usuarioDetalleRepository.save(usuarioDetalle);
                }
            }

            // Persistir roles
            List<Rol> rolesPersistidos = new ArrayList<>();
            for (Rol rol : usuario.getRoles()) {
                if (rol.getOid() != null) {
                    Rol rolExistente = rolRepository.findById(rol.getOid()).orElseThrow(() -> new RuntimeException("Rol no encontrado con OID: " + rol.getOid()));
                    rolesPersistidos.add(rolExistente);
                } else {
                    Rol nuevoRol = rolRepository.save(rol);
                    rolesPersistidos.add(nuevoRol);
                }
            }
            usuario.setRoles(rolesPersistidos);

            // Guardar Usuario
            usuariosGuardados.add(usuarioRepository.save(usuario));
        }
        return usuariosGuardados;
    }

    @Transactional
    public Usuario update(Integer id, Usuario usuarioActualizado) {
        // Buscar el usuario existente
        Usuario usuarioExistente = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));

        // Actualizar datos básicos
        usuarioExistente.setNombres(usuarioActualizado.getNombres().toUpperCase());
        usuarioExistente.setApellidos(usuarioActualizado.getApellidos().toUpperCase());
        usuarioExistente.setCorreo(usuarioActualizado.getCorreo());
        usuarioExistente.setEstado(usuarioActualizado.getEstado());

        // Actualizar o asociar UsuarioDetalle
        if (usuarioActualizado.getUsuarioDetalle() != null) {
            UsuarioDetalle usuarioDetalle = usuarioActualizado.getUsuarioDetalle();
        
            // Convertir los campos a mayúsculas
            if (usuarioDetalle.getFacultad() != null) {
                usuarioDetalle.setFacultad(usuarioDetalle.getFacultad().toUpperCase());
            }
            if (usuarioDetalle.getDepartamento() != null) {
                usuarioDetalle.setDepartamento(usuarioDetalle.getDepartamento().toUpperCase());
            }
            if (usuarioDetalle.getCategoria() != null) {
                usuarioDetalle.setCategoria(usuarioDetalle.getCategoria().toUpperCase());
            }
            if (usuarioDetalle.getContratacion() != null) {
                usuarioDetalle.setContratacion(usuarioDetalle.getContratacion().toUpperCase());
            }
            if (usuarioDetalle.getDedicacion() != null) {
                usuarioDetalle.setDedicacion(usuarioDetalle.getDedicacion().toUpperCase());
            }
            if (usuarioDetalle.getEstudios() != null) {
                usuarioDetalle.setEstudios(usuarioDetalle.getEstudios().toUpperCase());
            }
        
            if (usuarioDetalle.getOidUsuarioDetalle() != null) {
                UsuarioDetalle usuarioDetalleExistente = usuarioDetalleRepository
                        .findById(usuarioDetalle.getOidUsuarioDetalle())
                        .orElseThrow(() -> new RuntimeException("UsuarioDetalle no encontrado con OID: " 
                                + usuarioDetalle.getOidUsuarioDetalle()));
                usuarioExistente.setUsuarioDetalle(usuarioDetalleExistente);
            } else {
                usuarioDetalleRepository.save(usuarioDetalle);
                usuarioExistente.setUsuarioDetalle(usuarioDetalle);
            }
        }

        // Actualizar roles
        List<Rol> rolesActualizados = new ArrayList<>();
        for (Rol rol : usuarioActualizado.getRoles()) {
            if (rol.getOid() != null) {
                Rol rolExistente = rolRepository.findById(rol.getOid())
                        .orElseThrow(() -> new RuntimeException("Rol no encontrado con OID: " + rol.getOid()));
                rolesActualizados.add(rolExistente);
            } else {
                Rol nuevoRol = rolRepository.save(rol);
                rolesActualizados.add(nuevoRol);
            }
        }
        usuarioExistente.setRoles(rolesActualizados);

        // Guardar cambios
        return usuarioRepository.save(usuarioExistente);
    }

    public void delete(Integer oid) {
        this.usuarioRepository.deleteById(oid);
    }
}