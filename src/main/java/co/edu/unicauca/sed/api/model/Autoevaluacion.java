package co.edu.unicauca.sed.api.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "AUTOEVALUACION", schema = "SEDOCENTE")
@Data
public class Autoevaluacion {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "autoevaluacionSeq")
    @SequenceGenerator(name = "autoevaluacionSeq", sequenceName = "SEQ_OIDAUTOEVALUACION", allocationSize = 1)
    @Column(name = "OIDAUTOEVALUACION", nullable = false)
    private Integer oidAutoevaluacion;

    @ManyToOne
    @JoinColumn(name = "OIDFUENTE", nullable = false)
    private Fuente fuente;

    @Column(name = "FIRMA", nullable = false)
    private String firma;

    @Column(name = "SCREENSHOTSIMCA")
    private String screenshotSimca;

    @Column(name = "FECHACREACION", updatable = false)
    @CreationTimestamp
    private LocalDateTime fechaCreacion;

    @Column(name = "FECHAACTUALIZACION")
    @UpdateTimestamp
    private LocalDateTime fechaActualizacion;
}
