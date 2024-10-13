package co.edu.unicauca.sed.api.model;

import java.util.List;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "ACTIVIDAD", schema = "SEDOCENTE")
@Data
public class Actividad {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "actividadSeq")
    @SequenceGenerator(name = "actividadSeq", sequenceName = "SEQ_OIDACTIVIDAD", allocationSize = 1)
    @Column(name = "OIDACTIVIDAD")
    private Integer oidActividad;

    @ManyToOne
    @JoinColumn(name = "OIDTIPOACTIVIDAD", nullable = false)
    private TipoActividad tipoActividad;

    @ManyToOne
    @JoinColumn(name = "OIDESTADOACTIVIDAD", nullable = false)
    private EstadoActividad estadoActividad;

    @ManyToOne
    @JoinColumn(name = "OIDPROCESO", nullable = false)
    private Proceso proceso;

    @Column(name = "CODIGOACTIVIDAD", nullable = false)
    private String codigoActividad;

    @Column(name = "NOMBRE", nullable = false)
    private String nombre;

    @Column(name = "HORAS", nullable = false)
    private String horas;

    @Column(name = "ESTUDIANTE")
    private String estudiante;

    @OneToMany(mappedBy = "actividad", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Fuente> fuentes;
}
