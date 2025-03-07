package org.example.database;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.StringJoiner;

public class ORM {
    private final Connection connection;

    public ORM(MysqlDriver driver) {
        this.connection = driver.getConnection();
    }

    public <T> void insert(T obj) throws SQLException, IllegalAccessException {
        Class<?> clazz = obj.getClass();
        Field[] fields = clazz.getDeclaredFields();

        String tableName = clazz.getSimpleName();
        StringJoiner columnNames = new StringJoiner(", ");
        StringJoiner valuesPlaceholders = new StringJoiner(", ");

        for (Field field : fields) {
            field.setAccessible(true);
            columnNames.add("`" + field.getName() + "`");
            valuesPlaceholders.add("?");
        }

        String sql = "INSERT INTO " + tableName + " (" + columnNames + ") VALUES (" + valuesPlaceholders + ")";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            int index = 1;
            for (Field field : fields) {
                stmt.setObject(index++, field.get(obj));
            }
            stmt.executeUpdate();
        }
    }
}
