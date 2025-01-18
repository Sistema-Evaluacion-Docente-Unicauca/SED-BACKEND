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
@Table(name = "ADMINISTRACIONDETALLE")
public class AdministracionDetalle {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "administracionDetalleSeq")
    @SequenceGenerator(name = "administracionDetalleSeq", sequenceName = "SEQ_OIDADMINISTRACIONDETALLE", allocationSize = 1)
    @Column(name = "OIDADMINISTRACIONDETALLE")
    private Integer oidAdministracionDetalle;

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
