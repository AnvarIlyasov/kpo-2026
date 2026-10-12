import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import domain.Bicycle;
import domain.ChargingStation;
import domain.ElectricScooter;
import domain.Helmet;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import service.InspectionResult;

import java.math.BigDecimal;

class DomainTest {
    @ParameterizedTest
    @ValueSource(ints = {0, -1, 11})
    void easeMustBeWithinScale(int ease) {
        assertThrows(IllegalArgumentException.class, () -> new Bicycle("Bike", "B", ease, false));
    }

    @Test
    void identityMustBePresentAndIsTrimmed() {
        assertThrows(IllegalArgumentException.class, () -> new Helmet(" ", "H"));
        assertThrows(IllegalArgumentException.class, () -> new Helmet("Helmet", null));
        Helmet helmet = new Helmet(" Helmet ", " H ");
        assertEquals("Helmet", helmet.name());
        assertEquals("H", helmet.inventoryNumber());
    }

    @Test
    void energyMustBeNonNegative() {
        assertThrows(IllegalArgumentException.class, () -> new ChargingStation("C", "C", null));
        assertThrows(
                IllegalArgumentException.class,
                () -> new ChargingStation("C", "C", new BigDecimal("-1")));
        assertThrows(
                IllegalArgumentException.class,
                () -> new ElectricScooter("S", "S", 5, false, new BigDecimal("-0.1")));
        assertEquals(
                BigDecimal.ZERO, new ChargingStation("C", "C", BigDecimal.ZERO).dailyEnergyKwh());
    }

    @Test
    void inspectionMustExplainDecision() {
        assertThrows(IllegalArgumentException.class, () -> new InspectionResult(false, " "));
        assertThrows(IllegalArgumentException.class, () -> new InspectionResult(true, null));
    }

    @ParameterizedTest
    @ValueSource(strings = {"1e2147483647", "1e-2147483647", "1000000.001", "0.0001"})
    void excessiveEnergyOrPrecisionIsRejected(String value) {
        BigDecimal energy = new BigDecimal(value);
        assertThrows(
                IllegalArgumentException.class, () -> new ChargingStation("Station", "C", energy));
        assertThrows(
                IllegalArgumentException.class,
                () -> new ElectricScooter("Scooter", "S", 6, false, energy));
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "0.001", "1000000", "1.23000", "0e-2147483647", "1e3"})
    void validEnergyBoundariesAndTrailingZerosAreAccepted(String value) {
        BigDecimal energy = new BigDecimal(value);
        BigDecimal stored = new ChargingStation("Station", "C", energy).dailyEnergyKwh();
        assertEquals(0, energy.compareTo(stored));
        assertEquals(
                0,
                energy.compareTo(
                        new ElectricScooter("Scooter", "S", 6, false, energy).dailyEnergyKwh()));
    }
}
