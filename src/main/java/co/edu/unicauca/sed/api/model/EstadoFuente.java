package co.edu.unicauca.sed.api.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "ESTADOFUENTE")
@Data
public class EstadoFuente {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "estadoFuenteSeq")
    @SequenceGenerator(name = "estadoFuenteSeq", sequenceName = "SEQ_OIDESTADOFUENTE", allocationSize = 1)
    @Column(name = "OIDESTADOFUENTE", nullable = false)
    private Integer oidEstadoFuente;

    @Column(name = "NOMBREESTADO", nullable = false)
    private String nombreEstado;
}
