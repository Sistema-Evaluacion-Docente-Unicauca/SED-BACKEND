package co.edu.unicauca.sed.api.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Table(name = "ACTIVIDAD", schema = "SEDOCENTE")
@Data
public class Actividad {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "actividadSeq")
    @SequenceGenerator(name = "actividadSeq", sequenceName = "SEQ_OIDACTIVIDAD", allocationSize = 1)
    @Column(name = "OIDACTIVIDAD")
    private Integer oidActividad;

    @Column(name = "CODIGOACTIVIDAD")
    private String codigoActividad;

    @Column(name = "NOMBRE")
    private String nombre;

    @Column(name = "HORAS")
    private String horas;

    @Column(name = "ESTUDIANTE")
    private String estudiante;

    @ManyToOne
    @JoinColumn(name = "OIDTIPOACTIVIDAD", nullable = false)
    private TipoActividad tipoActividad;

    @ManyToOne
    @JoinColumn(name = "OIDESTADOACTIVIDAD", nullable = false)
    private EstadoActividad estadoActividad;

    @ManyToOne
    @JoinColumn(name = "OIDPROCESO", nullable = false)
    private Proceso proceso;

    @OneToMany(mappedBy = "actividad", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Fuente> fuentes;
}
