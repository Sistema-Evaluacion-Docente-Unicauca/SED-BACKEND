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
    @Column(name = "OIDFUENTE", nullable = false)
    private Integer oidFuente;

    @Column(name = "TIPOFUENTE", nullable = false)
    private String tipoFuente;

    @Column(name = "CALIFICACION", nullable = false)
    private Float calificacion;

    @Column(name = "NOMBREDOCUMENTO", nullable = false)
    private String nombreDocumento;

    @Column(name = "OBSERVACION")
    private String observacion;

    @Column(name = "FECHACREACION")
    private String fechaCreacion;

    @Column(name = "FECHAACTUALIZACION")
    private String fechaActualizacion;

    @ManyToOne
    @JoinColumn(name = "OIDACTIVIDAD", nullable = false)
    @JsonIgnore
    private Actividad actividad;

    @ManyToOne
    @JoinColumn(name = "OIDESTADOFUENTE", nullable = false)
    @JsonIgnore
    private EstadoFuente oidestadofuente;
}
