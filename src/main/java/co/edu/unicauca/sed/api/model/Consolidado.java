package co.edu.unicauca.sed.api.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "CONSOLIDADO", schema = "SEDOCENTE")
@Data
public class Consolidado {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "consolidadoSeq")
    @SequenceGenerator(name = "consolidadoSeq", sequenceName = "SEQ_OIDCONSOLIDADO", allocationSize = 1)
    @Column(name = "OIDCONSOLIDADO")
    private Integer oidConsolidado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OIDPROCESO")
    @JsonBackReference
    private Proceso proceso;

    @Column(name = "NOMBREDOCUMENTO", nullable = false)
    private String nombredocumento;

    @Column(name = "FECHACREACION", updatable = false)
    @CreationTimestamp
    private LocalDateTime fechaCreacion;

    @Column(name = "FECHAACTUALIZACION")
    @UpdateTimestamp
    private LocalDateTime fechaActualizacion;
}
