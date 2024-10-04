package co.edu.unicauca.sed.api.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "ESTADOACTIVIDAD", schema = "SEDOCENTE")
@Data
public class EstadoActividad {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "estadoActividadSeq")
    @SequenceGenerator(name = "estadoActividadSeq", sequenceName = "SEQ_OIDESTADOACTIVIDAD", allocationSize = 1)
    @Column(name = "OIDESTADOACTIVIDAD")
    private Integer oidEstadoActividad;

    @Column(name = "NOMBREESTADO", nullable = false)
    private String nombreEstado;
}
