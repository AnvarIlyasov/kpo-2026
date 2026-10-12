package domain;

/** Common immutable identity for objects on the balance sheet. */
public abstract class Asset implements Inventoriable {
    private final String name;
    private final String inventoryNumber;

    protected Asset(String name, String inventoryNumber) {
        this.name = requireText(name, "Название");
        this.inventoryNumber = requireText(inventoryNumber, "Инвентарный номер");
    }

    private static String requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " не может быть пустым");
        }
        return value.strip();
    }

    @Override
    public final String name() {
        return name;
    }

    @Override
    public final String inventoryNumber() {
        return inventoryNumber;
    }
}
