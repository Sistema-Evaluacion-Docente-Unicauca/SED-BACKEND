package co.edu.unicauca.sed.api.enums;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public enum CategoriaEnum {
    ASOCIADO("ASOCIADO"),
    TITULAR("TITULAR");

    private final String nombre;

    CategoriaEnum(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    public static List<Map<String, String>> getSelectOptions() {
        return Arrays.stream(CategoriaEnum.values())
            .map(categoria -> Map.of("codigo", categoria.getNombre(), "nombre", categoria.getNombre()))
            .collect(Collectors.toList());
    }
}
