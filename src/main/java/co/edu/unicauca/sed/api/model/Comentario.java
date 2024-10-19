package co.edu.unicauca.sed.api.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "COMENTARIO", schema = "SEDOCENTE")
@Data
public class Comentario {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "comentarioSeq")
    @SequenceGenerator(name = "comentarioSeq", sequenceName = "SEQ_OIDCOMENTARIO", allocationSize = 1)
    @Column(name = "OIDCOMENTARIO", nullable = false)
    private Integer oidComentario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OIDPROCESO")
    @JsonBackReference
    private Proceso proceso;

    @Column(name = "COMENTARIO", nullable = false)
    private String comentario;

    @Column(name = "FECHACREACION", updatable = false)
    @CreationTimestamp
    private LocalDateTime fechaCreacion;
}
