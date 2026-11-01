package ua.edu.nuos.lab5technology.dao;

import java.sql.SQLException;

/** Помилка роботи з базою даних із повідомленням, придатним для показу користувачу. */
public class DbException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final String sqlState;

    public DbException(String message) {
        super(message);
        this.sqlState = null;
    }

    public DbException(String message, SQLException cause) {
        super(message, cause);
        this.sqlState = cause.getSQLState();
    }

    public String getSqlState() {
        return sqlState;
    }

    /** Перетворює SQLException на зрозуміле повідомлення за кодом SQLSTATE. */
    public static DbException of(SQLException e) {
        String state = e.getSQLState() == null ? "" : e.getSQLState();
        String message = switch (state) {
            case "23503" -> "Операцію неможливо виконати: запис пов'язаний з іншими даними.";
            case "23505" -> "Такий запис уже існує.";
            case "23502" -> "Не заповнено обов'язкове поле.";
            case "28P01", "28000" -> "Невірний логін або пароль.";
            case "3D000" -> "Базу даних із такою назвою не знайдено.";
            case "42P01" -> "У базі немає потрібної таблиці. Перевірте, що обрано базу з лабораторної №4.";
            default -> state.startsWith("08")
                    ? "Не вдалося підключитися до сервера: " + e.getMessage()
                    : e.getMessage();
        };
        return new DbException(message, e);
    }
}
