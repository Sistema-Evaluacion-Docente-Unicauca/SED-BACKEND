package co.edu.unicauca.sed.api.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;

/**
 * Entidad que representa el estado consolidado.
 */
@Data
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "ESTADOCONSOLIDADO")
public class EstadoConsolidado {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "estadoConsolidadoSeq")
    @SequenceGenerator(name = "estadoConsolidadoSeq", sequenceName = "SEQ_OIDESTADOCONSOLIDADO", allocationSize = 1)
    @Column(name = "OIDESTADOCONSOLIDADO")
    private Integer oidEstadoConsolidado;

    @Column(name = "NOMBRE", nullable = false, length = 15)
    private String nombre;

    @Column(name = "FECHACREACION", updatable = false, nullable = false)
    @CreationTimestamp
    private LocalDateTime fechaCreacion;

    @Column(name = "FECHAACTUALIZACION", nullable = false)
    @UpdateTimestamp
    private LocalDateTime fechaActualizacion;
}
