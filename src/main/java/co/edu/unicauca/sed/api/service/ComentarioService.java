package co.edu.unicauca.sed.api.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.edu.unicauca.sed.api.model.Comentario;
import co.edu.unicauca.sed.api.repository.ComentarioRepository;

@Service
public class ComentarioService {
    @Autowired
    private ComentarioRepository comentarioRepository;

    public List<Comentario> findAll() {
        List<Comentario> list = new ArrayList<>();
        this.comentarioRepository.findAll().forEach(list::add);
        return list;
    }

    public Comentario findByOid(Integer oid) {
        Optional<Comentario> resultado = this.comentarioRepository.findById(oid);

        if (resultado.isPresent()) {
            return resultado.get();
        }

        return null;
    }

    public Comentario save(Comentario comentario) {
        Comentario result = null;
        try {
            result = this.comentarioRepository.save(comentario);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return result;
    }

    public void delete(Integer oid) {
        this.comentarioRepository.deleteById(oid);
    }
}
