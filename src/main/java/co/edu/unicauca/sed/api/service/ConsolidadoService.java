package co.edu.unicauca.sed.api.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import co.edu.unicauca.sed.api.model.Consolidado;
import co.edu.unicauca.sed.api.repository.ConsolidadoRepository;

@Service
public class ConsolidadoService {
    @Autowired
    private ConsolidadoRepository consolidadoRepository;

    public List<Consolidado> findAll() {
        List<Consolidado> list = new ArrayList<>();
        this.consolidadoRepository.findAll().forEach(list::add);
        return list;
    }

    public Consolidado findByOid(Integer oid) {
        Optional<Consolidado> resultado = this.consolidadoRepository.findById(oid);
        return resultado.orElse(null);
    }

    public Consolidado save(Consolidado consolidado) {
        try {
            return this.consolidadoRepository.save(consolidado);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void delete(Integer oid) {
        this.consolidadoRepository.deleteById(oid);
    }
}
