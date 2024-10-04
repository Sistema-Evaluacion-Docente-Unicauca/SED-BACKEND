package co.edu.unicauca.sed.api.model;

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

    @Column(name = "CONSOLIDADO", nullable = false)
    private String consolidado;
}
