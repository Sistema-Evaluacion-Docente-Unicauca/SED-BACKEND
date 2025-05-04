package co.edu.unicauca.sed.api.service.periodoAcademico;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import co.edu.unicauca.sed.api.controller.EstadoPeriodoAcademicoController;
import co.edu.unicauca.sed.api.domain.EstadoPeriodoAcademico;
import co.edu.unicauca.sed.api.dto.ApiResponse;
import co.edu.unicauca.sed.api.service.periodo_academico.EstadoPeriodoAcademicoService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.List;

@SpringBootTest
public class EstadoPeriodoAcademicoTest {

    @Autowired
    private EstadoPeriodoAcademicoController controller;

    @MockBean
    private EstadoPeriodoAcademicoService service;

    private EstadoPeriodoAcademico estado;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        estado = new EstadoPeriodoAcademico();
        estado.setOidEstadoPeriodoAcademico(1);
        estado.setNombre("ACTIVO");
    }

    @Test
    void crearEstadoPeriodoAcademicoConExito() {
        when(service.guardar(any(EstadoPeriodoAcademico.class)))
            .thenReturn(new ApiResponse<>(201, "Estado de período académico guardado correctamente.", estado));

        ResponseEntity<ApiResponse<EstadoPeriodoAcademico>> respuesta = controller.create(estado);

        assertThat(respuesta.getStatusCodeValue()).isEqualTo(200);
        assertThat(respuesta.getBody()).isNotNull();
        assertThat(respuesta.getBody().getCodigo()).isEqualTo(201);
        assertThat(respuesta.getBody().getData().getNombre()).isEqualTo("ACTIVO");
    }

    @Test
    void listarTodosLosEstadosPeriodoAcademico() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<EstadoPeriodoAcademico> page = new PageImpl<>(List.of(estado));
        when(service.buscarTodos(pageable)).thenReturn(
            new ApiResponse<>(200, "Estados de períodos académicos obtenidos correctamente.", page)
        );

        ResponseEntity<ApiResponse<Page<EstadoPeriodoAcademico>>> respuesta = controller.findAll(pageable);

        assertThat(respuesta.getStatusCodeValue()).isEqualTo(200);
        assertThat(respuesta.getBody().getCodigo()).isEqualTo(200);
        assertThat(respuesta.getBody().getData().getContent()).hasSize(1);
    }

    @Test
    void listarEstadosPeriodoAcademicoVacio() {
        Pageable pageable = PageRequest.of(0, 10);
        when(service.buscarTodos(pageable)).thenReturn(
            new ApiResponse<>(204, "No se encontraron estados de períodos académicos.", Page.empty())
        );

        ResponseEntity<ApiResponse<Page<EstadoPeriodoAcademico>>> respuesta = controller.findAll(pageable);

        assertThat(respuesta.getStatusCodeValue()).isEqualTo(200);
        assertThat(respuesta.getBody().getCodigo()).isEqualTo(204);
        assertThat(respuesta.getBody().getData().getContent()).isEmpty();
    }

    @Test
    void buscarEstadoPeriodoAcademicoPorId() {
        when(service.buscarPorId(1)).thenReturn(
            new ApiResponse<>(200, "Estado encontrado", estado)
        );

        ResponseEntity<ApiResponse<EstadoPeriodoAcademico>> respuesta = controller.findById(1);

        assertThat(respuesta.getStatusCodeValue()).isEqualTo(200);
        assertThat(respuesta.getBody().getCodigo()).isEqualTo(200);
        assertThat(respuesta.getBody().getData().getNombre()).isEqualTo("ACTIVO");
    }

    @Test
    void actualizarEstadoPeriodoAcademicoConExito() {
        estado.setNombre("INACTIVO");

        when(service.actualizar(1, estado)).thenReturn(
            new ApiResponse<>(200, "Actualizado correctamente", estado)
        );

        ResponseEntity<ApiResponse<EstadoPeriodoAcademico>> respuesta = controller.update(1, estado);

        assertThat(respuesta.getStatusCodeValue()).isEqualTo(200);
        assertThat(respuesta.getBody().getCodigo()).isEqualTo(200);
        assertThat(respuesta.getBody().getMensaje()).contains("Actualizado");
        assertThat(respuesta.getBody().getData().getNombre()).isEqualTo("INACTIVO");
    }

    @Test
    void actualizarEstadoPeriodoAcademico() {
        estado.setNombre("ACTIVO");

        when(service.actualizar(1, estado)).thenReturn(
            new ApiResponse<>(200, "Actualizado correctamente", estado)
        );

        ResponseEntity<ApiResponse<EstadoPeriodoAcademico>> respuesta = controller.update(1, estado);

        assertThat(respuesta.getStatusCodeValue()).isEqualTo(200);
        assertThat(respuesta.getBody().getCodigo()).isEqualTo(200);
        assertThat(respuesta.getBody().getMensaje()).contains("Actualizado");
        assertThat(respuesta.getBody().getData().getNombre()).isEqualTo("ACTIVO");
    }
}
