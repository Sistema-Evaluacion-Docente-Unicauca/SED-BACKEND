package co.edu.unicauca.sed.api.model;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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
@Table(name = "USUARIO", schema = "SEDOCENTE")
@Data
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "userSeq")
    @SequenceGenerator(name = "userSeq", sequenceName = "SEQ_OIDUSUARIO", allocationSize = 1)
    @Column(name = "OIDUSUARIO")
    private Integer oidUsuario;

    @Column(name = "IDENTIFICACION", nullable = false)
    private String identificacion;

    @Column (name = "FACULTAD", nullable = false)
    private String facultad;

    @Column (name = "DEPARTAMENTO", nullable = false)
    private String departamento;

    @Column (name = "CATEGORIA", nullable = false)
    private String categoria;

    @Column (name = "CONTRATACION")
    private String contratacion;

    @Column (name = "DEDICACION")
    private String dedicacion;

    @Column (name = "ESTUDIOS")
    private String estudios;

    @Column (name = "estado", nullable = false)
    private Integer estado;

    @Column(name = "FECHACREACION", updatable = false)
    @CreationTimestamp
    private LocalDateTime fechaCreacion;

    @Column(name = "FECHAACTUALIZACION")
    @UpdateTimestamp
    private LocalDateTime fechaActualizacion;

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
