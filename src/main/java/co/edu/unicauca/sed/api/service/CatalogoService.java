package co.edu.unicauca.sed.api.service;

import co.edu.unicauca.sed.api.dto.CatalogoDTO;
import co.edu.unicauca.sed.api.repository.RolRepository;
import co.edu.unicauca.sed.api.repository.TipoActividadRepository;
import co.edu.unicauca.sed.api.repository.UsuarioDetalleRepository;
import org.springframework.stereotype.Service;
import java.util.Objects;
import java.util.stream.Collectors;

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

        catalogoDTO.setFacultades(usuarioDetalleRepository.findDistinctFacultad().stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList()));

        catalogoDTO.setDepartamentos(usuarioDetalleRepository.findDistinctDepartamento().stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList()));

        catalogoDTO.setCategorias(usuarioDetalleRepository.findDistinctCategoria().stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList()));

        catalogoDTO.setContrataciones(usuarioDetalleRepository.findDistinctContratacion().stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList()));

        catalogoDTO.setDedicaciones(usuarioDetalleRepository.findDistinctDedicacion().stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList()));

        catalogoDTO.setEstudios(usuarioDetalleRepository.findDistinctEstudios().stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList()));

        catalogoDTO.setRoles(rolRepository.findDistinctNombre().stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList()));

        catalogoDTO.setTipoActividades(tipoActividadRepository.findDistinctNombre().stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList()));

        return catalogoDTO;
    }
}
