package ua.edu.nuos.lab5technology.service;

import ua.edu.nuos.lab5technology.dao.QueriesDao;
import ua.edu.nuos.lab5technology.data.Parameter;
import ua.edu.nuos.lab5technology.data.ParameterGroup;
import ua.edu.nuos.lab5technology.data.ProductGroup;
import ua.edu.nuos.lab5technology.data.QueryResult;

public class QueriesService {
    private final QueriesDao queriesDao = new QueriesDao();

    /** Запит 1. */
    public QueryResult getParametersOfGroup(ProductGroup group) {
        require(group, "Оберіть групу продукції.");
        return queriesDao.parametersOfGroup(group.id());
    }

    /** Запит 2. */
    public QueryResult getProductsWithoutParameter(Parameter parameter) {
        require(parameter, "Оберіть параметр.");
        return queriesDao.productsWithoutParameter(parameter.id());
    }

    /** Запит 3. */
    public QueryResult getProductsOfGroup(ProductGroup group) {
        require(group, "Оберіть групу продукції.");
        return queriesDao.productsOfGroup(group.id());
    }

    /** Запит 4. */
    public QueryResult getProductsWithParameters() {
        return queriesDao.productsWithParameters();
    }

    /** Запит 5. Другий параметр необов'язковий: якщо його не задано, використовується перший. */
    public QueryResult deleteProductsWithParameters(Parameter first, Parameter second) {
        require(first, "Оберіть хоча б один параметр.");
        Parameter other = second == null ? first : second;
        return queriesDao.deleteProductsWithParameters(first.id(), other.id());
    }

    /** Запит 6. Повертає false, якщо у вихідній групі немає такої групи параметрів. */
    public boolean moveParameterGroup(ParameterGroup parameterGroup, ProductGroup from, ProductGroup to) {
        require(parameterGroup, "Оберіть групу параметрів.");
        require(from, "Оберіть групу продукції, з якої переміщуємо.");
        require(to, "Оберіть групу продукції, в яку переміщуємо.");
        if (from.id() == to.id()) {
            throw new IllegalArgumentException("Оберіть дві різні групи продукції.");
        }
        return queriesDao.moveParameterGroup(from.id(), to.id(), parameterGroup.id());
    }

    public QueryResult getParameterGroupLinks() {
        return queriesDao.parameterGroupLinks();
    }

    private static void require(Object value, String message) {
        if (value == null) {
            throw new IllegalArgumentException(message);
        }
    }
}
