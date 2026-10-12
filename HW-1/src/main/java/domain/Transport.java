package domain;

public abstract class Transport extends Asset {
    private final int beginnerEase;
    private final boolean faulty;

    protected Transport(String name, String number, int beginnerEase, boolean faulty) {
        super(name, number);
        if (beginnerEase < 1 || beginnerEase > 10) {
            throw new IllegalArgumentException("Простота должна быть от 1 до 10");
        }
        this.beginnerEase = beginnerEase;
        this.faulty = faulty;
    }

    public final int beginnerEase() {
        return beginnerEase;
    }

    public final boolean faulty() {
        return faulty;
    }
}
