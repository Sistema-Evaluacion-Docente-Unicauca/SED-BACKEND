package co.edu.unicauca.sed.api.controller;

import co.edu.unicauca.sed.api.model.Actividad;
import co.edu.unicauca.sed.api.service.ActividadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/actividad")
public class ActividadController {

    @Autowired
    private ActividadService service;

    @GetMapping("/all")
    public List<Actividad> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Actividad> findById(@PathVariable Integer id) {
        Actividad actividad = service.findById(id);
        return (actividad != null) ? ResponseEntity.ok(actividad) : ResponseEntity.notFound().build();
    }

    @PostMapping("/save")
    public Actividad save(@RequestBody Actividad actividad) {
        return service.save(actividad);
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }
}
