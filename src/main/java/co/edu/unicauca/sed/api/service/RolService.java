package co.edu.unicauca.sed.api.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.edu.unicauca.sed.api.model.Rol;
import co.edu.unicauca.sed.api.repository.RolRepository;

@Service
public class RolService {
    @Autowired
    private RolRepository rolRepository;

    public List<Rol> findAll() {
        List<Rol> list = new ArrayList<>();
        this.rolRepository.findAll().forEach(list::add);
        return list;
    }

    public Rol findByOid(Integer oid) {
        Optional<Rol> resultado = this.rolRepository.findById(oid);

        if (resultado.isPresent()) {
            return resultado.get();
        }

        return null;
    }

    public Rol save(Rol rol) {
        Rol result = null;
        try {
            result = this.rolRepository.save(rol);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return result;
    }

    public void delete(Integer oid) {
        this.rolRepository.deleteById(oid);
    }
}
