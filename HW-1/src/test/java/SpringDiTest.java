import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import console.ConsoleApp;

import domain.Bicycle;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import service.FleetService;
import service.InspectionResult;
import service.ServiceCenter;

class SpringDiTest {
    @Test
    void productionContextWiresApplication() {
        try (var context = new AnnotationConfigApplicationContext(AppConfig.class)) {
            assertNotNull(context.getBean(ConsoleApp.class));
            assertTrue(
                    context.getBean(FleetService.class)
                            .acceptTransport(new Bicycle("Bike", "B", 6, false))
                            .accepted());
        }
    }

    @Test
    void springInjectsReplacementInspectionIntoProductionFleetBean() {
        try (var context = new AnnotationConfigApplicationContext()) {
            context.registerBean(
                    "fakeServiceCenter",
                    ServiceCenter.class,
                    () -> transport -> new InspectionResult(false, "Отказ тестового центра"),
                    definition -> definition.setPrimary(true));
            context.register(AppConfig.class);
            context.refresh();
            FleetService fleet = context.getBean(FleetService.class);
            InspectionResult result = fleet.acceptTransport(new Bicycle("Bike", "B", 6, false));
            assertFalse(result.accepted());
            assertEquals("Отказ тестового центра", result.reason());
            assertTrue(fleet.inventory().isEmpty());
        }
    }
}
