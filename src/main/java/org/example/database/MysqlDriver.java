package org.example.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MysqlDriver implements AutoCloseable {
    private static MysqlDriver instance;
    private Connection connection;
    private final String dsn;
    private final String user;
    private final String password;

    private MysqlDriver(String dsn, String user, String password) throws SQLException {
        this.dsn = dsn;
        this.user = user;
        this.password = password;
        this.connection = DriverManager.getConnection(dsn, user, password);
    }

    public static MysqlDriver getInstance(String dsn, String user, String password) throws SQLException {
        if (instance == null || instance.connection.isClosed()) {
            instance = new MysqlDriver(dsn, user, password);
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error closing connection", e);
        }
    }
}

