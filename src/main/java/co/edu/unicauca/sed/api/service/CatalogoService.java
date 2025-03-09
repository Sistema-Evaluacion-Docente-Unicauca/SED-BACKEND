package co.edu.unicauca.sed.api.service;

import co.edu.unicauca.sed.api.dto.ApiResponse;
import co.edu.unicauca.sed.api.dto.CatalogoDTO;
import co.edu.unicauca.sed.api.enums.*;
import co.edu.unicauca.sed.api.repository.RolRepository;
import co.edu.unicauca.sed.api.repository.TipoActividadRepository;
import co.edu.unicauca.sed.api.repository.UsuarioDetalleRepository;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class CatalogoService {

    private final UsuarioDetalleRepository usuarioDetalleRepository;
    private final RolRepository rolRepository;
    private final TipoActividadRepository tipoActividadRepository;

    public CatalogoService(UsuarioDetalleRepository usuarioDetalleRepository, RolRepository rolRepository,
            TipoActividadRepository tipoActividadRepository) {
        this.usuarioDetalleRepository = usuarioDetalleRepository;
        this.rolRepository = rolRepository;
        this.tipoActividadRepository = tipoActividadRepository;
    }

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

            return new ApiResponse<>(200, "Catálogo obtenido correctamente.", catalogoDTO);

        } catch (Exception e) {
            return new ApiResponse<>(500, "Error al obtener el catálogo: " + e.getMessage(), null);
        }
    }
}
