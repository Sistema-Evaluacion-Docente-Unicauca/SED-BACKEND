package co.edu.unicauca.sed.api.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.edu.unicauca.sed.api.model.PeriodoAcademico;
import co.edu.unicauca.sed.api.repository.PeriodoAcademicoRepository;

@Service
public class PeriodoAcademicoService {

    @Autowired
    private PeriodoAcademicoRepository periodoAcademicoRepository;

    public List<PeriodoAcademico> findAll() {
        List<PeriodoAcademico> list = new ArrayList<>();
        this.periodoAcademicoRepository.findAll().forEach(list::add);
        return list;
    }

    public PeriodoAcademico findByOid(Integer oid) {
        Optional<PeriodoAcademico> resultado = this.periodoAcademicoRepository.findById(oid);

        if (resultado.isPresent()) {
            return resultado.get();
        }

        return null;
    }

    public PeriodoAcademico save(PeriodoAcademico periodoAcademico) {
        PeriodoAcademico result = null;
        try {
            result = this.periodoAcademicoRepository.save(periodoAcademico);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return result;
    }

    public void delete(Integer oid) {
        this.periodoAcademicoRepository.deleteById(oid);
    }
}
