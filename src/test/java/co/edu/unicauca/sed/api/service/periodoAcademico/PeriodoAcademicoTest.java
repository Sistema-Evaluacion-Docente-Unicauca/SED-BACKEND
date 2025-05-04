package co.edu.unicauca.sed.api.service.periodoAcademico;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import co.edu.unicauca.sed.api.domain.EstadoPeriodoAcademico;
import co.edu.unicauca.sed.api.domain.PeriodoAcademico;
import co.edu.unicauca.sed.api.dto.ApiResponse;
import co.edu.unicauca.sed.api.repository.EstadoPeriodoAcademicoRepository;
import co.edu.unicauca.sed.api.service.periodo_academico.EstadoPeriodoAcademicoService;
import co.edu.unicauca.sed.api.service.periodo_academico.PeriodoAcademicoService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class PeriodoAcademicoTest {

    @Autowired
    private PeriodoAcademicoService periodoAcademicoService;

    @Autowired
    private EstadoPeriodoAcademicoRepository estadoRepo;

    @Test
    void noDebePermitirGuardarDosPeriodosActivos() {
        EstadoPeriodoAcademico estado = new EstadoPeriodoAcademico();
        estado.setOidEstadoPeriodoAcademico(1);

        // Act - Intentar guardar otro periodo con estado ACTIVO
        PeriodoAcademico periodo2 = new PeriodoAcademico();
        periodo2.setIdPeriodo("2025 - 2");
        periodo2.setIdPeriodoApi(1000);
        periodo2.setFechaInicio(LocalDate.of(2025, 7, 1));
        periodo2.setFechaFin(LocalDate.of(2025, 12, 30));
        periodo2.setEstadoPeriodoAcademico(estado);

        ApiResponse<PeriodoAcademico> respuesta2 = periodoAcademicoService.guardar(periodo2);

        // Assert
        assertThat(respuesta2.getCodigo()).isEqualTo(400);
        assertThat(respuesta2.getMensaje()).contains("Ya existe un período académico activo");
    }

    @Test
    void guardarPeriodoAcademicoInactivo() {
        EstadoPeriodoAcademico estado = estadoRepo.findById(2)
        .orElseThrow(() -> new RuntimeException("Estado INACTIVO con ID 2 no encontrado"));

        PeriodoAcademico periodo = new PeriodoAcademico();
        periodo.setIdPeriodo("2024-02");
        periodo.setIdPeriodoApi(323);
        periodo.setFechaInicio(LocalDate.of(2024, 6, 1));
        periodo.setFechaFin(LocalDate.of(2024, 12, 7));
        periodo.setEstadoPeriodoAcademico(estado);

        ApiResponse<PeriodoAcademico> respuesta = periodoAcademicoService.guardar(periodo);

        assertThat(respuesta.getCodigo()).isEqualTo(201);
        assertThat(respuesta.getData()).isNotNull();
    }
}
