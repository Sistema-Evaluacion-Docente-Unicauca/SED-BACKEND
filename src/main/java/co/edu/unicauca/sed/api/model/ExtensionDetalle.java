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
@Table(name = "EXTENSIONDETALLE")
public class ExtensionDetalle {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "extensionDetalleSeq")
    @SequenceGenerator(name = "extensionDetalleSeq", sequenceName = "SEQ_OIDEXTENSIONDETALLE", allocationSize = 1)
    @Column(name = "OIDEXTENSIONDETALLE", nullable = false)
    private Integer oidExtensionDetalle;

    @OneToOne
    @JsonIgnore
    @JoinColumn(name = "OIDACTIVIDAD", nullable = false)
    private Actividad actividad;

    @Column(name = "ACTOADMINISTRATIVO", nullable = false)
    private String actoAdministrativo;

    @Column(name = "NOMBREPROYECTO", nullable = false)
    private String nombreProyecto;

    @Column(name = "FECHACREACION", updatable = false, nullable = false)
    @CreationTimestamp
    private LocalDateTime fechaCreacion;

    @Column(name = "FECHAACTUALIZACION", nullable = false)
    @UpdateTimestamp
    private LocalDateTime fechaActualizacion;
}
