package co.edu.unicauca.sed.api.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(name = "USUARIO", schema = "SEDOCENTE")
@Data
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "usuarioSeq")
    @SequenceGenerator(name = "usuarioSeq", sequenceName = "SEQ_OIDUSUARIO", allocationSize = 1)
    @Column(name = "OIDUSUARIO", nullable = false)
    private Integer oidUsuario;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "OIDUSUARIODETALLE", nullable = false)
    private UsuarioDetalle usuarioDetalle;

    @Column(name = "NOMBRES", nullable = false)
    private String nombres;

    @Column(name = "APELLIDOS", nullable = false)
    private String apellidos;

    @Column(name = "CORREO", nullable = false)
    private String correo;

    @Column(name = "ESTADO", nullable = false)
    private Short estado;

    @CreationTimestamp
    @Column(name = "FECHACREACION", nullable = false)
    private Timestamp fechaCreacion;

    @UpdateTimestamp
    @Column(name = "ULTIMOINGRESO")
    private Timestamp ultimoIngreso;

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
