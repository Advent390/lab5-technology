package ua.edu.nuos.lab5technology.io;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Region;
import ua.edu.nuos.lab5technology.dao.DbException;

import java.util.Objects;

/** Діалогові вікна в загальному стилі застосунку. */
public class AlertIO {

    private static final String STYLESHEET = Objects.requireNonNull(
            AlertIO.class.getResource("/ua/edu/nuos/lab5technology/styles/styles.css")).toExternalForm();

    public void showInfo(String message) {
        create(Alert.AlertType.INFORMATION, "Повідомлення", message).showAndWait();
    }

    public void showWarning(String message) {
        create(Alert.AlertType.WARNING, "Увага", message).showAndWait();
    }

    public void showError(String message) {
        create(Alert.AlertType.ERROR, "Помилка", message).showAndWait();
    }

    /** Запитує підтвердження; повертає true, лише якщо користувач натиснув кнопку підтвердження. */
    public boolean confirm(String message, String confirmLabel) {
        Alert alert = create(Alert.AlertType.CONFIRMATION, "Підтвердження", message);
        ButtonType yes = new ButtonType(confirmLabel, ButtonBar.ButtonData.OK_DONE);
        ButtonType no = new ButtonType("Скасувати", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(yes, no);
        return alert.showAndWait().filter(button -> button == yes).isPresent();
    }

    /**
     * Виконує дію й перетворює винятки на повідомлення для користувача.
     *
     * @return true, якщо дія виконалась без помилок
     */
    public boolean guard(Runnable action) {
        try {
            action.run();
            return true;
        } catch (IllegalArgumentException e) {
            showWarning(e.getMessage());
        } catch (DbException e) {
            showError(e.getMessage());
        } catch (RuntimeException e) {
            e.printStackTrace();
            showError("Непередбачена помилка: " + e);
        }
        return false;
    }

    private Alert create(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.getDialogPane().getStylesheets().add(STYLESHEET);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        return alert;
    }
}
