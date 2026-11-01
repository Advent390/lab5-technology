package ua.edu.nuos.lab5technology.data;

import java.util.List;

/** Універсальний результат SQL-запиту: заголовки стовпців і рядки зі значеннями у вигляді тексту. */
public record QueryResult(List<String> columns, List<List<String>> rows) {
}
