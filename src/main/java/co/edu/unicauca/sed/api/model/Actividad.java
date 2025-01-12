package co.edu.unicauca.sed.api.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "ACTIVIDAD")
@Data
public class Actividad {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "actividadSeq")
    @SequenceGenerator(name = "actividadSeq", sequenceName = "SEQ_OIDACTIVIDAD", allocationSize = 1)
    @Column(name = "OIDACTIVIDAD")
    private Integer oidActividad;

    @Column(name = "CODIGOACTIVIDAD")
    private String codigoActividad;

    @Column(name = "NOMBRE", nullable = false)
    private String nombre;

    @Column(name = "HORASTOTALES", nullable = false)
    private Float horasTotales;

    @Column(name = "INFORMEEJECUTIVO", nullable = false)
    private Boolean informeEjecutivo;

    @Column(name = "CODVRI")
    private String codVRI;

    @Column(name = "ESTADOACTIVIDAD", nullable = false)
    private Short estadoActividad;

    @Column(name = "ACTOADMINISTRATIVO")
    private String actoAdministrativo;

    @Column(name = "FECHACREACION", updatable = false)
    @CreationTimestamp
    private LocalDateTime fechaCreacion;

    @Column(name = "FECHAACTUALIZACION")
    @UpdateTimestamp
    private LocalDateTime fechaActualizacion;

    @ManyToOne
    @JoinColumn(name = "OIDTIPOACTIVIDAD", nullable = false)
    private TipoActividad tipoActividad;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "OIDPROCESO", nullable = false)
    @JsonProperty("proceso")
    private Proceso proceso;

    @OneToMany(mappedBy = "actividad", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonIgnore
    private List<Fuente> fuentes;
}
