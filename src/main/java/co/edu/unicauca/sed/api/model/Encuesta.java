package co.edu.unicauca.sed.api.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Table(name = "ENCUESTA", schema = "SEDOCENTE")
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
