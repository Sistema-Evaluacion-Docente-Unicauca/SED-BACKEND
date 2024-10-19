package co.edu.unicauca.sed.api.service;

import org.springframework.stereotype.Service;

import co.edu.unicauca.sed.api.dto.ActividadDTO;
import co.edu.unicauca.sed.api.model.Actividad;
import co.edu.unicauca.sed.api.repository.ActividadRepository;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ActividadService {

    @Autowired
    private ActividadRepository actividadRepository;

    /**
     * Retrieves all activities that are part of active academic periods (state =
     * 1).
     * Converts the activities to DTO format.
     * 
     * @return List of activities as DTOs
     */
    public List<ActividadDTO> findAllInActivePeriods() {
        // Fetch all activities where the academic period is active
        List<Actividad> actividades = actividadRepository.findByProceso_OidPeriodoAcademico_Estado(1);

        // Convert the list of activities to DTO
        return actividades.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all activities along with their associated sources.
     * 
     * @return List of activities as DTOs
     */
    public List<ActividadDTO> findAll() {
        // Convert Iterable to List
        List<Actividad> actividades = new ArrayList<>();
        actividadRepository.findAll().forEach(actividades::add);

        // Convert the list of activities to DTO
        return actividades.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves activities for an evaluator in active academic periods.
     */
    public List<ActividadDTO> findActivitiesByEvaluadoInActivePeriod(Integer oidUsuario) {
        // Fetch activities for the evaluator where the academic period is active
        List<Actividad> actividades = actividadRepository
                .findByProceso_Evaluado_OidUsuarioAndProceso_OidPeriodoAcademico_Estado(oidUsuario, 1);

        // Convert the list of activities to DTO
        return actividades.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves activities for an evaluator.
     */
    public List<ActividadDTO> findActivitiesByEvaluado(Integer oidUsuario) {
        // Fetch activities for the evaluator
        List<Actividad> actividades = actividadRepository.findByProceso_Evaluado_OidUsuario(oidUsuario);

        // Convert the list of activities to DTO
        return actividades.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Finds an activity by its ID.
     */
    public Actividad findByOid(Integer oid) {
        return actividadRepository.findById(oid).orElse(null);
    }

    /**
     * Saves a new activity to the database.
     */
    public Actividad save(Actividad actividad) {
        return actividadRepository.save(actividad);
    }

    /**
     * Deletes an activity from the database by its ID.
     */
    public void delete(Integer oid) {
        actividadRepository.deleteById(oid);
    }

        /**
     * Converts an Actividad entity to ActividadDTO.
     */
    public ActividadDTO convertToDTO(Actividad actividad) {
        return new ActividadDTO(
                actividad.getNombre(),
                actividad.getFuentes(),
                actividad.getProceso().getEvaluador());
    }
}
