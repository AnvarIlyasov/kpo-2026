import console.ConsoleApp;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public final class Main {
    private Main() {}

    public static void main(String[] args) {
        try (var context = new AnnotationConfigApplicationContext(AppConfig.class)) {
            context.getBean(ConsoleApp.class).run();
        }
    }
}
