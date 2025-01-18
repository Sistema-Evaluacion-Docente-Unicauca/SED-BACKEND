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
@Table(name = "OTROSERVICIODETALLE", schema = "SEDOCENTE")
public class OtroServicioDetalle {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "otroServicioDetalleSeq")
    @SequenceGenerator(name = "otroServicioDetalleSeq", sequenceName = "SEQ_OIDOTROSERVICIODETALLE", allocationSize = 1)
    @Column(name = "OIDOTROSERVICIODETALLE", nullable = false)
    private Integer oidOtroServicioDetalle;

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
