package co.edu.unicauca.sed.api.dto;

public class RolDTO {
    private String nombre;
    private String estado;

    public RolDTO(String nombre, Integer estado) {
        this.nombre = nombre;
        this.estado = estado == 1 ? "activo" : "inactivo";
    }

    // Getters and setters
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
