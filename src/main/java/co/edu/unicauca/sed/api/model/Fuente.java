package co.edu.unicauca.sed.api.model;

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

    @ManyToOne
    @JoinColumn(name = "OIDACTIVIDAD", nullable = false)
    private Actividad actividad;

    @Column(name = "CALIFICACION")
    private Float calificacion;

    @Column(name = "TIPOFUENTE", nullable = false)
    private String tipoFuente;

    @Column(name = "DOCUMENTOSOPORTE")
    private String documentoSoporte;

    @Column(name = "INFORMEEJECUTIVO")
    private String informeEjecutivo;
}
