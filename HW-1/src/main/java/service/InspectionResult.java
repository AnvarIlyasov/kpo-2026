package service;

public record InspectionResult(boolean accepted, String reason) {
    public InspectionResult {
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("Укажите причину решения техосмотра");
        }
    }
}
