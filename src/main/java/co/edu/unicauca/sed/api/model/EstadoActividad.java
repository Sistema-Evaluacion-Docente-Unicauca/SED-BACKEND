package co.edu.unicauca.sed.api.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "ESTADOACTIVIDAD")
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
