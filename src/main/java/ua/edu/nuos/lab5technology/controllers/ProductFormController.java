package ua.edu.nuos.lab5technology.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import ua.edu.nuos.lab5technology.data.Product;
import ua.edu.nuos.lab5technology.data.ProductGroup;
import ua.edu.nuos.lab5technology.io.AlertIO;
import ua.edu.nuos.lab5technology.io.DateFormats;
import ua.edu.nuos.lab5technology.service.ProductGroupService;
import ua.edu.nuos.lab5technology.service.ProductService;

/** Одна й та сама форма для створення (product == null) і редагування продукту. */
public class ProductFormController {
    private final ProductService productService = new ProductService();
    private final ProductGroupService productGroupService = new ProductGroupService();
    private final AlertIO alertIO = new AlertIO();

    private Product product;
    private boolean saved;

    @FXML
    private Label titleLabel;
    @FXML
    private Label subtitleLabel;
    @FXML
    private TextField nameField;
    @FXML
    private ComboBox<ProductGroup> groupBox;
    @FXML
    private DatePicker releaseDatePicker;
    @FXML
    private TextArea descriptionArea;
    @FXML
    private Button saveButton;

    @FXML
    public void initialize() {
        releaseDatePicker.setConverter(DateFormats.converter());
        alertIO.guard(() -> groupBox.setItems(FXCollections.observableArrayList(productGroupService.getAll())));
        Platform.runLater(nameField::requestFocus);
    }

    /** Викликається після завантаження форми. null означає створення нового продукту. */
    public void setProduct(Product product) {
        this.product = product;
        if (product == null) {
            titleLabel.setText("Новий продукт");
            subtitleLabel.setText("Заповніть поля та натисніть «Додати»");
            saveButton.setText("Додати");
            return;
        }
        titleLabel.setText("Редагування продукту");
        subtitleLabel.setText("Змініть потрібні поля та натисніть «Зберегти»");
        saveButton.setText("Зберегти");
        nameField.setText(product.name());
        groupBox.setValue(product.group());
        releaseDatePicker.setValue(product.releaseDate());
        descriptionArea.setText(product.description());
    }

    public boolean isSaved() {
        return saved;
    }

    @FXML
    private void save() {
        boolean ok = alertIO.guard(() -> {
            if (product == null) {
                productService.create(nameField.getText(), groupBox.getValue(),
                        descriptionArea.getText(), releaseDatePicker.getValue());
            } else {
                productService.update(product.id(), nameField.getText(), groupBox.getValue(),
                        descriptionArea.getText(), releaseDatePicker.getValue());
            }
        });
        if (ok) {
            saved = true;
            close();
        }
    }

    @FXML
    private void cancel() {
        close();
    }

    private void close() {
        ((Stage) saveButton.getScene().getWindow()).close();
    }
}
