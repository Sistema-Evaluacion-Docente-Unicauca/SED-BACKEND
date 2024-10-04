package co.edu.unicauca.sed.api.controller;

import co.edu.unicauca.sed.api.model.EstadoActividad;
import co.edu.unicauca.sed.api.service.EstadoActividadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/estadoactividad")
public class EstadoActividadController {

    @Autowired
    private EstadoActividadService service;

    @GetMapping("/all")
    public List<EstadoActividad> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<EstadoActividad> findById(@PathVariable Integer id) {
        EstadoActividad estadoActividad = service.findById(id);
        return (estadoActividad != null) ? ResponseEntity.ok(estadoActividad) : ResponseEntity.notFound().build();
    }

    @PostMapping("/save")
    public EstadoActividad save(@RequestBody EstadoActividad estadoActividad) {
        return service.save(estadoActividad);
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }
}
