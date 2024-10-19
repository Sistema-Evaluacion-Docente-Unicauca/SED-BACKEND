package co.edu.unicauca.sed.api.dto;

import co.edu.unicauca.sed.api.model.Usuario;
import co.edu.unicauca.sed.api.model.Fuente;
import java.util.List;

public class ActividadDTO {

    private String nombre;           // Nombre de la actividad
    private List<Fuente> fuentes;    // Lista de fuentes asociadas a la actividad
    private Usuario evaluador;       // Evaluador asociado al proceso de la actividad

    // Constructor, getters y setters
    public ActividadDTO(String nombre, List<Fuente> fuentes, Usuario evaluador) {
        this.nombre = nombre;
        this.fuentes = fuentes;
        this.evaluador = evaluador;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public List<Fuente> getFuentes() {
        return fuentes;
    }

    public void setFuentes(List<Fuente> fuentes) {
        this.fuentes = fuentes;
    }

    public Usuario getEvaluador() {
        return evaluador;
    }

    public void setEvaluador(Usuario evaluador) {
        this.evaluador = evaluador;
    }
}