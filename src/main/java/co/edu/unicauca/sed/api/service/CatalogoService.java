package co.edu.unicauca.sed.api.service;

import co.edu.unicauca.sed.api.dto.ApiResponse;
import co.edu.unicauca.sed.api.dto.CatalogoDTO;
import co.edu.unicauca.sed.api.enums.CategoriaEnum;
import co.edu.unicauca.sed.api.enums.ContratacionEnum;
import co.edu.unicauca.sed.api.enums.DedicacionEnum;
import co.edu.unicauca.sed.api.enums.DepartamentoEnum;
import co.edu.unicauca.sed.api.enums.EstudiosEnum;
import co.edu.unicauca.sed.api.enums.FacultadEnum;
import co.edu.unicauca.sed.api.repository.RolRepository;
import co.edu.unicauca.sed.api.repository.TipoActividadRepository;
import co.edu.unicauca.sed.api.service.evaluacion_docente.EstadoEtapaDesarrolloService;
import co.edu.unicauca.sed.api.repository.PreguntaRepository;
import co.edu.unicauca.sed.api.repository.EstadoEtapaDesarrolloRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class CatalogoService {

    private final EstadoEtapaDesarrolloService estadoEtapaDesarrolloService;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private TipoActividadRepository tipoActividadRepository;

    @Autowired
    private PreguntaRepository preguntaRepository;

    @Autowired
    private EstadoEtapaDesarrolloRepository estadoEtapaDesarrolloRepository;

    CatalogoService(EstadoEtapaDesarrolloService estadoEtapaDesarrolloService) {
        this.estadoEtapaDesarrolloService = estadoEtapaDesarrolloService;
    }

    /**
     * Método principal para obtener el catálogo completo.
     */
    public ApiResponse<CatalogoDTO> obtenerCatalogo() {
        try {
            CatalogoDTO catalogoDTO = new CatalogoDTO();

            // Obtener las opciones para las secciones del catálogo
            catalogoDTO.setFacultades(obtenerFacultades());
            catalogoDTO.setDepartamentos(obtenerDepartamentos());
            catalogoDTO.setCategorias(obtenerCategorias());
            catalogoDTO.setContrataciones(obtenerContrataciones());
            catalogoDTO.setDedicaciones(obtenerDedicaciones());
            catalogoDTO.setEstudios(obtenerEstudios());
            catalogoDTO.setEstadoEtapaDesarrollo(obtenerEstadoEtapasDesarrollo());

            // Obtener Roles
            catalogoDTO.setRoles(obtenerRoles());

            // Obtener Tipo Actividades
            catalogoDTO.setTipoActividades(obtenerTipoActividades());

            // Obtener Preguntas de Evaluación Docente
            catalogoDTO.setPreguntaEvaluacionDocente(obtenerPreguntasEvaluacionDocente());

            return new ApiResponse<>(200, "Catálogo obtenido correctamente.", catalogoDTO);

        } catch (Exception e) {
            return new ApiResponse<>(500, "Error al obtener el catálogo: " + e.getMessage(), null);
        }
    }

    private List<Map<String, String>> obtenerFacultades() {
        return FacultadEnum.getSelectOptions().stream()
            .map(map -> map.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> String.valueOf(e.getValue()))))
            .collect(Collectors.toList());
    }

    private List<Map<String, String>> obtenerDepartamentos() {
        return DepartamentoEnum.getSelectOptions().stream()
            .map(map -> map.entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, e -> String.valueOf(e.getValue()))))
            .collect(Collectors.toList());
    }

    private List<Map<String, String>> obtenerCategorias() {
        return CategoriaEnum.getSelectOptions().stream()
            .map(map -> map.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> String.valueOf(e.getValue())))).collect(Collectors.toList());
    }

    private List<Map<String, String>> obtenerContrataciones() {
        return ContratacionEnum.getSelectOptions().stream()
            .map(map -> map.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> String.valueOf(e.getValue()))))
            .collect(Collectors.toList());
    }

    private List<Map<String, String>> obtenerDedicaciones() {
        return DedicacionEnum.getSelectOptions().stream()
            .map(map -> map.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> String.valueOf(e.getValue()))))
            .collect(Collectors.toList());
    }

    private List<Map<String, String>> obtenerEstudios() {
        return EstudiosEnum.getSelectOptions().stream()
            .map(map -> map.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> String.valueOf(e.getValue()))))
            .collect(Collectors.toList());
    }

    private List<Map<String, Object>> obtenerEstadoEtapasDesarrollo() {
        return estadoEtapaDesarrolloRepository.findAll().stream()
            .map(estado -> {
                Map<String, Object> estadoMap = new HashMap<>();
                estadoMap.put("oidEstadoEtapaDesarrollo", estado.getOidEstadoEtapaDesarrollo());
                estadoMap.put("nombre", estado.getNombre());
                return estadoMap;
            }).collect(Collectors.toList());
    }

    private List<Map<String, Object>> obtenerRoles() {
        return rolRepository.findAll().stream()
            .filter(Objects::nonNull)
            .map(rol -> Map.<String, Object>of("codigo", rol.getOid(), "nombre", rol.getNombre()))
            .collect(Collectors.toList());
    }

    private List<Map<String, Object>> obtenerTipoActividades() {
        return tipoActividadRepository.findAll().stream()
            .filter(Objects::nonNull)
            .map(tipoActividad -> Map.<String, Object>of("codigo", tipoActividad.getOidTipoActividad(), "nombre", tipoActividad.getNombre()))
            .collect(Collectors.toList());
    }

    private List<Map<String, Object>> obtenerPreguntasEvaluacionDocente() {
        return preguntaRepository.findAll().stream()
                .filter(pregunta -> pregunta.getEstadoPregunta())
                .map(pregunta -> {
                    Map<String, Object> preguntaMap = new HashMap<>();
                    preguntaMap.put("oidPregunta", pregunta.getOidPregunta());
                    preguntaMap.put("pregunta", pregunta.getPregunta());
                    return preguntaMap;
                }).collect(Collectors.toList());
    }    
}
