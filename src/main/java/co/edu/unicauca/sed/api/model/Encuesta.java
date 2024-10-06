package co.edu.unicauca.sed.api.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

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
    private Short estado;

    @ManyToMany(mappedBy = "encuestas")
    @JsonIgnore
    private List<EvaluacionEstudiante> encuestas;
}
