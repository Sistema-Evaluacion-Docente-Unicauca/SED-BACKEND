package co.edu.unicauca.sed.api.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Utilidades para operaciones matemáticas comunes.
 */
public class MathUtils {

  private MathUtils() {
  }

  /**
   * Redondea un número a un número específico de decimales.
   *
   * @param valor   Número a redondear.
   * @param digitos Número de decimales.
   * @return Número redondeado.
   */
  public static BigDecimal redondearDecimal(double valor, int digitos) {
    return BigDecimal.valueOf(valor).setScale(digitos, RoundingMode.HALF_UP);
  }

  /**
   * Calcula el porcentaje de una cantidad.
   *
   * @param parcial Cantidad parcial.
   * @param total   Cantidad total.
   * @return Porcentaje redondeado a 2 decimales.
   */
  public static double calcularPorcentaje(double parcial, double total) {
    if (total == 0) {
      return 0;
    }
    return BigDecimal.valueOf((parcial / total) * 100)
        .setScale(2, RoundingMode.HALF_UP)
        .doubleValue();
  }
}
