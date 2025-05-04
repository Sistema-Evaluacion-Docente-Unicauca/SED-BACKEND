package co.edu.unicauca.sed.api.service.actividad;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.List;
import co.edu.unicauca.sed.api.domain.*;
import co.edu.unicauca.sed.api.dto.ApiResponse;
import co.edu.unicauca.sed.api.dto.AtributoDTO;
import co.edu.unicauca.sed.api.dto.actividad.ActividadBaseDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ActividadServiceImplTest {

    @Autowired
    private ActividadService actividadService;

    private ActividadBaseDTO construirActividadBase(Integer oidEvaluado, Integer oidTipoActividad) {
        ActividadBaseDTO dto = new ActividadBaseDTO();
        dto.setOidEvaluado(oidEvaluado);
        dto.setOidEvaluador(0);
        dto.setOidEstadoActividad(1);
        dto.setNombreActividad("DOCENCIA TEST");
        dto.setHoras(2.0f);
        dto.setSemanas(16.0f);
        dto.setInformeEjecutivo(false);
        dto.setTipoActividad(new TipoActividad() {{ setOidTipoActividad(oidTipoActividad); }});
        dto.setAtributos(List.of(
            new AtributoDTO("CODIGO", "M34874"),
            new AtributoDTO("GRUPO", "A"),
            new AtributoDTO("MATERIA", "Fundamentos de Tecnologías de la Información")
        ));
        return dto;
    }

    @Test
    void caso1_guardadoExitoso() {
        ApiResponse<List<Actividad>> respuesta = actividadService.guardar(
            List.of(construirActividadBase(57, 9))
        );

        assertThat(respuesta.getCodigo()).isEqualTo(201);
        assertThat(respuesta.getMensaje()).contains("guardadas");
        assertThat(respuesta.getData()).isNotEmpty();
    }

    @Test
    void caso2_evaluadoNoAsignado() {
        ActividadBaseDTO dto = construirActividadBase(null, 9);
    
        ApiResponse<List<Actividad>> respuesta = actividadService.guardar(List.of(dto));
    
        assertThat(respuesta.getCodigo()).isEqualTo(400);
        assertThat(respuesta.getMensaje()).contains("El id evaluado no puede ser nulo");
    }

    @Test
    void caso3_tipoActividadInvalido() {
        ActividadBaseDTO dto = construirActividadBase(57, 9999);

        ApiResponse<List<Actividad>> respuesta = actividadService.guardar(List.of(dto));

        assertThat(respuesta.getCodigo()).isEqualTo(400);
        assertThat(respuesta.getMensaje()).contains("tipo de actividad");
    }

    @Test
    void caso5_horasNegativas() {
        ActividadBaseDTO dto = construirActividadBase(57, 9);
        dto.setHoras(-2.0f);

        ApiResponse<List<Actividad>> respuesta = actividadService.guardar(List.of(dto));

        assertThat(respuesta.getCodigo()).isEqualTo(400);
        assertThat(respuesta.getMensaje()).contains("horas no puede ser nula o negativa");
    }

    @Test
    void caso6_semanasNulas() {
        ActividadBaseDTO dto = construirActividadBase(57, 9);
        dto.setSemanas(null);

        ApiResponse<List<Actividad>> respuesta = actividadService.guardar(List.of(dto));

        assertThat(respuesta.getCodigo()).isEqualTo(400);
        assertThat(respuesta.getMensaje()).contains("semanas no puede ser nula o negativa");
    }

}
