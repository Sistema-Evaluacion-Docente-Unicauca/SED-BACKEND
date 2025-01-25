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
@Table(name = "TRABAJOINVESTIGACIONDETALLE")
public class TrabajoInvestigacionDetalle {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "trabajoInvestigacionDetalleSeq")
    @SequenceGenerator(name = "trabajoInvestigacionDetalleSeq", sequenceName = "SEQ_OIDTRABAJOINVESTIGACIONDETALLE", allocationSize = 1)
    @Column(name = "OIDTRABAJOINVESTIGACIONDETALLE", nullable = false)
    private Integer oidTrabajoInvestigacionDetalle;

    @OneToOne
    @JsonIgnore
    @JoinColumn(name = "OIDACTIVIDAD", nullable = false)
    private Actividad actividad;

    @Column(name = "ACTOADMINISTRATIVO", nullable = false)
    private String actoAdministrativo;

    @Column(name = "FECHACREACION", updatable = false, nullable = false)
    @CreationTimestamp
    private LocalDateTime fechaCreacion;

    @Column(name = "FECHAACTUALIZACION", nullable = false)
    @UpdateTimestamp
    private LocalDateTime fechaActualizacion;
}
