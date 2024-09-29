package co.edu.unicauca.sed.api.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "ROL", schema = "ACADEMICO")
@Data
public class Rol {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "rolSeq")
    @SequenceGenerator(name = "rolSeq", sequenceName = "SEQ_OIDROL", allocationSize = 1)
    @Column(name = "OIDROL")
    private Integer oid;

    @Column(name = "NOMBRE")
    private String nombre;

    @Column(name = "ESTADO")
    private Integer estado;

    @ManyToMany(mappedBy = "roles")
    @JsonIgnore
    private List<Usuario> usuarios;
}
