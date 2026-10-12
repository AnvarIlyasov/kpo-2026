package domain;

import java.math.BigDecimal;

public abstract class ElectricTransport extends Transport implements EnergyConsumer {
    private final BigDecimal dailyEnergyKwh;

    protected ElectricTransport(
            String name, String number, int ease, boolean faulty, BigDecimal energy) {
        super(name, number, ease, faulty);
        this.dailyEnergyKwh = EnergyValidation.requireValid(energy);
    }

    @Override
    public final BigDecimal dailyEnergyKwh() {
        return dailyEnergyKwh;
    }
}
