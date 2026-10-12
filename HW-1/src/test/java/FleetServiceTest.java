import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import domain.Bicycle;
import domain.ChargingStation;
import domain.ElectricBicycle;
import domain.ElectricScooter;
import domain.Helmet;
import domain.Transport;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import service.BasicServiceCenter;
import service.FleetService;
import service.InspectionResult;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicInteger;

class FleetServiceTest {
    private final FleetService fleet = new FleetService(new BasicServiceCenter());

    @Test
    void inspectionDecisionControlsAdmissionAndRejectedNumberCanBeReused() {
        AtomicInteger calls = new AtomicInteger();
        FleetService controlled =
                new FleetService(
                        t -> new InspectionResult(calls.incrementAndGet() > 1, "Решение fake"));
        Bicycle bicycle = new Bicycle("Bike", "B1", 6, false);
        assertFalse(controlled.acceptTransport(bicycle).accepted());
        assertTrue(controlled.inventory().isEmpty());
        assertTrue(controlled.acceptTransport(bicycle).accepted());
        assertEquals(1, controlled.transportCount());
        assertEquals(2, calls.get());
    }

    @Test
    void faultyTransportDoesNotAffectAnyReport() {
        assertFalse(
                fleet.acceptTransport(
                                new ElectricScooter("Bad", "S1", 10, true, new BigDecimal("9")))
                        .accepted());
        assertTrue(fleet.inventory().isEmpty());
        assertTrue(fleet.beginnerTransport().isEmpty());
        assertEquals(BigDecimal.ZERO, fleet.totalDailyEnergyKwh());
    }

    @ParameterizedTest
    @CsvSource({"1,false", "5,false", "6,true", "10,true"})
    void beginnerThreshold(int ease, boolean expected) {
        fleet.acceptTransport(new Bicycle("Bike", "B1", ease, false));
        assertEquals(expected ? 1 : 0, fleet.beginnerTransport().size());
    }

    @Test
    void countsAndEnergyIncludeItemsAndAllElectricTransport() {
        fleet.acceptTransport(new Bicycle("Bike", "B1", 5, false));
        fleet.acceptTransport(
                new ElectricScooter("Scooter", "S1", 6, false, new BigDecimal("1.2")));
        fleet.acceptTransport(new ElectricBicycle("E-bike", "E1", 7, false, new BigDecimal("0.3")));
        fleet.addItem(new Helmet("Helmet", "H1"));
        fleet.addItem(new ChargingStation("Station", "C1", new BigDecimal("2.1")));
        assertEquals(3, fleet.transportCount());
        assertEquals(2, fleet.itemCount());
        assertEquals(new BigDecimal("3.6"), fleet.totalDailyEnergyKwh());
        assertEquals(2, fleet.beginnerTransport().size());
        assertEquals(5, fleet.inventory().size());
        assertEquals("Bike", fleet.inventory().get(0).name());
    }

    @Test
    void emptyFleetReportsZero() {
        assertEquals(0, fleet.transportCount());
        assertEquals(0, fleet.itemCount());
        assertEquals(BigDecimal.ZERO, fleet.totalDailyEnergyKwh());
    }

    @Test
    void duplicateNumbersAcrossCategoriesAreRejectedBeforeInspection() {
        FleetService controlled =
                new FleetService(
                        t -> {
                            fail("Must not inspect duplicate");
                            return null;
                        });
        controlled.addItem(new Helmet("Helmet", " X "));
        assertThrows(
                IllegalArgumentException.class,
                () -> controlled.acceptTransport(new Bicycle("Bike", "X", 6, false)));
        assertThrows(
                IllegalArgumentException.class, () -> controlled.addItem(new Helmet("Other", "X")));
        assertEquals(1, controlled.inventory().size());
    }

    @Test
    void itemCannotReplaceTransportAndReportsCannotMutateFleet() {
        fleet.acceptTransport(new Bicycle("Bike", "B1", 6, false));
        assertThrows(
                IllegalArgumentException.class, () -> fleet.addItem(new Helmet("Helmet", "B1")));
        assertThrows(UnsupportedOperationException.class, () -> fleet.inventory().clear());
        assertThrows(UnsupportedOperationException.class, () -> fleet.beginnerTransport().clear());
    }

    @Test
    void newTransportSubtypeNeedsNoFleetChanges() {
        class Tandem extends Transport {
            Tandem() {
                super("Tandem", "T1", 7, false);
            }
        }
        assertTrue(fleet.acceptTransport(new Tandem()).accepted());
        assertEquals(1, fleet.beginnerTransport().size());
    }

    @Test
    void inspectionFailureLeavesFleetUnchanged() {
        FleetService broken =
                new FleetService(
                        t -> {
                            throw new IllegalStateException("Unavailable");
                        });
        assertThrows(
                IllegalStateException.class,
                () -> broken.acceptTransport(new Bicycle("Bike", "B1", 6, false)));
        assertTrue(broken.inventory().isEmpty());
    }
}
