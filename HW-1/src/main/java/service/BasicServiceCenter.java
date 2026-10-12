package service;

import domain.Transport;

import java.util.Objects;

public final class BasicServiceCenter implements ServiceCenter {
    @Override
    public InspectionResult inspect(Transport transport) {
        Objects.requireNonNull(transport);
        return transport.faulty()
                ? new InspectionResult(false, "Обнаружена неисправность")
                : new InspectionResult(true, "Техосмотр пройден");
    }
}
