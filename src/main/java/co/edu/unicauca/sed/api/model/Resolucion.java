package co.edu.unicauca.sed.api.model;

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

    @Column(name = "PROCESO", nullable = false)
    private String procesoName;
}
