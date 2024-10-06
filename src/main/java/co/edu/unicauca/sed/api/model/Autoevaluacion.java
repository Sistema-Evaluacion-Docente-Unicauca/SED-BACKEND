package co.edu.unicauca.sed.api.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "AUTOEVALUACION", schema = "SEDOCENTE")
@Data
public class Autoevaluacion {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "autoevaluacionSeq")
    @SequenceGenerator(name = "autoevaluacionSeq", sequenceName = "SEQ_OIDAUTOEVALUACION", allocationSize = 1)
    @Column(name = "OIDAUTOEVALUACION")
    private Integer oidAutoevaluacion;

    @ManyToOne
    @JoinColumn(name = "OIDFUENTE", nullable = false)
    private Fuente fuente;

    @Column(name = "FIRMA", nullable = false)
    private String firma;

    @Lob
    @Column(name = "SCREENSHOTSIMCA")
    private byte[] screenshotSimca;
}
