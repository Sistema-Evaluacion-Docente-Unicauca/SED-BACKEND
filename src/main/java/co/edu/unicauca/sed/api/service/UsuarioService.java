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
import org.springframework.data.jpa.domain.Specification;

@Service
public class UsuarioService {
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private UsuarioDetalleRepository usuarioDetalleRepository;

    @Autowired
    private RolRepository rolRepository;

    public Page<Usuario> findAll(String facultad, String departamento, String categoria, String contratacion,
    String dedicacion, String estudios, String rol, Pageable pageable) {
        Specification<Usuario> spec = Specification.where(null);

        if (facultad != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("usuarioDetalle").get("facultad"), facultad));
        }
        if (departamento != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("usuarioDetalle").get("departamento"), departamento));
        }
        if (categoria != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("usuarioDetalle").get("categoria"), categoria));
        }
        if (contratacion != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("usuarioDetalle").get("contratacion"), contratacion));
        }
        if (dedicacion != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("usuarioDetalle").get("dedicacion"), dedicacion));
        }
        if (estudios != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("usuarioDetalle").get("estudios"), estudios));
        }
        if (rol != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.join("roles").get("nombre"), rol));
        }

        return usuarioRepository.findAll(spec, pageable);
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