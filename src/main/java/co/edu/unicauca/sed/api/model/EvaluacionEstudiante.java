package co.edu.unicauca.sed.api.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Table(name = "EVALUACIONESTUDIANTE", schema = "SEDOCENTE")
@Data
public class EvaluacionEstudiante {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "evaluacionEstudianteSeq")
    @SequenceGenerator(name = "evaluacionEstudianteSeq", sequenceName = "SEQ_OIDEVALUACIONESTUDIANTE", allocationSize = 1)
    @Column(name = "OIDEVALUACIONESTUDIANTE")
    private Integer oidEvaluacionEstudiante;

    @ManyToOne
    @JoinColumn(name = "OIDFUENTE", nullable = false)
    private Fuente fuente;

    @Column(name = "OBSERVACION")
    private String observacion;

    @Column(name = "FIRMA", nullable = false)
    private String firma;

    @ManyToMany(fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(name = "ENCUESTAESTUDIANTE", joinColumns = @JoinColumn(name = "OIDEVALUACIONESTUDIANTE"), inverseJoinColumns = @JoinColumn(name = "OIDENCUESTA"))
    private List<Encuesta> encuestas;
}
