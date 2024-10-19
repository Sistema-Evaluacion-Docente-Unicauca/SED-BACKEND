package co.edu.unicauca.sed.api.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "AUTENTICACION", schema = "SEDOCENTE")
@Data
public class Autenticacion {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "autenticacionSeq")
    @SequenceGenerator(name = "autenticacionSeq", sequenceName = "SEQ_OIDAUTENTICACION", allocationSize = 1)
    @Column(name = "OIDAUTENTICACION", nullable = false)
    private Integer oidAutenticacion;

    @Column(name = "OIDUSUARIO", nullable = false)
    private Integer oidUsuario;

    @Column(name = "NOMBRES", nullable = false)
    private String nombres;

    @Column(name = "APELLIDOS", nullable = false)
    private String apellidos;

    @Column(name = "CORREO", nullable = false)
    private String correo;

    @Column(name = "FECHACREACION", nullable = false)
    @CreationTimestamp
    private LocalDateTime fechaCreacion;

    @Column(name = "ULTIMOINGRESO", nullable = false)
    @UpdateTimestamp
    private LocalDateTime ultimoIngreso;
}
