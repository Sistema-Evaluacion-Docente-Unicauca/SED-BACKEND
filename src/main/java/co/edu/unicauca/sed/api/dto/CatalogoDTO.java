package co.edu.unicauca.sed.api.dto;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CatalogoDTO {

    private List<String> facultades;
    private List<String> departamentos;
    private List<String> categorias;
    private List<String> contrataciones;
    private List<String> dedicaciones;
    private List<String> estudios;
    private List<String> roles;
    private List<String> tipoActividades;

    // Getters y setters
    public List<String> getFacultades() {
        return facultades;
    }

    public void setFacultades(List<String> facultades) {
        this.facultades = facultades;
    }

    public List<String> getDepartamentos() {
        return departamentos;
    }

    public void setDepartamentos(List<String> departamentos) {
        this.departamentos = departamentos;
    }

    public List<String> getCategorias() {
        return categorias;
    }

    public void setCategorias(List<String> categorias) {
        this.categorias = categorias;
    }

    public List<String> getContrataciones() {
        return contrataciones;
    }

    public void setContrataciones(List<String> contrataciones) {
        this.contrataciones = contrataciones;
    }

    public List<String> getDedicaciones() {
        return dedicaciones;
    }

    public void setDedicaciones(List<String> dedicaciones) {
        this.dedicaciones = dedicaciones;
    }

    public List<String> getEstudios() {
        return estudios;
    }

    public void setEstudios(List<String> estudios) {
        this.estudios = estudios;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public List<String> getTipoActividades() {
        return tipoActividades;
    }

    public void setTipoActividades(List<String> tipoActividades) {
        this.tipoActividades = tipoActividades;
    }
}
