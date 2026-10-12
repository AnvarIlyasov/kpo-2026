package console;

import domain.Bicycle;
import domain.ChargingStation;
import domain.ElectricBicycle;
import domain.ElectricScooter;
import domain.Helmet;
import domain.Inventoriable;
import domain.InventoryItem;
import domain.Transport;

import service.FleetService;
import service.InspectionResult;

import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

public final class ConsoleApp {
    private final FleetService fleet;
    private final Scanner input;
    private final PrintStream output;

    public ConsoleApp(FleetService fleet, Scanner input, PrintStream output) {
        this.fleet = fleet;
        this.input = input;
        this.output = output;
    }

    public void run() {
        output.println("ВышКат — Доедем до дедлайна! Данные хранятся до выхода.");
        while (true) {
            output.println("1 — Добавить транспорт | 2 — Добавить вещь | 3 — Отчёт");
            output.println("4 — Транспорт для новичков | 5 — Всё имущество | 0 — Выход");
            try {
                switch (read("Команда")) {
                    case "1" -> addTransport();
                    case "2" -> addItem();
                    case "3" -> report();
                    case "4" -> printAssets(fleet.beginnerTransport());
                    case "5" -> printAssets(fleet.inventory());
                    case "0" -> {
                        output.println("До свидания!");
                        return;
                    }
                    default -> output.println("Неизвестная команда");
                }
            } catch (IllegalArgumentException exception) {
                output.println(
                        "Ошибка: "
                                + exception.getMessage()
                                + ". Операция отменена, повторите через меню.");
            } catch (NoSuchElementException exception) {
                output.println("Ввод завершён. До свидания!");
                return;
            }
        }
    }

    private void addTransport() {
        String type = read("Вид: 1 — электросамокат, 2 — велосипед, 3 — электровелосипед");
        if (!List.of("1", "2", "3").contains(type)) {
            throw new IllegalArgumentException("Неизвестный вид транспорта");
        }
        String name = read("Название");
        String number = read("Инвентарный номер");
        int ease = integer(read("Простота для новичка (1–10)"));
        boolean faulty = fault(read("Есть неисправность? (да/нет)"));
        Transport transport =
                switch (type) {
                    case "1" -> new ElectricScooter(name, number, ease, faulty, energy());
                    case "2" -> new Bicycle(name, number, ease, faulty);
                    default -> new ElectricBicycle(name, number, ease, faulty, energy());
                };
        InspectionResult result = fleet.acceptTransport(transport);
        output.println((result.accepted() ? "Принято: " : "Отклонено: ") + result.reason());
    }

    private void addItem() {
        String type = read("Вид: 1 — шлем, 2 — зарядная станция");
        if (!List.of("1", "2").contains(type)) {
            throw new IllegalArgumentException("Неизвестный вид вещи");
        }
        String name = read("Название");
        String number = read("Инвентарный номер");
        InventoryItem item =
                type.equals("1")
                        ? new Helmet(name, number)
                        : new ChargingStation(name, number, energy());
        fleet.addItem(item);
        output.println("Вещь добавлена");
    }

    private void report() {
        output.printf(
                "Транспорт: %d; вещи: %d; всего: %d%n",
                fleet.transportCount(), fleet.itemCount(), fleet.inventory().size());
        output.println(
                "Суточная энергия: " + fleet.totalDailyEnergyKwh().toPlainString() + " кВт·ч");
    }

    private void printAssets(List<? extends Inventoriable> assets) {
        if (assets.isEmpty()) {
            output.println("Список пуст");
        }
        assets.forEach(asset -> output.println(asset.name() + " | " + asset.inventoryNumber()));
    }

    private String read(String prompt) {
        output.print(prompt + ": ");
        output.flush();
        return input.nextLine().strip();
    }

    private int integer(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Ожидается целое число");
        }
    }

    private BigDecimal energy() {
        try {
            return new BigDecimal(read("Суточный расход (кВт·ч)").replace(',', '.'));
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Ожидается десятичное число");
        }
    }

    private boolean fault(String value) {
        if (value.equalsIgnoreCase("да")) {
            return true;
        }
        if (value.equalsIgnoreCase("нет")) {
            return false;
        }
        throw new IllegalArgumentException("Введите да или нет");
    }
}
