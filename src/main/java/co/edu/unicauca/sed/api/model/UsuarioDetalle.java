package co.edu.unicauca.sed.api.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "USUARIODETALLE", schema = "SEDOCENTE")
@Data
public class UsuarioDetalle {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "usuarioDetalleSeq")
    @SequenceGenerator(name = "usuarioDetalleSeq", sequenceName = "SEQ_OIDUSUARIODETALLE", allocationSize = 1)
    @Column(name = "OIDUSUARIODETALLE", nullable = false)
    private Integer oidUsuarioDetalle;

    @Column(name = "IDENTIFICACION", nullable = false)
    private String identificacion;

    @Column(name = "FACULTAD", nullable = false)
    private String facultad;

    @Column(name = "DEPARTAMENTO", nullable = false)
    private String departamento;

    @Column(name = "CATEGORIA")
    private String categoria;

    @Column(name = "CONTRATACION")
    private String contratacion;

    @Column(name = "DEDICACION")
    private String dedicacion;

    @Column(name = "ESTUDIOS")
    private String estudios;

    @Column(name = "FECHACREACION")
    @CreationTimestamp
    private LocalDateTime fechaCreacion;

    @Column(name = "FECHAACTUALIZACION")
    @UpdateTimestamp
    private LocalDateTime fechaActualizacion;
}
