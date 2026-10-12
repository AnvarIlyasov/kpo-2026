package domain;

public abstract class InventoryItem extends Asset {
    protected InventoryItem(String name, String number) {
        super(name, number);
    }
}
