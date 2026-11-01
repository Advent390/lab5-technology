package ua.edu.nuos.lab5technology.dao;

import ua.edu.nuos.lab5technology.connection.PostgresConnection;
import ua.edu.nuos.lab5technology.data.QueryResult;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** Шість запитів лабораторної роботи №4. Заголовки стовпців задано в SQL через псевдоніми. */
@SuppressWarnings({"SqlNoDataSourceInspection", "SqlResolve"})
public class QueriesDao {

    /** 1. Перелік параметрів для заданої групи продукції. */
    private static final String QUERY_1 = """
            SELECT DISTINCT p.id AS "ID",
                            p.name AS "Параметр",
                            COALESCE(p.unit, '') AS "Одиниця виміру"
            FROM product_group_param_group pgpg
            JOIN parameter_group_item pgi ON pgi.parameter_group_id = pgpg.parameter_group_id
            JOIN parameter p              ON p.id = pgi.parameter_id
            WHERE pgpg.product_group_id = ?
            ORDER BY 1
            """;

    /** 2. Продукція, яка не містить заданого параметра. */
    private static final String QUERY_2 = """
            SELECT pr.id AS "ID",
                   pr.name AS "Назва",
                   g.name AS "Група",
                   COALESCE(pr.description, '') AS "Опис",
                   COALESCE(to_char(pr.release_date, 'DD.MM.YYYY'), '') AS "Дата випуску"
            FROM product pr
            JOIN product_group g ON g.id = pr.product_group_id
            WHERE NOT EXISTS (
                SELECT 1
                FROM product_parameter_value v
                WHERE v.product_id = pr.id AND v.parameter_id = ?
            )
            ORDER BY pr.id
            """;

    /** 3. Інформація про продукцію для заданої групи. */
    private static final String QUERY_3 = """
            SELECT pr.id AS "ID",
                   pr.name AS "Назва",
                   g.name AS "Група",
                   COALESCE(pr.description, '') AS "Опис",
                   COALESCE(to_char(pr.release_date, 'DD.MM.YYYY'), '') AS "Дата випуску"
            FROM product pr
            JOIN product_group g ON g.id = pr.product_group_id
            WHERE g.id = ?
            ORDER BY pr.id
            """;

    /** 4. Продукція та всі її параметри зі значеннями. */
    private static final String QUERY_4 = """
            SELECT pr.name AS "Продукт",
                   COALESCE(par.name, '—') AS "Параметр",
                   COALESCE(v.value, '') AS "Значення",
                   COALESCE(par.unit, '') AS "Одиниця виміру"
            FROM product pr
            LEFT JOIN product_parameter_value v ON v.product_id = pr.id
            LEFT JOIN parameter par             ON par.id = v.parameter_id
            ORDER BY pr.name, par.name
            """;

    /** 5. Видалити продукцію, що містить задані параметри (значення параметрів видаляються каскадно). */
    private static final String QUERY_5 = """
            DELETE FROM product p
            WHERE EXISTS (
                SELECT 1
                FROM product_parameter_value v
                WHERE v.product_id = p.id AND v.parameter_id IN (?, ?)
            )
            RETURNING p.id AS "ID", p.name AS "Видалена продукція"
            """;

    /** 6. Переміщення групи параметрів: спочатку видаляємо зв'язок зі старою групою... */
    private static final String QUERY_6_DELETE = """
            DELETE FROM product_group_param_group
            WHERE product_group_id = ? AND parameter_group_id = ?
            """;

    /** ...потім додаємо зв'язок з новою групою (якщо він там уже є, нічого не змінюємо). */
    private static final String QUERY_6_INSERT = """
            INSERT INTO product_group_param_group(product_group_id, parameter_group_id)
            VALUES (?, ?)
            ON CONFLICT DO NOTHING
            """;

    /** Поточні зв'язки «група продукції — група параметрів» (для показу результату запиту 6). */
    private static final String LINKS = """
            SELECT g.name AS "Група продукції", pg.name AS "Група параметрів"
            FROM product_group_param_group l
            JOIN product_group g    ON g.id = l.product_group_id
            JOIN parameter_group pg ON pg.id = l.parameter_group_id
            ORDER BY 1, 2
            """;

    public QueryResult parametersOfGroup(int productGroupId) {
        return select(QUERY_1, productGroupId);
    }

    public QueryResult productsWithoutParameter(int parameterId) {
        return select(QUERY_2, parameterId);
    }

    public QueryResult productsOfGroup(int productGroupId) {
        return select(QUERY_3, productGroupId);
    }

    public QueryResult productsWithParameters() {
        return select(QUERY_4);
    }

    /** Повертає видалені продукти (через RETURNING). Зміни фіксуються автоматично. */
    public QueryResult deleteProductsWithParameters(int firstParameterId, int secondParameterId) {
        return select(QUERY_5, firstParameterId, secondParameterId);
    }

    /**
     * Переміщує групу параметрів з однієї групи продукції в іншу в межах однієї транзакції.
     *
     * @return false, якщо у вихідній групі продукції такої групи параметрів немає
     */
    public boolean moveParameterGroup(int fromGroupId, int toGroupId, int parameterGroupId) {
        try (Connection connection = PostgresConnection.getConnection()) {
            connection.setAutoCommit(false);
            try {
                int removed;
                try (PreparedStatement ps = connection.prepareStatement(QUERY_6_DELETE)) {
                    ps.setInt(1, fromGroupId);
                    ps.setInt(2, parameterGroupId);
                    removed = ps.executeUpdate();
                }
                if (removed == 0) {
                    connection.rollback();
                    return false;
                }
                try (PreparedStatement ps = connection.prepareStatement(QUERY_6_INSERT)) {
                    ps.setInt(1, toGroupId);
                    ps.setInt(2, parameterGroupId);
                    ps.executeUpdate();
                }
                connection.commit();
                return true;
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw DbException.of(e);
        }
    }

    public QueryResult parameterGroupLinks() {
        return select(LINKS);
    }

    //  допоміжне
    private QueryResult select(String sql, Object... params) {
        try (Connection connection = PostgresConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                return read(rs);
            }
        } catch (SQLException e) {
            throw DbException.of(e);
        }
    }

    private static QueryResult read(ResultSet rs) throws SQLException {
        ResultSetMetaData meta = rs.getMetaData();
        int count = meta.getColumnCount();

        List<String> columns = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            columns.add(meta.getColumnLabel(i));
        }

        List<List<String>> rows = new ArrayList<>();
        while (rs.next()) {
            List<String> row = new ArrayList<>();
            for (int i = 1; i <= count; i++) {
                Object value = rs.getObject(i);
                row.add(value == null ? "" : value.toString());
            }
            rows.add(row);
        }
        return new QueryResult(columns, rows);
    }
}
