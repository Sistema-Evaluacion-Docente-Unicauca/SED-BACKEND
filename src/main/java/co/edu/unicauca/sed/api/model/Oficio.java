package co.edu.unicauca.sed.api.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "OFICIO", schema = "SEDOCENTE")
@Data
public class Oficio {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "oficioSeq")
    @SequenceGenerator(name = "oficioSeq", sequenceName = "SEQ_OIDOFICIO", allocationSize = 1)
    @Column(name = "OIDOFICIO")
    private Integer oidOficio;

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
