package ua.edu.nuos.lab5technology.service;

import ua.edu.nuos.lab5technology.dao.ProductDao;
import ua.edu.nuos.lab5technology.data.Product;
import ua.edu.nuos.lab5technology.data.ProductGroup;

import java.time.LocalDate;
import java.util.List;

public class ProductService {
    private static final int MAX_NAME_LENGTH = 150;

    private final ProductDao productDao = new ProductDao();

    public List<Product> getAll() {
        return productDao.findAll();
    }

    public void create(String name, ProductGroup group, String description, LocalDate releaseDate) {
        productDao.create(build(0, name, group, description, releaseDate));
    }

    public void update(int id, String name, ProductGroup group, String description, LocalDate releaseDate) {
        productDao.update(build(id, name, group, description, releaseDate));
    }

    public void delete(int id) {
        productDao.delete(id);
    }

    /** Перевіряє введені дані; помилки валідації повертаються як IllegalArgumentException. */
    private static Product build(int id, String name, ProductGroup group, String description, LocalDate releaseDate) {
        String trimmedName = name == null ? "" : name.trim();
        if (trimmedName.isEmpty()) {
            throw new IllegalArgumentException("Вкажіть назву продукту.");
        }
        if (trimmedName.length() > MAX_NAME_LENGTH) {
            throw new IllegalArgumentException("Назва занадто довга (максимум " + MAX_NAME_LENGTH + " символів).");
        }
        if (group == null) {
            throw new IllegalArgumentException("Оберіть групу продукції.");
        }
        String trimmedDescription = description == null || description.isBlank() ? null : description.trim();
        return new Product(id, trimmedName, group, trimmedDescription, releaseDate);
    }
}
