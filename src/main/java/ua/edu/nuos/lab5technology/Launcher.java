package ua.edu.nuos.lab5technology;

import javafx.application.Application;

/**
 * Окремий стартовий клас без наслідування від Application.
 * Запускати треба саме його (з IDE або через mvn javafx:run): так уникаємо помилки
 * "JavaFX runtime components are missing".
 */
public class Launcher {
    public static void main(String[] args) {
        Application.launch(ProductsApplication.class, args);
    }
}
