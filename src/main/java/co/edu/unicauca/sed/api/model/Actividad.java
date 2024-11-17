package co.edu.unicauca.sed.api.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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

    @Column(name = "HORAS", nullable = false)
    private Float horas;

    @Column(name = "INFORMEEJECUTIVO", nullable = false)
    private Boolean informeEjecutivo;

    @Column(name = "FECHACREACION", updatable = false)
    @CreationTimestamp
    private LocalDateTime fechaCreacion;

    @Column(name = "FECHAACTUALIZACION")
    @UpdateTimestamp
    private LocalDateTime fechaActualizacion;

    @ManyToOne
    @JoinColumn(name = "OIDTIPOACTIVIDAD", nullable = false)
    private TipoActividad tipoActividad;

    @ManyToOne
    @JoinColumn(name = "OIDPROCESO", nullable = false)
    private Proceso proceso;

    @OneToMany(mappedBy = "actividad", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Fuente> fuentes;
}
