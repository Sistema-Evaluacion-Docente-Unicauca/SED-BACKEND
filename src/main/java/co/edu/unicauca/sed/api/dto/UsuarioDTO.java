package co.edu.unicauca.sed.api.dto;

import java.util.List;

public class UsuarioDTO {
    private Integer oidUsuario;
    private String identificacion;
    private String nombres;
    private String apellidos;
    private List<RolDTO> roles;

    // Constructor
    public UsuarioDTO(Integer oidUsuario, String identificacion, String nombres, String apellidos, List<RolDTO> roles) {
        this.oidUsuario = oidUsuario;
        this.identificacion = identificacion;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.roles = roles;
    }

    // Getters and Setters
    public Integer getOidUsuario() {
        return oidUsuario;
    }

    public void setOidUsuario(Integer oidUsuario) {
        this.oidUsuario = oidUsuario;
    }

    public String getIdentificacion() {
        return identificacion;
    }

    public void setIdentificacion(String identificacion) {
        this.identificacion = identificacion;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public List<RolDTO> getRoles() {
        return roles;
    }

    public void setRoles(List<RolDTO> roles) {
        this.roles = roles;
    }
}
