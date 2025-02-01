package co.edu.unicauca.sed.api.enums;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public enum EstudiosEnum {
    MAESTRIA("MAESTRÍA"),
    DOCTORADO("DOCTORADO"),
    POSDOCTORADO("POSDOCTORADO"),
    ESPECIALIZACION("ESPECIALIZACIÓN");

    private final String nombre;

    EstudiosEnum(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    public static List<Map<String, String>> getSelectOptions() {
        return Arrays.stream(EstudiosEnum.values())
            .map(estudio -> Map.of("codigo", estudio.getNombre(), "nombre", estudio.getNombre()))
            .collect(Collectors.toList());
    }
}