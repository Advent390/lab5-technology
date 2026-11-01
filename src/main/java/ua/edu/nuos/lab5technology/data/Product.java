package ua.edu.nuos.lab5technology.data;

import java.time.LocalDate;

/** Продукт. Для нового запису id дорівнює 0. Опис і дата випуску можуть бути null. */
public record Product(int id, String name, ProductGroup group, String description, LocalDate releaseDate) {
}
