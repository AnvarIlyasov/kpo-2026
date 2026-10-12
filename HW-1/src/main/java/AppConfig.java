import console.ConsoleApp;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import service.BasicServiceCenter;
import service.FleetService;
import service.ServiceCenter;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

@Configuration
public class AppConfig {
    @Bean
    public ServiceCenter serviceCenter() {
        return new BasicServiceCenter();
    }

    @Bean
    public FleetService fleetService(ServiceCenter serviceCenter) {
        return new FleetService(serviceCenter);
    }

    @Bean
    public ConsoleApp consoleApp(FleetService fleetService) {
        return new ConsoleApp(
                fleetService,
                new Scanner(System.in, StandardCharsets.UTF_8),
                new PrintStream(System.out, true, StandardCharsets.UTF_8));
    }
}
