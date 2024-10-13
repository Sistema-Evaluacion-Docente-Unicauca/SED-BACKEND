package co.edu.unicauca.sed.api.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "ENCUESTAPREGUNTA", schema = "SEDOCENTE")
@Data
public class EncuestaPregunta {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "encuestaPreguntaSeq")
    @SequenceGenerator(name = "encuestaPreguntaSeq", sequenceName = "SEQ_OIDENCUESTAPREGUNTA", allocationSize = 1)
    @Column(name = "OIDENCUESTAPREGUNTA")
    private Integer oidEncuestaPregunta;

    @ManyToOne
    @JoinColumn(name = "OIDENCUESTA", nullable = false)
    private Encuesta encuesta;

    @ManyToOne
    @JoinColumn(name = "OIDPREGUNTA", nullable = false)
    private Pregunta pregunta;

    @Column(name = "RESPUESTA", nullable = false)
    private String respuesta;
}
