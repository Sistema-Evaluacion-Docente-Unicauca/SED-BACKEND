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
@Table(name = "PROYECTOINVDETALLE", schema = "SEDOCENTE")
public class ProyectoInvestigacionDetalle {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "proyectoInvDetalleSeq")
    @SequenceGenerator(name = "proyectoInvDetalleSeq", sequenceName = "SEQ_PROYECTOINVDETALLE", allocationSize = 1)
    @Column(name = "OIDPROYECTOINVDETALLE", nullable = false)
    private Integer oidProyectoInvestigacionDetalle;

    @OneToOne
    @JsonIgnore
    @JoinColumn(name = "OIDACTIVIDAD", nullable = false)
    private Actividad actividad;

    @Column(name = "VRI", nullable = false)
    private String vri;

    @Column(name = "NOMBREPROYECTO", nullable = false)
    private String nombreProyecto;

    @Column(name = "FECHACREACION", updatable = false, nullable = false)
    @CreationTimestamp
    private LocalDateTime fechaCreacion;

    @Column(name = "FECHAACTUALIZACION", nullable = false)
    @UpdateTimestamp
    private LocalDateTime fechaActualizacion;
}
