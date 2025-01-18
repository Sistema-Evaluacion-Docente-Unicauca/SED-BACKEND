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
@Table(name = "ESTADOUSUARIO", schema = "SEDOCENTE")
public class EstadoUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "estadoUsuarioSeq")
    @SequenceGenerator(name = "estadoUsuarioSeq", sequenceName = "SEQ_OIDESTADOUSUARIO", allocationSize = 1)
    @Column(name = "OIDESTADOUSUARIO", nullable = false)
    private Integer oidEstadoUsuario;

    @Column(name = "NOMBRE", nullable = false)
    private String nombre;

    @Column(name = "FECHACREACION", updatable = false, nullable = false)
    @CreatedDate
    private LocalDateTime fechaCreacion;

    @Column(name = "FECHAACTUALIZACION", nullable = false)
    @LastModifiedDate
    private LocalDateTime fechaActualizacion;
}
