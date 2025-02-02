package co.edu.unicauca.sed.api.utils;

import org.springframework.stereotype.Component;

@Component
public class StringUtils {

    private StringUtils() {
    }

    public String safeToUpperCase(String value) {
        return (value != null && !value.isBlank()) ? value.toUpperCase() : value;
    }
}
