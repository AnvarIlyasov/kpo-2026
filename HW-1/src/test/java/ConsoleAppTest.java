import static org.junit.jupiter.api.Assertions.assertTrue;

import console.ConsoleApp;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import service.BasicServiceCenter;
import service.FleetService;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

class ConsoleAppTest {
    private String run(String input) {
        var bytes = new ByteArrayOutputStream();
        new ConsoleApp(
                        new FleetService(new BasicServiceCenter()),
                        new Scanner(input),
                        new PrintStream(bytes, true, StandardCharsets.UTF_8))
                .run();
        return bytes.toString(StandardCharsets.UTF_8);
    }

    @Test
    void interactiveScenarioCoversEveryAssetTypeAndReports() {
        String result =
                run(
                        String.join(
                                "\n",
                                "1",
                                "1",
                                "Scooter",
                                "S1",
                                "6",
                                "нет",
                                "1,2",
                                "1",
                                "2",
                                "Bike",
                                "B1",
                                "5",
                                "нет",
                                "1",
                                "3",
                                "E-bike",
                                "E1",
                                "7",
                                "нет",
                                "0.3",
                                "1",
                                "1",
                                "Broken",
                                "BAD",
                                "10",
                                "да",
                                "8",
                                "2",
                                "1",
                                "Helmet",
                                "H1",
                                "2",
                                "2",
                                "Station",
                                "C1",
                                "2.1",
                                "2",
                                "1",
                                "Duplicate",
                                "S1",
                                "3",
                                "4",
                                "5",
                                "0"));
        assertTrue(result.contains("Отклонено: Обнаружена неисправность"));
        assertTrue(result.contains("Инвентарный номер уже занят"));
        assertTrue(result.contains("Транспорт: 3; вещи: 2; всего: 5"));
        assertTrue(result.contains("Суточная энергия: 3.6"));
        assertTrue(result.contains("Helmet | H1"));
        assertTrue(result.contains("До свидания!"));
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                "1\n9",
                "2\n9",
                "1\n2\nBike\nB\nabc",
                "1\n2\nBike\nB\n6\nmaybe",
                "2\n2\nStation\nC\nabc",
                "1\n2\nBike\nB\n11\nнет",
                "2\n2\nStation\nC\n-1"
            })
    void invalidInputReturnsToMenu(String input) {
        String result = run(input + "\n0");
        assertTrue(result.contains("Ошибка:"));
        assertTrue(result.contains("До свидания!"));
    }

    @Test
    void emptyReportsUnknownCommandAndEndOfInputAreHandled() {
        String result = run("3\n4\n5\nunknown\n1\n2\nPartial");
        assertTrue(result.contains("всего: 0"));
        assertTrue(result.contains("Список пуст"));
        assertTrue(result.contains("Неизвестная команда"));
        assertTrue(result.contains("Ввод завершён"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"1e2147483647", "1e-2147483647", "1000000.001", "0.0001"})
    void excessiveEnergyDoesNotEnterInventoryAndReportStillWorks(String energy) {
        String result = run("2\n2\nStation\nC1\n" + energy + "\n3\n0");
        assertTrue(result.contains("Ошибка:"));
        assertTrue(result.contains("Транспорт: 0; вещи: 0; всего: 0"));
        assertTrue(result.contains("Суточная энергия: 0"));
        assertTrue(result.contains("До свидания!"));
    }
}
