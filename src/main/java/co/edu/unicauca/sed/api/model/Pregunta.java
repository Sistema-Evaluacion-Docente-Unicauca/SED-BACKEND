package co.edu.unicauca.sed.api.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "PREGUNTA")
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
