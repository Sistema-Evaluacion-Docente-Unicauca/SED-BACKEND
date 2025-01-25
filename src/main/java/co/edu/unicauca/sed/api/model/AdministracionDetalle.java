package co.edu.unicauca.sed.api.model;

import jakarta.persistence.*;
import lombok.Data;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;

@Data
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "ADMINISTRACIONDETALLE")
public class AdministracionDetalle {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "administracionDetalleSeq")
    @SequenceGenerator(name = "administracionDetalleSeq", sequenceName = "SEQ_OIDADMINISTRACIONDETALLE", allocationSize = 1)
    @Column(name = "OIDADMINISTRACIONDETALLE")
    private Integer oidAdministracionDetalle;

    @OneToOne
    @JsonIgnore
    @JoinColumn(name = "OIDACTIVIDAD", nullable = false)
    private Actividad actividad;

    @Column(name = "ACTOADMINISTRATIVO", nullable = false)
    private String actoAdministrativo;

    @Column(name = "DETALLE", nullable = false)
    private String detalle;

    @Column(name = "FECHACREACION", updatable = false, nullable = false)
    @CreationTimestamp
    private LocalDateTime fechaCreacion;

    @Column(name = "FECHAACTUALIZACION", nullable = false)
    @UpdateTimestamp
    private LocalDateTime fechaActualizacion;
}
