package ua.edu.nuos.lab5technology.service;

import ua.edu.nuos.lab5technology.dao.ParameterGroupDao;
import ua.edu.nuos.lab5technology.data.ParameterGroup;

import java.util.List;

public class ParameterGroupService {
    private final ParameterGroupDao parameterGroupDao = new ParameterGroupDao();

    public List<ParameterGroup> getAll() {
        return parameterGroupDao.findAll();
    }
}
