package co.edu.unicauca.sed.api.service;

import co.edu.unicauca.sed.api.dto.CatalogoDTO;
import co.edu.unicauca.sed.api.repository.UsuarioDetalleRepository;

import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

@Service
public class CatalogoService {

    private final UsuarioDetalleRepository usuarioDetalleRepository;

    public CatalogoService(UsuarioDetalleRepository usuarioDetalleRepository) {
        this.usuarioDetalleRepository = usuarioDetalleRepository;
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

        return catalogoDTO;
    }

}
