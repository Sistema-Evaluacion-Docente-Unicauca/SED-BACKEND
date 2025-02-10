package co.edu.unicauca.sed.api.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import co.edu.unicauca.sed.api.model.Actividad;
import co.edu.unicauca.sed.api.model.Proceso;
import co.edu.unicauca.sed.api.repository.ProcesoRepository;
import co.edu.unicauca.sed.api.specification.ProcesoSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
public class ProcesoService {

    @Autowired
    private ProcesoRepository procesoRepository;

    @Autowired
    private PeriodoAcademicoService periodoAcademicoService;

    public Page<Proceso> findAll(Integer evaluadorId, Integer evaluadoId, Integer idPeriodo, String nombreProceso,
        LocalDateTime fechaCreacion, LocalDateTime fechaActualizacion, int page, int size) {

        if (idPeriodo == null) {
            idPeriodo = periodoAcademicoService.obtenerPeriodoAcademicoActivo();
        }
        Pageable pageable = PageRequest.of(page, size);

        return procesoRepository.findAll(
            ProcesoSpecification.byFilters(evaluadorId, evaluadoId, idPeriodo, nombreProceso, fechaCreacion,fechaActualizacion), pageable
        );
    }

    public Proceso findByOid(Integer oid) {
        Optional<Proceso> resultado = this.procesoRepository.findById(oid);

        if (resultado.isPresent()) {
            return resultado.get();
        }

        return null;
    }

    public Proceso save(Proceso proceso) {
        Proceso result = null;
        try {
            if (proceso.getNombreProceso() != null) {
                proceso.setNombreProceso(proceso.getNombreProceso().toUpperCase());
            }
            result = this.procesoRepository.save(proceso);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return result;
    }

    public Proceso update(Integer oid, Proceso proceso) {
        Proceso existingProceso = procesoRepository.findById(oid).orElse(null);
        if (existingProceso != null) {
            existingProceso.setEvaluador(proceso.getEvaluador());
            existingProceso.setEvaluado(proceso.getEvaluado());
            existingProceso.setOidPeriodoAcademico(proceso.getOidPeriodoAcademico());
            existingProceso.setNombreProceso(proceso.getNombreProceso().toUpperCase());
            existingProceso.setResolucion(proceso.getResolucion());
            existingProceso.setOficio(proceso.getOficio());
            existingProceso.setConsolidado(proceso.getConsolidado());
            existingProceso.setActividades(proceso.getActividades());
            return procesoRepository.save(existingProceso);
        }
        return null;
    }

    public void delete(Integer oid) {
        this.procesoRepository.deleteById(oid);
    }

    /**
     * Obtiene los procesos del evaluado en un período académico.
     */
    public List<Proceso> obtenerProcesosDelEvaluado(Integer idEvaluado, Integer idPeriodoAcademico) {
        List<Proceso> procesos = procesoRepository.findByEvaluado_OidUsuarioAndOidPeriodoAcademico_OidPeriodoAcademico(
                idEvaluado, idPeriodoAcademico);

        if (procesos.isEmpty()) {
            throw new IllegalArgumentException("No se encontraron procesos para el evaluado en el período académico especificado.");
        }
        return procesos;
    }

    public void guardarProceso(Actividad actividad) {
        if (actividad.getProceso() != null) {
            Proceso savedProceso = procesoRepository.save(actividad.getProceso());
            actividad.setProceso(savedProceso);
        }
    }
}
