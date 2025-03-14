package co.edu.unicauca.sed.api.service;

import co.edu.unicauca.sed.api.dto.ApiResponse;
import co.edu.unicauca.sed.api.dto.CatalogoDTO;
import co.edu.unicauca.sed.api.enums.*;
import co.edu.unicauca.sed.api.repository.RolRepository;
import co.edu.unicauca.sed.api.repository.TipoActividadRepository;
import co.edu.unicauca.sed.api.repository.UsuarioDetalleRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import co.edu.unicauca.sed.api.repository.PreguntaRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class CatalogoService {

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private TipoActividadRepository tipoActividadRepository;

    @Autowired
    private PreguntaRepository preguntaRepository;

    public ApiResponse<CatalogoDTO> obtenerCatalogo() {
        try {
            CatalogoDTO catalogoDTO = new CatalogoDTO();

            catalogoDTO.setFacultades(FacultadEnum.getSelectOptions());
            catalogoDTO.setDepartamentos(DepartamentoEnum.getSelectOptions());
            catalogoDTO.setCategorias(CategoriaEnum.getSelectOptions());
            catalogoDTO.setContrataciones(ContratacionEnum.getSelectOptions());
            catalogoDTO.setDedicaciones(DedicacionEnum.getSelectOptions());
            catalogoDTO.setEstudios(EstudiosEnum.getSelectOptions());

            catalogoDTO.setRoles(
                rolRepository.findAll().stream()
                    .filter(Objects::nonNull)
                    .map(rol -> Map.<String, Object>of("codigo", rol.getOid(), "nombre", rol.getNombre()))
                    .collect(Collectors.toList()));

            catalogoDTO.setTipoActividades(
                tipoActividadRepository.findAll().stream()
                    .filter(Objects::nonNull)
                    .map(tipoActividad -> Map.<String, Object>of("codigo", tipoActividad.getOidTipoActividad(),
                        "nombre", tipoActividad.getNombre()))
                    .collect(Collectors.toList()));

            catalogoDTO.setPreguntaEvaluacionDocente(
                preguntaRepository.findAll().stream()
                    .filter(Objects::nonNull)
                    .map(pregunta -> {
                        Map<String, Object> preguntaMap = new HashMap<>();
                        preguntaMap.put("oidPregunta", pregunta.getOidPregunta());
                        preguntaMap.put("pregunta", pregunta.getPregunta());
                        preguntaMap.put("porcentajeImportancia", pregunta.getPorcentajeImportancia());
                        return preguntaMap;
                    })
                    .collect(Collectors.toList()));

            return new ApiResponse<>(200, "Catálogo obtenido correctamente.", catalogoDTO);

        } catch (Exception e) {
            return new ApiResponse<>(500, "Error al obtener el catálogo: " + e.getMessage(), null);
        }
    }
}
