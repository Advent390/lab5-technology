package ua.edu.nuos.lab5technology.dao;

import ua.edu.nuos.lab5technology.connection.PostgresConnection;
import ua.edu.nuos.lab5technology.data.Parameter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"SqlNoDataSourceInspection", "SqlResolve"})
public class ParameterDao {

    public List<Parameter> findAll() {
        List<Parameter> parameters = new ArrayList<>();

        try (Connection connection = PostgresConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT id, name, unit FROM parameter ORDER BY name");
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                parameters.add(new Parameter(rs.getInt("id"), rs.getString("name"), rs.getString("unit")));
            }
        } catch (SQLException e) {
            throw DbException.of(e);
        }
        return parameters;
    }
}
