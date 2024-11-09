package co.edu.unicauca.sed.api.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "FUENTE")
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

    @Column(name = "RUTADOCUMENTO", nullable = false)
    private String rutaDocumento;

    @Column(name = "OBSERVACION")
    private String observacion;

    @CreationTimestamp
    @Column(name = "FECHACREACION", nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "FECHAACTUALIZACION")
    @UpdateTimestamp
    private LocalDateTime fechaActualizacion;

    @ManyToOne
    @JoinColumn(name = "OIDACTIVIDAD", nullable = false)
    @JsonIgnore
    private Actividad actividad;

    @ManyToOne
    @JoinColumn(name = "OIDESTADOFUENTE", nullable = false)
    @JsonIgnore
    private EstadoFuente estadoFuente;
}
