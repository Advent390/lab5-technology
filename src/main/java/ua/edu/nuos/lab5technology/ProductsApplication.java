package ua.edu.nuos.lab5technology;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class ProductsApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(
                ProductsApplication.class.getResource("main.fxml")));
        stage.setTitle("Каталог продукції");
        stage.setScene(new Scene(root, 900, 580));
        stage.show();
    }
}
