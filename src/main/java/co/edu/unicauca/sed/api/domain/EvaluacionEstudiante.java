package co.edu.unicauca.sed.api.domain;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "EVALUACIONESTUDIANTE")
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
