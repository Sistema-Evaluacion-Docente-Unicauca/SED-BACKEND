package co.edu.unicauca.sed.api.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "PREGUNTA", schema = "SEDOCENTE")
@Data
public class Pregunta {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "preguntaSeq")
    @SequenceGenerator(name = "preguntaSeq", sequenceName = "SEQ_OIDPREGUNTA", allocationSize = 1)
    @Column(name = "OIDPREGUNTA")
    private Integer oidPregunta;

    @Column(name = "PREGUNTA", nullable = false)
    private String pregunta;

    @Column(name = "PORCENTAJEIMPORTANCIA", nullable = false)
    private Float porcentajeImportancia;
}
