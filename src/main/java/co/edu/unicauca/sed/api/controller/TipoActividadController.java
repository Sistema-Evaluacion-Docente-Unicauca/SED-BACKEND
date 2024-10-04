package co.edu.unicauca.sed.api.controller;

import co.edu.unicauca.sed.api.model.TipoActividad;
import co.edu.unicauca.sed.api.service.TipoActividadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tipoactividad")
public class TipoActividadController {

    @Autowired
    private TipoActividadService service;

    @GetMapping("/all")
    public List<TipoActividad> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TipoActividad> findById(@PathVariable Integer id) {
        TipoActividad tipoActividad = service.findById(id);
        return (tipoActividad != null) ? ResponseEntity.ok(tipoActividad) : ResponseEntity.notFound().build();
    }

    @PostMapping("/save")
    public TipoActividad save(@RequestBody TipoActividad tipoActividad) {
        return service.save(tipoActividad);
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }
}
