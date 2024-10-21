package co.edu.unicauca.sed.api.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "RESOLUCION", schema = "SEDOCENTE")
@Data
public class Resolucion {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "resolucionSeq")
    @SequenceGenerator(name = "resolucionSeq", sequenceName = "SEQ_OIDRESOLUCION", allocationSize = 1)
    @Column(name = "OIDRESOLUCION")
    private Integer oidResolucion;

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
