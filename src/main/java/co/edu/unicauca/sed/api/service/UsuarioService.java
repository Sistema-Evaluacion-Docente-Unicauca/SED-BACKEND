package co.edu.unicauca.sed.api.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.unicauca.sed.api.model.Rol;
import co.edu.unicauca.sed.api.model.Usuario;
import co.edu.unicauca.sed.api.repository.RolRepository;
import co.edu.unicauca.sed.api.repository.UsuarioRepository;

@Service
public class UsuarioService {
    @Autowired
    private UsuarioRepository usuarioRepository;

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
                Rol rolExistente = rolRepository.findById(rol.getOid()).orElseThrow(() -> new RuntimeException("Rol no encontrado con OID: " + rol.getOid()));
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
}
