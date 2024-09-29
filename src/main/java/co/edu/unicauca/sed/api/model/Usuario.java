package co.edu.unicauca.sed.api.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "USUARIO", schema = "ACADEMICO")
@Data
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "userSeq")
    @SequenceGenerator(name = "userSeq", sequenceName = "SEQ_OIDUSER", allocationSize = 1)
    @Column(name = "OIDUSUARIO")
    private Integer oidUsuario;

    @Column
    private String identificacion;

    @Column
    private String nombres;

    @Column
    private String apellidos;

    @Column
    private String correo;

    @Column
    private String facultad;

    @Column
    private String departamento;

    @Column
    private String categoria;

    @Column
    private String contratacion;

    @Column
    private String dedicacion;

    @Column
    private String estudios;

    @Column
    private Integer estado;

    @ManyToMany(fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(name = "ROLUSUARIO", joinColumns = @JoinColumn(name = "OIDUSUARIO"), inverseJoinColumns = @JoinColumn(name = "OIDROL"))
    private List<Rol> roles;

    @JsonIgnore
    @OneToMany(mappedBy = "evaluador", cascade = CascadeType.REMOVE)
    private List<Proceso> procesosEvaluados;

    @JsonIgnore
    @OneToMany(mappedBy = "evaluado", cascade = CascadeType.REMOVE)
    private List<Proceso> procesosEvaluado;
}
