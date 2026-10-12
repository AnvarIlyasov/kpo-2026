package domain;

import java.math.BigDecimal;

public final class ElectricScooter extends ElectricTransport {
    public ElectricScooter(
            String name, String number, int ease, boolean faulty, BigDecimal energy) {
        super(name, number, ease, faulty, energy);
    }
}
