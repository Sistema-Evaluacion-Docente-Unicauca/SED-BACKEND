package co.edu.unicauca.sed.api.controller;

import co.edu.unicauca.sed.api.dto.CatalogoDTO;
import co.edu.unicauca.sed.api.service.CatalogoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/catalogo")
public class CatalogoController {

    private final CatalogoService catalogoService;

    public CatalogoController(CatalogoService catalogoService) {
        this.catalogoService = catalogoService;
    }

    /**
     * Endpoint para obtener los valores únicos de facultad, departamento,
     * categoría, contratación, dedicación, estudios.
     *
     * @return Un objeto CatalogoDTO con los valores únicos.
     */
    @GetMapping("/obtenerCatalogo")
    public ResponseEntity<CatalogoDTO> obtenerCatalogo() {
        CatalogoDTO catalogo = catalogoService.obtenerCatalogo();
        return ResponseEntity.ok(catalogo);
    }
}
