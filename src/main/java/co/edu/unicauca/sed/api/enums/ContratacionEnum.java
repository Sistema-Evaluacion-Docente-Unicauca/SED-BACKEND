package co.edu.unicauca.sed.api.enums;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public enum ContratacionEnum {
    PLANTA("PLANTA"),
    OCASIONAL("OCASIONAL"),
    BECARIOS_Y_PRACTICANTES("BECARIOS Y PRACTICANTES"),
    CATEDRA("CÁTEDRA");

    private final String nombre;

    ContratacionEnum(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    public static List<Map<String, String>> getSelectOptions() {
        return Arrays.stream(ContratacionEnum.values())
            .map(contrato -> Map.of("codigo", contrato.getNombre(), "nombre", contrato.getNombre()))
            .collect(Collectors.toList());
    }
}
