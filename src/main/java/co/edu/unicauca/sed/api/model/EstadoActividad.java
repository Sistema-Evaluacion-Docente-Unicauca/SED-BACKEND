package co.edu.unicauca.sed.api.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "ESTADOACTIVIDAD", schema = "SEDOCENTE")
public class EstadoActividad {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "estadoActividadSeq")
    @SequenceGenerator(name = "estadoActividadSeq", sequenceName = "SEQ_OIDESTADOACTIVIDAD", allocationSize = 1)
    @Column(name = "OIDESTADOACTIVIDAD", nullable = false)
    private Integer oidEstadoActividad;

    @Column(name = "NOMBRE")
    private String nombre;

    @Column(name = "FECHACREACION", updatable = false, nullable = false)
    @CreatedDate
    private LocalDateTime fechaCreacion;

    @Column(name = "FECHAACTUALIZACION", nullable = false)
    @LastModifiedDate
    private LocalDateTime fechaActualizacion;
}
