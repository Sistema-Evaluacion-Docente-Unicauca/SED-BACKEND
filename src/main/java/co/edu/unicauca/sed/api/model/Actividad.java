package co.edu.unicauca.sed.api.model;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Data
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "ACTIVIDAD")
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
    @JoinColumn(name = "OIDPROCESO", nullable = false)
    private Proceso proceso;

    @OneToMany(mappedBy = "actividad", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonIgnore
    private List<Fuente> fuentes;

    @ManyToOne
    @JoinColumn(name = "OIDESTADOACTIVIDAD", nullable = false)
    private EstadoActividad estadoActividad;

    @Column(name = "NOMBREACTIVIDAD", nullable = false, length = 255)
    private String nombreActividad;

    @Column(name = "HORAS", nullable = false)
    private Float horas;

    @Column(name = "SEMANAS", nullable = false)
    private Float semanas;

    @Column(name = "INFORMEEJECUTIVO", nullable = false)
    private Boolean informeEjecutivo;

    @Column(name = "FECHACREACION", updatable = false, nullable = false)
    @CreatedDate
    private LocalDateTime fechaCreacion;

    @Column(name = "FECHAACTUALIZACION", nullable = false)
    @LastModifiedDate
    private LocalDateTime fechaActualizacion;
}
