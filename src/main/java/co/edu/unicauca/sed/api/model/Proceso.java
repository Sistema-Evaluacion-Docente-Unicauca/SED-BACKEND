package co.edu.unicauca.sed.api.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "PROCESO", schema = "SEDOCENTE")
@Data
public class Proceso {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "procesoSeq")
    @SequenceGenerator(name = "procesoSeq", sequenceName = "SEQ_OIDPROCESO", allocationSize = 1)
    @Column(name = "OIDPROCESO")
    private Integer oidProceso;

    @ManyToOne
    @JoinColumn(name = "EVALUADOR_OID")
    private Usuario evaluador;

    @ManyToOne
    @JoinColumn(name = "EVALUADO_OID")
    private Usuario evaluado;

    @ManyToOne
    @JoinColumn(name = "OIDPERIODOACADEMICO")
    private PeriodoAcademico oidPeriodoAcademico;

    @Column(name = "NOMBREPROCESO")
    private String nombreProceso;

    @OneToMany(mappedBy = "proceso", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<Comentario> comentarios;

    @OneToMany(mappedBy = "proceso", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<Resolucion> resolucion;

    @OneToMany(mappedBy = "proceso", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<Oficio> oficio;

    @OneToMany(mappedBy = "proceso", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<Consolidado> consolidado;
}