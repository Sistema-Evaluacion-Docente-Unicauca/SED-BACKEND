package co.edu.unicauca.sed.api.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import co.edu.unicauca.sed.api.model.Usuario;
import co.edu.unicauca.sed.api.model.UsuarioDetalle;
import co.edu.unicauca.sed.api.repository.UsuarioDetalleRepository;
import co.edu.unicauca.sed.api.utils.StringUtils;

@Service
public class UsuarioDetalleService {

    @Autowired
    private UsuarioDetalleRepository usuarioDetalleRepository;

    @Autowired
    private StringUtils stringUtils;

    public List<UsuarioDetalle> findAll() {
        List<UsuarioDetalle> list = new ArrayList<>();
        this.usuarioDetalleRepository.findAll().forEach(list::add);
        return list;
    }

    public UsuarioDetalle findByOid(Integer oid) {
        Optional<UsuarioDetalle> resultado = this.usuarioDetalleRepository.findById(oid);

        if (resultado.isPresent()) {
            return resultado.get();
        }

        return null;
    }

    @Transactional
    public UsuarioDetalle save(UsuarioDetalle usuarioDetalle) {
        if (usuarioDetalle.getFacultad() != null) {
            usuarioDetalle.setFacultad(stringUtils.safeToUpperCase(usuarioDetalle.getFacultad()));
        }
        if (usuarioDetalle.getDepartamento() != null) {
            usuarioDetalle.setDepartamento(stringUtils.safeToUpperCase(usuarioDetalle.getDepartamento()));
        }
        if (usuarioDetalle.getCategoria() != null) {
            usuarioDetalle.setCategoria(stringUtils.safeToUpperCase(usuarioDetalle.getCategoria()));
        }
        if (usuarioDetalle.getContratacion() != null) {
            usuarioDetalle.setContratacion(stringUtils.safeToUpperCase(usuarioDetalle.getContratacion()));
        }
        if (usuarioDetalle.getDedicacion() != null) {
            usuarioDetalle.setDedicacion(stringUtils.safeToUpperCase(usuarioDetalle.getDedicacion()));
        }
        if (usuarioDetalle.getEstudios() != null) {
            usuarioDetalle.setEstudios(stringUtils.safeToUpperCase(usuarioDetalle.getEstudios()));
        }
        return usuarioDetalleRepository.save(usuarioDetalle);
    }

    public void delete(Integer oid) {
        this.usuarioDetalleRepository.deleteById(oid);
    }

    public void procesarUsuarioDetalle(Usuario usuario) {
        if (usuario.getUsuarioDetalle() != null) {
            UsuarioDetalle usuarioDetalle = usuario.getUsuarioDetalle();
            usuarioDetalle.setFacultad(stringUtils.safeToUpperCase(usuarioDetalle.getFacultad()));
            usuarioDetalle.setDepartamento(stringUtils.safeToUpperCase(usuarioDetalle.getDepartamento()));
            usuarioDetalle.setCategoria(stringUtils.safeToUpperCase(usuarioDetalle.getCategoria()));
            usuarioDetalle.setContratacion(stringUtils.safeToUpperCase(usuarioDetalle.getContratacion()));
            usuarioDetalle.setDedicacion(stringUtils.safeToUpperCase(usuarioDetalle.getDedicacion()));
            usuarioDetalle.setEstudios(stringUtils.safeToUpperCase(usuarioDetalle.getEstudios()));
    
            // Creamos una nueva variable para evitar la reasignación de usuarioDetalle
            UsuarioDetalle usuarioDetalleProcesado = (usuarioDetalle.getOidUsuarioDetalle() != null)
                    ? usuarioDetalleRepository.findById(usuarioDetalle.getOidUsuarioDetalle())
                            .orElseThrow(() -> new RuntimeException(
                                    "UsuarioDetalle no encontrado con OID: " + usuarioDetalle.getOidUsuarioDetalle())) : usuarioDetalleRepository.save(usuarioDetalle);
            usuario.setUsuarioDetalle(usuarioDetalleProcesado);
        }
    }    
}
