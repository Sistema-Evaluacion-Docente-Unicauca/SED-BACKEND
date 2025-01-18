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
@Table(name = "TRABAJODOCENCIADETALLE", schema = "SEDOCENTE")
public class TrabajoDocenciaDetalle {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "trabajoDocenciaDetalleSeq")
    @SequenceGenerator(name = "trabajoDocenciaDetalleSeq", sequenceName = "SEQ_OIDTRABAJODOCENCIADETALLE", allocationSize = 1)
    @Column(name = "OIDTRABAJODOCENCIADETALLE", nullable = false)
    private Integer oidTrabajoDocenciaDetalle;

    @OneToOne
    @JoinColumn(name = "OIDACTIVIDAD", nullable = false)
    private Actividad actividad;

    @Column(name = "ACTOADMINISTRATIVO", nullable = false)
    private String actoAdministrativo;

    @Column(name = "FECHACREACION", updatable = false, nullable = false)
    @CreatedDate
    private LocalDateTime fechaCreacion;

    @Column(name = "FECHAACTUALIZACION", nullable = false)
    @LastModifiedDate
    private LocalDateTime fechaActualizacion;
}
