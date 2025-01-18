package co.edu.unicauca.sed.api.model;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Data
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "ESTADOPERIODOACADEMICO", schema = "SEDOCENTE")
public class EstadoPeriodoAcademico {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "estadoPeriodoAcademicoSeq")
    @SequenceGenerator(name = "estadoPeriodoAcademicoSeq", sequenceName = "SEQ_OIDESTADOPERIODOACADEMICO", allocationSize = 1)
    @Column(name = "OIDESTADOPERIODOACADEMICO", nullable = false)
    private Integer oidEstadoPeriodoAcademico;

    @Column(name = "NOMBRE", nullable = false)
    private String nombre;

    @Column(name = "FECHACREACION", updatable = false, nullable = false)
    @CreatedDate
    private LocalDateTime fechaCreacion;

    @Column(name = "FECHAACTUALIZACION", nullable = false)
    @LastModifiedDate
    private LocalDateTime fechaActualizacion;
}
