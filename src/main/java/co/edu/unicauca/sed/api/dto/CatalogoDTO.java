package co.edu.unicauca.sed.api.dto;

import java.util.List;
import java.util.Map;

public class CatalogoDTO {

    private List<Map<String, String>> facultades;
    private List<Map<String, String>> departamentos;
    private List<Map<String, String>> categorias;
    private List<Map<String, String>> contrataciones;
    private List<Map<String, String>> dedicaciones;
    private List<Map<String, String>> estudios;
    private List<Map<String, Object>> roles;
    private List<Map<String, Object>> tipoActividades;

    public List<Map<String, String>> getFacultades() {
        return facultades;
    }

    public void setFacultades(List<Map<String, String>> facultades) {
        this.facultades = facultades;
    }

    public List<Map<String, String>> getDepartamentos() {
        return departamentos;
    }

    public void setDepartamentos(List<Map<String, String>> departamentos) {
        this.departamentos = departamentos;
    }

    public List<Map<String, String>> getCategorias() {
        return categorias;
    }

    public void setCategorias(List<Map<String, String>> categorias) {
        this.categorias = categorias;
    }

    public List<Map<String, String>> getContrataciones() {
        return contrataciones;
    }

    public void setContrataciones(List<Map<String, String>> contrataciones) {
        this.contrataciones = contrataciones;
    }

    public List<Map<String, String>> getDedicaciones() {
        return dedicaciones;
    }

    public void setDedicaciones(List<Map<String, String>> dedicaciones) {
        this.dedicaciones = dedicaciones;
    }

    public List<Map<String, String>> getEstudios() {
        return estudios;
    }

    public void setEstudios(List<Map<String, String>> estudios) {
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
