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

    public Page<Usuario> findAll(Pageable pageable) {
        return usuarioRepository.findAll(pageable);
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
        // Verificar si UsuarioDetalle ya existe
        if (usuario.getUsuarioDetalle() != null) {
            UsuarioDetalle usuarioDetalleExistente = usuarioDetalleRepository
                    .findByIdentificacion(usuario.getUsuarioDetalle().getIdentificacion());

            if (usuarioDetalleExistente != null) {
                usuario.setUsuarioDetalle(usuarioDetalleExistente);
            } else {
                usuarioDetalleRepository.save(usuario.getUsuarioDetalle());
            }
        }

        // Persistir roles
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

        // Guardar Usuario
        return usuarioRepository.save(usuario);
    }

    @Transactional
    public Usuario update(Integer id, Usuario usuarioActualizado) {
        // Buscar el usuario existente
        Usuario usuarioExistente = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));

        // Actualizar datos básicos
        usuarioExistente.setNombres(usuarioActualizado.getNombres());
        usuarioExistente.setApellidos(usuarioActualizado.getApellidos());
        usuarioExistente.setCorreo(usuarioActualizado.getCorreo());
        usuarioExistente.setEstado(usuarioActualizado.getEstado());

        // Actualizar o asociar UsuarioDetalle
        if (usuarioActualizado.getUsuarioDetalle() != null) {
            if (usuarioActualizado.getUsuarioDetalle().getOidUsuarioDetalle() != null) {
                UsuarioDetalle usuarioDetalleExistente = usuarioDetalleRepository
                        .findById(usuarioActualizado.getUsuarioDetalle().getOidUsuarioDetalle())
                        .orElseThrow(() -> new RuntimeException("UsuarioDetalle no encontrado con OID: "
                                + usuarioActualizado.getUsuarioDetalle().getOidUsuarioDetalle()));
                usuarioExistente.setUsuarioDetalle(usuarioDetalleExistente);
            } else {
                usuarioDetalleRepository.save(usuarioActualizado.getUsuarioDetalle());
                usuarioExistente.setUsuarioDetalle(usuarioActualizado.getUsuarioDetalle());
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