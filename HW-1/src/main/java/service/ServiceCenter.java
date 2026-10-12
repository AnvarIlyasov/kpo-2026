package service;

import domain.Transport;

@FunctionalInterface
public interface ServiceCenter {
    InspectionResult inspect(Transport transport);
}
