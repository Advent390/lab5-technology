package ua.edu.nuos.lab5technology.controllers;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ua.edu.nuos.lab5technology.data.Product;
import ua.edu.nuos.lab5technology.io.AlertIO;
import ua.edu.nuos.lab5technology.io.DateFormats;
import ua.edu.nuos.lab5technology.service.ProductService;

import java.io.IOException;
import java.time.LocalDate;

public class ProductController {
    private final ProductService productService = new ProductService();
    private final AlertIO alertIO = new AlertIO();

    private final ObservableList<Product> products = FXCollections.observableArrayList();
    private final FilteredList<Product> filteredProducts = new FilteredList<>(products, product -> true);

    @FXML
    private TableView<Product> productTable;
    @FXML
    private TableColumn<Product, Number> idColumn;
    @FXML
    private TableColumn<Product, String> nameColumn;
    @FXML
    private TableColumn<Product, String> groupColumn;
    @FXML
    private TableColumn<Product, String> descriptionColumn;
    @FXML
    private TableColumn<Product, LocalDate> dateColumn;
    @FXML
    private TextField searchField;
    @FXML
    private Button editButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Label countLabel;

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(cell -> new ReadOnlyIntegerWrapper(cell.getValue().id()));
        nameColumn.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().name()));
        groupColumn.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().group().name()));
        descriptionColumn.setCellValueFactory(cell -> new ReadOnlyStringWrapper(
                cell.getValue().description() == null ? "" : cell.getValue().description()));
        dateColumn.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<LocalDate>(cell.getValue().releaseDate()));
        // дату зберігаємо як LocalDate (правильне сортування), а показуємо у форматі дд.мм.рррр
        dateColumn.setCellFactory(column -> new TableCell<Product, LocalDate>() {
            @Override
            protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : DateFormats.DISPLAY.format(item));
            }
        });
        // ширина стовпців у відсотках від ширини таблиці
        productTable.widthProperty().addListener((observable, oldWidth, newWidth) -> {
            double width = newWidth.doubleValue() - 20;   // запас під смугу прокрутки
            idColumn.setPrefWidth(width * 0.06);
            nameColumn.setPrefWidth(width * 0.22);
            groupColumn.setPrefWidth(width * 0.16);
            descriptionColumn.setPrefWidth(width * 0.38);
            dateColumn.setPrefWidth(width * 0.14);
        });

        // пошук + сортування по кліку на заголовок стовпця
        SortedList<Product> sortedProducts = new SortedList<>(filteredProducts);
        sortedProducts.comparatorProperty().bind(productTable.comparatorProperty());
        productTable.setItems(sortedProducts);

        searchField.textProperty().addListener((observable, oldText, newText) -> applyFilter(newText));
        filteredProducts.addListener((ListChangeListener<Product>) change -> updateCount());

        editButton.disableProperty().bind(productTable.getSelectionModel().selectedItemProperty().isNull());
        deleteButton.disableProperty().bind(productTable.getSelectionModel().selectedItemProperty().isNull());

        // подвійний клік по рядку відкриває форму редагування
        productTable.setRowFactory(table -> {
            TableRow<Product> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    editProduct();
                }
            });
            return row;
        });
        productTable.setPlaceholder(new Label("Продукції не знайдено"));

        Platform.runLater(() -> alertIO.guard(() -> reload(null)));
    }

    @FXML
    private void refresh() {
        Product selected = productTable.getSelectionModel().getSelectedItem();
        alertIO.guard(() -> reload(selected == null ? null : selected.id()));
    }

    @FXML
    private void addProduct() {
        openForm(null);
    }

    @FXML
    private void editProduct() {
        Product selected = productTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            openForm(selected);
        }
    }

    @FXML
    private void deleteProduct() {
        Product selected = productTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            return;
        }
        boolean confirmed = alertIO.confirm(
                "Видалити продукт «" + selected.name() + "»?\nРазом з ним буде видалено значення його параметрів.",
                "Видалити");
        if (!confirmed) {
            return;
        }
        alertIO.guard(() -> {
            productService.delete(selected.id());
            reload(null);
        });
    }

    private void openForm(Product product) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/ua/edu/nuos/lab5technology/modals/product_form.fxml"));
            Parent root = loader.load();

            ProductFormController controller = loader.getController();
            controller.setProduct(product);

            Stage stage = new Stage();
            stage.setTitle(product == null ? "Новий продукт" : "Редагування продукту");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(productTable.getScene().getWindow());
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.showAndWait();

            if (controller.isSaved()) {
                alertIO.guard(() -> reload(product == null ? null : product.id()));
            }
        } catch (IOException e) {
            alertIO.showError("Не вдалося відкрити форму: " + e.getMessage());
        }
    }

    /** Перечитує дані з БД; якщо передано id, повертає виділення на цей рядок. */
    private void reload(Integer keepSelectedId) {
        products.setAll(productService.getAll());
        updateCount();
        if (keepSelectedId != null) {
            products.stream()
                    .filter(product -> product.id() == keepSelectedId)
                    .findFirst()
                    .ifPresent(product -> productTable.getSelectionModel().select(product));
        }
    }

    private void applyFilter(String text) {
        String query = text == null ? "" : text.trim().toLowerCase();
        filteredProducts.setPredicate(product -> query.isEmpty()
                || product.name().toLowerCase().contains(query)
                || product.group().name().toLowerCase().contains(query)
                || (product.description() != null && product.description().toLowerCase().contains(query)));
    }

    private void updateCount() {
        countLabel.setText("Показано " + filteredProducts.size() + " із " + products.size());
    }
}
