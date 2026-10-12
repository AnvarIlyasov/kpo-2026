package domain;

import java.math.BigDecimal;

public final class ElectricBicycle extends ElectricTransport {
    public ElectricBicycle(
            String name, String number, int ease, boolean faulty, BigDecimal energy) {
        super(name, number, ease, faulty, energy);
    }
}
