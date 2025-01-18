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
@Table(name = "EXTENSIONDETALLE", schema = "SEDOCENTE")
public class ExtensionDetalle {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "extensionDetalleSeq")
    @SequenceGenerator(name = "extensionDetalleSeq", sequenceName = "SEQ_OIDEXTENSIONDETALLE", allocationSize = 1)
    @Column(name = "OIDEXTENSIONDETALLE", nullable = false)
    private Integer oidExtensionDetalle;

    @OneToOne
    @JoinColumn(name = "OIDACTIVIDAD", nullable = false)
    private Actividad actividad;

    @Column(name = "ACTOADMINISTRATIVO", nullable = false)
    private String actoAdministrativo;

    @Column(name = "NOMBREPROYECTO", nullable = false)
    private String nombreProyecto;

    @Column(name = "FECHACREACION", updatable = false, nullable = false)
    @CreatedDate
    private LocalDateTime fechaCreacion;

    @Column(name = "FECHAACTUALIZACION", nullable = false)
    @LastModifiedDate
    private LocalDateTime fechaActualizacion;
}
