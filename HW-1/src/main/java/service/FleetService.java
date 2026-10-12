package service;

import domain.EnergyConsumer;
import domain.Inventoriable;
import domain.InventoryItem;
import domain.Transport;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/** In-memory balance sheet; transport can enter only through inspection. */
public final class FleetService {
    private final ServiceCenter serviceCenter;
    private final Map<String, Inventoriable> assets = new LinkedHashMap<>();

    public FleetService(ServiceCenter serviceCenter) {
        this.serviceCenter = Objects.requireNonNull(serviceCenter);
    }

    public InspectionResult acceptTransport(Transport transport) {
        requireUnique(transport);
        InspectionResult result = Objects.requireNonNull(serviceCenter.inspect(transport));
        if (result.accepted()) {
            assets.put(transport.inventoryNumber(), transport);
        }
        return result;
    }

    public void addItem(InventoryItem item) {
        requireUnique(item);
        assets.put(item.inventoryNumber(), item);
    }

    private void requireUnique(Inventoriable asset) {
        Objects.requireNonNull(asset);
        if (assets.containsKey(asset.inventoryNumber())) {
            throw new IllegalArgumentException(
                    "Инвентарный номер уже занят: " + asset.inventoryNumber());
        }
    }

    public List<Inventoriable> inventory() {
        return List.copyOf(assets.values());
    }

    public long transportCount() {
        return assets.values().stream().filter(Transport.class::isInstance).count();
    }

    public long itemCount() {
        return assets.size() - transportCount();
    }

    public BigDecimal totalDailyEnergyKwh() {
        return assets.values().stream()
                .filter(EnergyConsumer.class::isInstance)
                .map(EnergyConsumer.class::cast)
                .map(EnergyConsumer::dailyEnergyKwh)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public List<Transport> beginnerTransport() {
        return assets.values().stream()
                .filter(Transport.class::isInstance)
                .map(Transport.class::cast)
                .filter(t -> t.beginnerEase() >= 6)
                .toList();
    }
}
