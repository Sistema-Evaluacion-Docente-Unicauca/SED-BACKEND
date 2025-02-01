package co.edu.unicauca.sed.api.enums;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public enum DepartamentoEnum {
    ELECTRONICA_INSTRUMENTACION_CONTROL("DEPARTAMENTO DE ELECTRÓNICA, INSTRUMENTACIÓN Y CONTROL"),
    TELEMATICA("DEPARTAMENTO DE TELEMATICA"),
    TELECOMUNICACIONES("DEPARTAMENTO DE TELECOMUNICACIONES"),
    SISTEMAS("DEPARTAMENTO DE SISTEMAS");

    private final String nombre;

    DepartamentoEnum(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    public static List<Map<String, String>> getSelectOptions() {
        return Arrays.stream(DepartamentoEnum.values())
            .map(depto -> Map.of("codigo", depto.getNombre(), "nombre", depto.getNombre()))
            .collect(Collectors.toList());
    }
}
