package co.edu.unicauca.sed.api.enums;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public enum DedicacionEnum {
    TIEMPO_COMPLETO("TIEMPO COMPLETO"),
    MEDIO_TIEMPO("MEDIO TIEMPO"),
    HORAS_CATEDRA("HORAS CÁTEDRA");

    private final String nombre;

    DedicacionEnum(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    public static List<Map<String, String>> getSelectOptions() {
        return Arrays.stream(DedicacionEnum.values())
            .map(dedicacion -> Map.of("codigo", dedicacion.getNombre(), "nombre", dedicacion.getNombre()))
            .collect(Collectors.toList());
    }
}
