package domain;

import java.math.BigDecimal;

public final class ChargingStation extends InventoryItem implements EnergyConsumer {
    private final BigDecimal dailyEnergyKwh;

    public ChargingStation(String name, String number, BigDecimal energy) {
        super(name, number);
        dailyEnergyKwh = EnergyValidation.requireValid(energy);
    }

    @Override
    public BigDecimal dailyEnergyKwh() {
        return dailyEnergyKwh;
    }
}
