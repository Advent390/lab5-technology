package ua.edu.nuos.lab5technology.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ua.edu.nuos.lab5technology.io.AlertIO;

import java.io.IOException;

public class MainController {
    private static final String BASE = "/ua/edu/nuos/lab5technology/";

    private final AlertIO alertIO = new AlertIO();

    @FXML
    private void openProducts() {
        openWindow("product.fxml", "Продукція", 1120, 700);
    }

    @FXML
    private void openQueries() {
        openWindow("queries.fxml", "SQL-запити — з лабораторної №4", 1260, 780);
    }

    private void openWindow(String fxml, String title, double width, double height) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(BASE + fxml));
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root, width, height));
            stage.show();
        } catch (IOException e) {
            alertIO.showError("Не вдалося відкрити вікно: " + e.getMessage());
        }
    }
}
