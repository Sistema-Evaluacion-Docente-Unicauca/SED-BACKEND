package co.edu.unicauca.sed.api.enums;

import co.edu.unicauca.sed.api.dto.actividad.*;
import co.edu.unicauca.sed.api.model.*;

import java.util.HashMap;
import java.util.Map;

public enum TipoActividadEnum {
    TRABAJO_DOCENCIA(2, "TRABAJO DE DOCENCIA", TrabajoDocenciaDetalleDTO.class, TrabajoDocenciaDetalle.class),
    PROYECTO_INVESTIGACION(3, "PROYECTO DE INVESTIGACIÓN", ProyectoInvestigacionDetalleDTO.class, ProyectoInvestigacionDetalle.class),
    CAPACITACION(4, "CAPACITACIÓN", CapacitacionDetalleDTO.class, CapacitacionDetalle.class),
    ADMINISTRACION(5, "ADMINISTRACIÓN", AdministracionDetalleDTO.class, AdministracionDetalle.class),
    OTRO_SERVICIO(6, "OTRO SERVICIO", OtroServicioDetalleDTO.class, OtroServicioDetalle.class),
    EXTENSION(7, "EXTENSIÓN", ExtensionDetalleDTO.class, ExtensionDetalle.class),
    TRABAJO_INVESTIGACION(8, "TRABAJO DE INVESTIGACIÓN", TrabajoInvestigacionDetalleDTO.class, TrabajoInvestigacionDetalle.class);

    private static final Map<String, TipoActividadEnum> NAME_MAP = new HashMap<>();
    private static final Map<Integer, TipoActividadEnum> OID_MAP = new HashMap<>();

    static {
        for (TipoActividadEnum tipo : values()) {
            NAME_MAP.put(tipo.nombre, tipo);
            OID_MAP.put(tipo.oid, tipo);
        }
    }

    private final int oid;
    private final String nombre;
    private final Class<?> dtoClass;
    private final Class<?> entityClass;

    TipoActividadEnum(int oid, String nombre, Class<?> dtoClass, Class<?> entityClass) {
        this.oid = oid;
        this.nombre = nombre;
        this.dtoClass = dtoClass;
        this.entityClass = entityClass;
    }

    public Class<?> getDtoClass() {
        return dtoClass;
    }

    public Class<?> getEntityClass() {
        return entityClass;
    }

    public static TipoActividadEnum fromNombre(String nombre) {
        return NAME_MAP.getOrDefault(nombre, null);
    }

    public static TipoActividadEnum fromOid(int oid) {
        return OID_MAP.getOrDefault(oid, null);
    }
}
