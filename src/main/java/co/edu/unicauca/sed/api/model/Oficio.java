package co.edu.unicauca.sed.api.model;

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

    @Column(name = "OFICIO", nullable = false)
    private String oficio;
}
