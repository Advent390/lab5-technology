package ua.edu.nuos.lab5technology.dao;

import ua.edu.nuos.lab5technology.connection.PostgresConnection;
import ua.edu.nuos.lab5technology.data.Product;
import ua.edu.nuos.lab5technology.data.ProductGroup;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"SqlNoDataSourceInspection", "SqlResolve"})
public class ProductDao {

    private static final String READ_PRODUCTS = """
            SELECT p.id,
                   p.name,
                   p.description,
                   p.release_date,
                   g.id   AS group_id,
                   g.name AS group_name
            FROM product p
            JOIN product_group g ON g.id = p.product_group_id
            ORDER BY p.id
            """;

    private static final String CREATE_PRODUCT = """
            INSERT INTO product(name, product_group_id, description, release_date)
            VALUES (?, ?, ?, ?)
            """;

    private static final String UPDATE_PRODUCT = """
            UPDATE product
            SET name = ?, product_group_id = ?, description = ?, release_date = ?
            WHERE id = ?
            """;

    private static final String DELETE_PRODUCT = "DELETE FROM product WHERE id = ?";

    public List<Product> findAll() {
        List<Product> products = new ArrayList<>();

        try (Connection connection = PostgresConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(READ_PRODUCTS);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                ProductGroup group = new ProductGroup(rs.getInt("group_id"), rs.getString("group_name"));
                products.add(new Product(
                        rs.getInt("id"),
                        rs.getString("name"),
                        group,
                        rs.getString("description"),
                        rs.getObject("release_date", LocalDate.class)
                ));
            }
        } catch (SQLException e) {
            throw DbException.of(e);
        }
        return products;
    }

    public void create(Product product) {
        try (Connection connection = PostgresConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(CREATE_PRODUCT)) {
            bindFields(ps, product);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw DbException.of(e);
        }
    }

    public void update(Product product) {
        try (Connection connection = PostgresConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(UPDATE_PRODUCT)) {
            bindFields(ps, product);
            ps.setInt(5, product.id());
            if (ps.executeUpdate() == 0) {
                throw new DbException("Запис не знайдено — можливо, його вже видалено.");
            }
        } catch (SQLException e) {
            throw DbException.of(e);
        }
    }

    public void delete(int id) {
        try (Connection connection = PostgresConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(DELETE_PRODUCT)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw DbException.of(e);
        }
    }

    private static void bindFields(PreparedStatement ps, Product product) throws SQLException {
        ps.setString(1, product.name());
        ps.setInt(2, product.group().id());
        if (product.description() == null) {
            ps.setNull(3, Types.VARCHAR);
        } else {
            ps.setString(3, product.description());
        }
        if (product.releaseDate() == null) {
            ps.setNull(4, Types.DATE);
        } else {
            ps.setDate(4, Date.valueOf(product.releaseDate()));
        }
    }
}
