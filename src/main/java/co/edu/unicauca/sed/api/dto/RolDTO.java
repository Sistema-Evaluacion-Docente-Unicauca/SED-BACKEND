package co.edu.unicauca.sed.api.dto;

public class RolDTO {
    private String nombre;

    public RolDTO(String nombre) {
        this.nombre = nombre;
    }

    // Getters and setters
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
