package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    private static final String JDBC_URL = "jdbc:mysql://127.0.0.1:3306/data";
    private static final String JDBC_USER = "root";
    private static final String JDBC_PASSWORD = "Ravendette01@";

    public static Connection getConnection() throws SQLException {
        try {
            System.out.println("Loading database driver...");
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Database driver loaded.");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new SQLException("MySQL Driver not found", e);
        }
        return DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
    }
}
