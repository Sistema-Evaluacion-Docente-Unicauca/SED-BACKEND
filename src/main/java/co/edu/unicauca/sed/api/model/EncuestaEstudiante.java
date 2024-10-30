package co.edu.unicauca.sed.api.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "ENCUESTAESTUDIANTE")
@Data
public class EncuestaEstudiante {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "encuestaEstudianteSeq")
    @SequenceGenerator(name = "encuestaEstudianteSeq", sequenceName = "SEQ_OIDENCUESTAESTUDIANTE", allocationSize = 1)
    @Column(name = "OIDENCUESTAESTUDIANTE")
    private Integer oidEncuestaEstudiante;

    @ManyToOne
    @JoinColumn(name = "OIDENCUESTA", nullable = false)
    @JsonBackReference
    private Encuesta encuesta;

    @ManyToOne
    @JoinColumn(name = "OIDEVALUACIONESTUDIANTE", nullable = false)
    private EvaluacionEstudiante evaluacionEstudiante;

    @Column(name = "FECHAEVALUACION", updatable = false)
    @CreationTimestamp
    private LocalDateTime fechaEvaluacion;
}
