package co.edu.unicauca.sed.api.dto;

import lombok.Data;

/**
 * DTO para recibir y enviar datos de una Encuesta.
 */
@Data
public class EncuestaDTO {
    private String nombre;
    private Integer estado; 
}
