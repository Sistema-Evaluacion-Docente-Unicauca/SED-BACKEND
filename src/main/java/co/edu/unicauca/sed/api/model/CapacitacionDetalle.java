package co.edu.unicauca.sed.api.model;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;

@Data
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "CAPACITACIONDETALLE")
public class CapacitacionDetalle {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "capacitacionDetalleSeq")
    @SequenceGenerator(name = "capacitacionDetalleSeq", sequenceName = "SEQ_OIDCAPACITACIONDETALLE", allocationSize = 1)
    @Column(name = "OIDCAPACITACIONDETALLE")
    private Integer oidCapacitacionDetalle;

    @OneToOne
    @JoinColumn(name = "OIDACTIVIDAD", nullable = false)
    private Actividad actividad;

    @Column(name = "ACTOADMINISTRATIVO", nullable = false)
    private String actoAdministrativo;

    @Column(name = "ACTIVIDAD", nullable = false)
    private String actividadDetalle;

    @Column(name = "FECHACREACION", updatable = false, nullable = false)
    @CreatedDate
    private LocalDateTime fechaCreacion;

    @Column(name = "FECHAACTUALIZACION", nullable = false)
    @LastModifiedDate
    private LocalDateTime fechaActualizacion;
}
