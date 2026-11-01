package ua.edu.nuos.lab5technology.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Підключення до PostgreSQL. Параметри задано константами нижче:
 * змініть USER і PASSWORD на свої, якщо вони відрізняються.
 */
public final class PostgresConnection {

    private static final String URL = "jdbc:postgresql://localhost:5432/tech_lab_4";
    private static final String USER = "postgres";
    private static final String PASSWORD = "1234";

    private PostgresConnection() {
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    /** Короткий опис підключення для відображення в інтерфейсі. */
    public static String describe() {
        return USER + "@" + URL.substring("jdbc:postgresql://".length());
    }
}
