package ua.edu.nuos.lab5technology.service;

import ua.edu.nuos.lab5technology.dao.ParameterDao;
import ua.edu.nuos.lab5technology.data.Parameter;

import java.util.List;

public class ParameterService {
    private final ParameterDao parameterDao = new ParameterDao();

    public List<Parameter> getAll() {
        return parameterDao.findAll();
    }
}
