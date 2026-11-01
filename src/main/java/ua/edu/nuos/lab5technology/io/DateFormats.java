package ua.edu.nuos.lab5technology.io;

import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/** Єдиний формат дати (дд.мм.рррр) для таблиці й поля вибору дати, незалежно від мови системи. */
public final class DateFormats {

    public static final DateTimeFormatter DISPLAY = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private DateFormats() {
    }

    public static StringConverter<LocalDate> converter() {
        return new StringConverter<>() {
            @Override
            public String toString(LocalDate date) {
                return date == null ? "" : DISPLAY.format(date);
            }

            @Override
            public LocalDate fromString(String text) {
                if (text == null || text.isBlank()) {
                    return null;
                }
                try {
                    return LocalDate.parse(text.trim(), DISPLAY);
                } catch (DateTimeParseException e) {
                    return null;
                }
            }
        };
    }
}
