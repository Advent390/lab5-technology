package ua.edu.nuos.lab5technology.dao;

import ua.edu.nuos.lab5technology.connection.PostgresConnection;
import ua.edu.nuos.lab5technology.data.ParameterGroup;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"SqlNoDataSourceInspection", "SqlResolve"})
public class ParameterGroupDao {

    public List<ParameterGroup> findAll() {
        List<ParameterGroup> groups = new ArrayList<>();

        try (Connection connection = PostgresConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT id, name FROM parameter_group ORDER BY name");
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                groups.add(new ParameterGroup(rs.getInt("id"), rs.getString("name")));
            }
        } catch (SQLException e) {
            throw DbException.of(e);
        }
        return groups;
    }
}
