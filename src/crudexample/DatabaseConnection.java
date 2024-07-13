package crudexample;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String MYSQL_URL = "jdbc:mysql://localhost:3306/crud_java";
    private static final String POSTGRESQL_URL = "jdbc:postgresql://localhost:5432/crud_java";

    public static Connection getMySQLConnection(String username, String password) throws SQLException {
        return DriverManager.getConnection(MYSQL_URL, username, password);
    }

    public static Connection getPostgreSQLConnection(String username, String password) throws SQLException {
        return DriverManager.getConnection(POSTGRESQL_URL, username, password);
    }
}
