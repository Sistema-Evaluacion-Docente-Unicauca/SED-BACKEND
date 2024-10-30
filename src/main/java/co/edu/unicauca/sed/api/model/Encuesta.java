package co.edu.unicauca.sed.api.model;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "ENCUESTA")
@Data
public class Encuesta {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "encuestaSeq")
    @SequenceGenerator(name = "encuestaSeq", sequenceName = "SEQ_OIDENCUESTA", allocationSize = 1)
    @Column(name = "OIDENCUESTA")
    private Integer oidEncuesta;

    @Column(name = "NOMBRE", nullable = false)
    private String nombre;

    @Column(name = "ESTADO", nullable = false)
    private Integer estado;

    @OneToMany(mappedBy = "encuesta", cascade = CascadeType.ALL)
    private List<EncuestaPregunta> encuestaPreguntas;
}
