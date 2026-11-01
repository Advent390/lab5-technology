package ua.edu.nuos.lab5technology.service;

import ua.edu.nuos.lab5technology.dao.ProductGroupDao;
import ua.edu.nuos.lab5technology.data.ProductGroup;

import java.util.List;

public class ProductGroupService {
    private final ProductGroupDao productGroupDao = new ProductGroupDao();

    public List<ProductGroup> getAll() {
        return productGroupDao.findAll();
    }
}
