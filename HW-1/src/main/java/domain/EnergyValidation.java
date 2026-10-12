package domain;

import java.math.BigDecimal;

/** Допустимый суточный расход: до миллиона кВт·ч с точностью до 0.001 кВт·ч. */
final class EnergyValidation {
    private static final BigDecimal MAX_DAILY_ENERGY = new BigDecimal("1000000");
    private static final int MAX_DECIMAL_PLACES = 3;

    private EnergyValidation() {}

    static BigDecimal requireValid(BigDecimal energy) {
        if (energy == null || energy.signum() < 0) {
            throw new IllegalArgumentException("Расход энергии должен быть неотрицательным");
        }
        if (energy.compareTo(MAX_DAILY_ENERGY) > 0) {
            throw new IllegalArgumentException(
                    "Расход энергии не должен превышать 1000000 кВт·ч в сутки");
        }
        BigDecimal normalized = energy.stripTrailingZeros();
        if (normalized.scale() > MAX_DECIMAL_PLACES) {
            throw new IllegalArgumentException(
                    "Расход энергии должен содержать не более 3 значащих знаков после запятой");
        }
        // A bounded, non-negative scale keeps addition and plain-text output safe.
        return normalized.scale() < 0 ? normalized.setScale(0) : normalized;
    }
}
