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
@Table(name = "DOCENCIADETALLE")
public class DocenciaDetalle {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "docenciaDetalleSeq")
    @SequenceGenerator(name = "docenciaDetalleSeq", sequenceName = "SEQ_OIDDOCENCIADETALLE", allocationSize = 1)
    @Column(name = "OIDDOCENCIADETALLE")
    private Integer oidDocenciaDetalle;

    @OneToOne
    @JoinColumn(name = "OIDACTIVIDAD", nullable = false)
    private Actividad actividad;

    @Column(name = "CODIGO", nullable = false)
    private String codigo;

    @Column(name = "GRUPO", nullable = false)
    private String grupo;

    @Column(name = "MATERIA", nullable = false)
    private String materia;

    @Column(name = "FECHACREACION", updatable = false, nullable = false)
    @CreatedDate
    private LocalDateTime fechaCreacion;

    @Column(name = "FECHAACTUALIZACION", nullable = false)
    @LastModifiedDate
    private LocalDateTime fechaActualizacion;
}
