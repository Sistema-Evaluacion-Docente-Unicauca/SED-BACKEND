package co.edu.unicauca.sed.api.enums;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public enum FacultadEnum {
    INGENIERIA_ELECTRONICA_Y_TELECOMUNICACIONES("FACULTAD DE INGENIERÍA ELECTRÓNICA Y TELECOMUNICACIONES");

    private final String nombre;

    FacultadEnum(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    public static List<Map<String, String>> getSelectOptions() {
        return Arrays.stream(FacultadEnum.values())
            .map(facultad -> Map.of("codigo", facultad.getNombre(), "nombre", facultad.getNombre()))
            .collect(Collectors.toList());
    }
}
