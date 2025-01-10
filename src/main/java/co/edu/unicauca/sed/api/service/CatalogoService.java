package co.edu.unicauca.sed.api.service;

import co.edu.unicauca.sed.api.dto.CatalogoDTO;
import co.edu.unicauca.sed.api.model.Rol;
import co.edu.unicauca.sed.api.model.TipoActividad;
import co.edu.unicauca.sed.api.repository.RolRepository;
import co.edu.unicauca.sed.api.repository.TipoActividadRepository;
import co.edu.unicauca.sed.api.repository.UsuarioDetalleRepository;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

@Service
public class CatalogoService {

    private final UsuarioDetalleRepository usuarioDetalleRepository;
    private final RolRepository rolRepository;
    private final TipoActividadRepository tipoActividadRepository;

    public CatalogoService(UsuarioDetalleRepository usuarioDetalleRepository, RolRepository rolRepository, TipoActividadRepository tipoActividadRepository) {
        this.usuarioDetalleRepository = usuarioDetalleRepository;
        this.rolRepository = rolRepository;
        this.tipoActividadRepository = tipoActividadRepository;
    }

    public CatalogoDTO obtenerCatalogo() {
        CatalogoDTO catalogoDTO = new CatalogoDTO();

        // Facultades
        catalogoDTO.setFacultades(
            usuarioDetalleRepository.findDistinctFacultad().stream()
                .filter(Objects::nonNull)
                .map(facultad -> Map.<String, Object>of("codigo", facultad, "nombre", facultad))
                .collect(Collectors.toList())
        );

        // Departamentos
        catalogoDTO.setDepartamentos(
            usuarioDetalleRepository.findDistinctDepartamento().stream()
                .filter(Objects::nonNull)
                .map(departamento -> Map.<String, Object>of("codigo", departamento, "nombre", departamento))
                .collect(Collectors.toList())
        );

        // Categorías
        catalogoDTO.setCategorias(
            usuarioDetalleRepository.findDistinctCategoria().stream()
                .filter(Objects::nonNull)
                .map(categoria -> Map.<String, Object>of("codigo", categoria, "nombre", categoria))
                .collect(Collectors.toList())
        );

        // Contrataciones
        catalogoDTO.setContrataciones(
            usuarioDetalleRepository.findDistinctContratacion().stream()
                .filter(Objects::nonNull)
                .map(contratacion -> Map.<String, Object>of("codigo", contratacion, "nombre", contratacion))
                .collect(Collectors.toList())
        );

        // Dedicaciones
        catalogoDTO.setDedicaciones(
            usuarioDetalleRepository.findDistinctDedicacion().stream()
                .filter(Objects::nonNull)
                .map(dedicacion -> Map.<String, Object>of("codigo", dedicacion, "nombre", dedicacion))
                .collect(Collectors.toList())
        );

        // Estudios
        catalogoDTO.setEstudios(
            usuarioDetalleRepository.findDistinctEstudios().stream()
                .filter(Objects::nonNull)
                .map(estudio -> Map.<String, Object>of("codigo", estudio, "nombre", estudio))
                .collect(Collectors.toList())
        );

        // Roles
        catalogoDTO.setRoles(
            rolRepository.findAll().stream()
                .filter(Objects::nonNull)
                .map(rol -> Map.<String, Object>of("codigo", rol.getOid(), "nombre", rol.getNombre()))
                .collect(Collectors.toList())
        );

        // Tipo de Actividades
        catalogoDTO.setTipoActividades(
            tipoActividadRepository.findAll().stream()
                .filter(Objects::nonNull)
                .map(tipoActividad -> Map.<String, Object>of("codigo", tipoActividad.getOidTipoActividad(), "nombre", tipoActividad.getNombre()))
                .collect(Collectors.toList())
        );

        return catalogoDTO;
    }
}
