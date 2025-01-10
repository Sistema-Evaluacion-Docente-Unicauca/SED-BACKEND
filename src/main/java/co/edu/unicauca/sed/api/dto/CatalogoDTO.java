package co.edu.unicauca.sed.api.dto;

import java.util.List;
import java.util.Map;

public class CatalogoDTO {

    private List<Map<String, Object>> facultades;
    private List<Map<String, Object>> departamentos;
    private List<Map<String, Object>> categorias;
    private List<Map<String, Object>> contrataciones;
    private List<Map<String, Object>> dedicaciones;
    private List<Map<String, Object>> estudios;
    private List<Map<String, Object>> roles;
    private List<Map<String, Object>> tipoActividades;

    public List<Map<String, Object>> getFacultades() {
        return facultades;
    }

    public void setFacultades(List<Map<String, Object>> facultades) {
        this.facultades = facultades;
    }

    public List<Map<String, Object>> getDepartamentos() {
        return departamentos;
    }

    public void setDepartamentos(List<Map<String, Object>> departamentos) {
        this.departamentos = departamentos;
    }

    public List<Map<String, Object>> getCategorias() {
        return categorias;
    }

    public void setCategorias(List<Map<String, Object>> categorias) {
        this.categorias = categorias;
    }

    public List<Map<String, Object>> getContrataciones() {
        return contrataciones;
    }

    public void setContrataciones(List<Map<String, Object>> contrataciones) {
        this.contrataciones = contrataciones;
    }

    public List<Map<String, Object>> getDedicaciones() {
        return dedicaciones;
    }

    public void setDedicaciones(List<Map<String, Object>> dedicaciones) {
        this.dedicaciones = dedicaciones;
    }

    public List<Map<String, Object>> getEstudios() {
        return estudios;
    }

    public void setEstudios(List<Map<String, Object>> estudios) {
        this.estudios = estudios;
    }

    public List<Map<String, Object>> getRoles() {
        return roles;
    }

    public void setRoles(List<Map<String, Object>> roles) {
        this.roles = roles;
    }

    public List<Map<String, Object>> getTipoActividades() {
        return tipoActividades;
    }

    public void setTipoActividades(List<Map<String, Object>> tipoActividades) {
        this.tipoActividades = tipoActividades;
    }
}
