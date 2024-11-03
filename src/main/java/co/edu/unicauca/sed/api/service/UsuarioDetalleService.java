package co.edu.unicauca.sed.api.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.unicauca.sed.api.model.UsuarioDetalle;
import co.edu.unicauca.sed.api.repository.UsuarioDetalleRepository;

@Service
public class UsuarioDetalleService {

    @Autowired
    private UsuarioDetalleRepository usuarioDetalleRepository;

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
        return usuarioDetalleRepository.save(usuarioDetalle);
    }

    public void delete(Integer oid) {
        this.usuarioDetalleRepository.deleteById(oid);
    }
}
