package co.edu.unicauca.sed.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "FUENTE", schema = "SEDOCENTE")
@Data
public class Fuente {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "fuenteSeq")
    @SequenceGenerator(name = "fuenteSeq", sequenceName = "SEQ_OIDFUENTE", allocationSize = 1)
    @Column(name = "OIDFUENTE")
    private Integer oidFuente;

    @Column(name = "TIPOFUENTE")
    private String tipoFuente;

    @Column(name = "CALIFICACION")
    private Float calificacion;

    @ManyToOne
    @JoinColumn(name = "OIDACTIVIDAD", nullable = false)
    @JsonIgnore
    private Actividad actividad;
}
