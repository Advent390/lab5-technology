package ua.edu.nuos.lab5technology.controllers;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import ua.edu.nuos.lab5technology.data.Parameter;
import ua.edu.nuos.lab5technology.data.ParameterGroup;
import ua.edu.nuos.lab5technology.data.ProductGroup;
import ua.edu.nuos.lab5technology.data.QueryResult;
import ua.edu.nuos.lab5technology.io.AlertIO;
import ua.edu.nuos.lab5technology.service.ParameterGroupService;
import ua.edu.nuos.lab5technology.service.ParameterService;
import ua.edu.nuos.lab5technology.service.ProductGroupService;
import ua.edu.nuos.lab5technology.service.QueriesService;

import java.util.List;

/** Шість запитів лабораторної №4. Результат будь-якого запиту показується в одній таблиці. */
public class QueriesController {
    private final QueriesService queriesService = new QueriesService();
    private final ProductGroupService productGroupService = new ProductGroupService();
    private final ParameterService parameterService = new ParameterService();
    private final ParameterGroupService parameterGroupService = new ParameterGroupService();
    private final AlertIO alertIO = new AlertIO();

    // запит 1
    @FXML
    private ComboBox<ProductGroup> parametersGroupBox;
    // запит 2
    @FXML
    private ComboBox<Parameter> missingParameterBox;
    // запит 3
    @FXML
    private ComboBox<ProductGroup> productsGroupBox;
    // запит 5
    @FXML
    private ComboBox<Parameter> deleteParameterBox;
    @FXML
    private ComboBox<Parameter> deleteSecondParameterBox;
    // запит 6
    @FXML
    private ComboBox<ParameterGroup> moveParameterGroupBox;
    @FXML
    private ComboBox<ProductGroup> moveFromBox;
    @FXML
    private ComboBox<ProductGroup> moveToBox;

    @FXML
    private TableView<List<String>> resultTable;
    @FXML
    private Label resultTitleLabel;
    @FXML
    private Label resultCountLabel;

    private final Label placeholder = new Label("Виконайте запит, щоб побачити результат");

    @FXML
    public void initialize() {
        resultTable.setPlaceholder(placeholder);
        Platform.runLater(() -> alertIO.guard(this::loadLookups));
    }

    private void loadLookups() {
        List<ProductGroup> productGroups = productGroupService.getAll();
        List<Parameter> parameters = parameterService.getAll();
        List<ParameterGroup> parameterGroups = parameterGroupService.getAll();

        parametersGroupBox.setItems(FXCollections.observableArrayList(productGroups));
        productsGroupBox.setItems(FXCollections.observableArrayList(productGroups));
        moveFromBox.setItems(FXCollections.observableArrayList(productGroups));
        moveToBox.setItems(FXCollections.observableArrayList(productGroups));

        missingParameterBox.setItems(FXCollections.observableArrayList(parameters));
        deleteParameterBox.setItems(FXCollections.observableArrayList(parameters));
        deleteSecondParameterBox.setItems(FXCollections.observableArrayList(parameters));

        moveParameterGroupBox.setItems(FXCollections.observableArrayList(parameterGroups));
    }

    @FXML
    private void runQuery1() {
        alertIO.guard(() -> {
            ProductGroup group = parametersGroupBox.getValue();
            QueryResult result = queriesService.getParametersOfGroup(group);
            show("1. Параметри групи «" + group + "»", result);
        });
    }

    @FXML
    private void runQuery2() {
        alertIO.guard(() -> {
            Parameter parameter = missingParameterBox.getValue();
            QueryResult result = queriesService.getProductsWithoutParameter(parameter);
            show("2. Продукція без параметра «" + parameter.name() + "»", result);
        });
    }

    @FXML
    private void runQuery3() {
        alertIO.guard(() -> {
            ProductGroup group = productsGroupBox.getValue();
            QueryResult result = queriesService.getProductsOfGroup(group);
            show("3. Продукція групи «" + group + "»", result);
        });
    }

    @FXML
    private void runQuery4() {
        alertIO.guard(() -> show("4. Уся продукція та її параметри", queriesService.getProductsWithParameters()));
    }

    @FXML
    private void runQuery5() {
        Parameter first = deleteParameterBox.getValue();
        Parameter second = deleteSecondParameterBox.getValue();
        if (first == null) {
            alertIO.showWarning("Оберіть хоча б один параметр.");
            return;
        }
        String names = second == null || second.equals(first)
                ? "«" + first.name() + "»"
                : "«" + first.name() + "» або «" + second.name() + "»";
        boolean confirmed = alertIO.confirm(
                "Буде безповоротно видалено всю продукцію, що має параметр " + names + ".\nПродовжити?",
                "Видалити");
        if (!confirmed) {
            return;
        }
        alertIO.guard(() -> {
            QueryResult result = queriesService.deleteProductsWithParameters(first, second);
            show("5. Видалена продукція", result);
            alertIO.showInfo("Видалено продуктів: " + result.rows().size());
        });
    }

    @FXML
    private void runQuery6() {
        alertIO.guard(() -> {
            ParameterGroup parameterGroup = moveParameterGroupBox.getValue();
            ProductGroup from = moveFromBox.getValue();
            ProductGroup to = moveToBox.getValue();

            boolean moved = queriesService.moveParameterGroup(parameterGroup, from, to);
            if (!moved) {
                alertIO.showWarning("У групі «" + from + "» немає групи параметрів «" + parameterGroup + "».");
                return;
            }
            show("6. Зв'язки груп продукції та груп параметрів після переміщення",
                    queriesService.getParameterGroupLinks());
            alertIO.showInfo("Групу параметрів «" + parameterGroup + "» переміщено з «" + from
                    + "» до «" + to + "».");
        });
    }

    /** Перебудовує таблиці під стовпці результату: запити повертають різні набори стовпців. */
    private void show(String title, QueryResult result) {
        resultTable.getColumns().clear();
        for (int i = 0; i < result.columns().size(); i++) {
            final int index = i;
            TableColumn<List<String>, String> column = new TableColumn<>(result.columns().get(i));
            column.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().get(index)));
            resultTable.getColumns().add(column);
        }
        resultTable.setItems(FXCollections.observableArrayList(result.rows()));

        placeholder.setText("Запит не повернув жодного рядка");
        resultTitleLabel.setText(title);
        resultCountLabel.setText("Рядків: " + result.rows().size());
    }
}
