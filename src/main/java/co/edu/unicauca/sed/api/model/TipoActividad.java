package co.edu.unicauca.sed.api.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "TIPOACTIVIDAD", schema = "SEDOCENTE")
@Data
public class TipoActividad {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tipoActividadSeq")
    @SequenceGenerator(name = "tipoActividadSeq", sequenceName = "SEQ_OIDTIPOACTIVIDAD", allocationSize = 1)
    @Column(name = "OIDTIPOACTIVIDAD")
    private Integer oidTipoActividad;

    @Column(name = "NOMBRE", nullable = false)
    private String nombre;

    @Column(name = "HORASTOTALES", nullable = false)
    private Float horasTotales;

    @Column(name = "DESCRIPCION", nullable = false)
    private String descripcion;
}
