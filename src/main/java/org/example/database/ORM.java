package org.example.database;

import org.example.annotations.ManyToMany;
import org.example.annotations.ManyToOne;
import org.example.annotations.PrimaryKey;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ORM {
    private final Connection connection;

    public ORM(MysqlDriver driver) throws Exception {
        this.connection = driver.getConnection();
    }

    public <T> void findFirst(T item) throws Exception {
        String tableName = item.getClass().getSimpleName().toLowerCase();
        String sql = "SELECT * FROM " + tableName + " LIMIT 1";

        var stmt = connection.prepareStatement(sql);
        var resultSet = stmt.executeQuery();

        if (resultSet.next()) {
            for (Field field : item.getClass().getDeclaredFields()) {
                field.setAccessible(true);

                if (field.isAnnotationPresent(ManyToOne.class)) {
                    continue;
                } else if (field.isAnnotationPresent(ManyToMany.class)) {
                    var primaryField = Arrays.stream(item.getClass().getDeclaredFields())
                                    .filter(f -> f.isAnnotationPresent(PrimaryKey.class))
                                            .findFirst().orElseThrow(RuntimeException::new);
                    primaryField.setAccessible(true);
                    loadManyToManyRelation(item, field, primaryField.get(item), null);
                } else {
                    field.set(item, resultSet.getObject(field.getName()));
                }
            }
        }
    }

    public <T> T find(Class<T> clazz, Object id, String... relations) throws Exception {
        String tableName = clazz.getSimpleName().toLowerCase();

        var fields = clazz.getDeclaredFields();
        String primaryKey = fields[0].getName();

        String sql = "SELECT * FROM " + tableName + " WHERE " + primaryKey + " = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, id);
            ResultSet rs = stmt.executeQuery();

            if (!rs.next()) return null;

            T obj = clazz.getDeclaredConstructor().newInstance();
            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);
                if (field.isAnnotationPresent(ManyToOne.class)) {
                    loadManyToOneRelation(obj, field, rs);
                } else if (field.isAnnotationPresent(ManyToMany.class) && contains(relations, field.getName())) {
                    loadManyToManyRelation(obj, field, id, null);
                } else {
                    field.set(obj, rs.getObject(field.getName()));
                }
            }
            return obj;
        }
    }


    private <T> void loadManyToOneRelation(T obj, Field field, ResultSet rs) throws Exception {
        ManyToOne annotation = field.getAnnotation(ManyToOne.class);
        Object relatedId = rs.getObject(annotation.column());
        Class<?> relatedClass = field.getType();
        Object relatedObj = find(relatedClass, relatedId);
        field.set(obj, relatedObj);
    }

    private <T> void loadManyToManyRelation(T obj, Field field, Object id, Object parent) throws Exception {
        ManyToMany annotation = field.getAnnotation(ManyToMany.class);
        String table = annotation.joinTable();
        String joinColumn = annotation.foreignKey();
        String inverseJoinColumn = annotation.references();
        String relatedTable = field.getName();

        String sql = "SELECT * FROM " + relatedTable + " WHERE " + inverseJoinColumn +
                " IN (SELECT " + inverseJoinColumn + " FROM " + table + " WHERE " + joinColumn + " = ?)";

        List<Object> relatedObjects = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, id);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                var type = ((ParameterizedType)field.getGenericType()).getActualTypeArguments()[0];
                var clazz = (Class<?>) type;
                var constructor = clazz.getDeclaredConstructor();

                Object relatedObj = constructor.newInstance();
                for (Field relatedField : relatedObj.getClass().getDeclaredFields()) {
                    relatedField.setAccessible(true);

                    if (relatedField.isAnnotationPresent(ManyToOne.class)) {
                        continue;
                    } else if (relatedField.isAnnotationPresent(ManyToMany.class)) {
                        var joinFromRelated = relatedField.getAnnotation(ManyToMany.class).joinTable();
                        var joinFromField = field.getAnnotation(ManyToMany.class).joinTable();

                        // check if this field is actually the parent field
                        if (joinFromRelated.equals(joinFromField) && parent != null) {
                            relatedField.set(relatedObj, parent);
                            continue;
                        }

                        var primaryField = Arrays.stream(relatedObj.getClass().getDeclaredFields())
                                .filter(f -> f.isAnnotationPresent(PrimaryKey.class))
                                .findFirst().orElseThrow(RuntimeException::new);
                        primaryField.setAccessible(true);
                        loadManyToManyRelation(relatedObj, relatedField, primaryField.get(relatedObj), relatedObjects);
                        continue;
                    }

                    relatedField.set(relatedObj, rs.getObject(relatedField.getName()));
                }
                relatedObjects.add(relatedObj);
            }
        }
        field.set(obj, relatedObjects);
    }

    private boolean contains(String[] array, String value) {
        for (String s : array) {
            if (s.equals(value)) return true;
        }
        return false;
    }


    public <T> T insert(T obj) throws Exception {
        Class<?> clazz = obj.getClass();
        String tableName = clazz.getSimpleName();
        StringBuilder columns = new StringBuilder();
        StringBuilder values = new StringBuilder();

        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(PrimaryKey.class)) {
                PrimaryKey primaryKey = field.getAnnotation(PrimaryKey.class);
                if (primaryKey.autoincrement()) {
                    continue;
                }
            }

            if (field.isAnnotationPresent(ManyToOne.class)) {
                // Relaciones 1:N (almacenar solo la clave foránea)
                Object relatedObj = field.get(obj);
                if (relatedObj != null) {
                    Field relatedIdField = relatedObj.getClass().getDeclaredFields()[0]; // Se asume que la PK es el primer campo
                    relatedIdField.setAccessible(true);
                    columns.append(field.getAnnotation(ManyToOne.class).column()).append(", ");
                    values.append("'").append(relatedIdField.get(relatedObj)).append("', ");
                }
            } else if (!field.isAnnotationPresent(ManyToMany.class)) {
                // Campos normales
                columns.append(field.getName()).append(", ");
                values.append("'").append(field.get(obj)).append("', ");
            }
        }

        // Quitar la última coma y espacio
        if (columns.length() > 0) columns.setLength(columns.length() - 2);
        if (values.length() > 0) values.setLength(values.length() - 2);

        // Construir e insertar
        String sql = "INSERT INTO " + tableName + " (" + columns + ") VALUES (" + values + ")";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.executeUpdate();
        }

        // Insertar relaciones N:M
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(ManyToMany.class)) {
                insertManyToMany(obj, field);
            }
        }

        return obj;
    }

    private <T> void insertManyToMany(T obj, Field field) throws Exception {
        ManyToMany annotation = field.getAnnotation(ManyToMany.class);
        String joinTable = annotation.joinTable();
        String joinColumn = annotation.foreignKey();
        String inverseJoinColumn = annotation.references();

        field.setAccessible(true);
        List<?> relatedObjects = (List<?>) field.get(obj);
        if (relatedObjects == null) return;

        Field idField = obj.getClass().getDeclaredFields()[0]; // Se asume que la PK es el primer campo
        idField.setAccessible(true);
        Object idValue = idField.get(obj);

        for (Object relatedObj : relatedObjects) {
            Field relatedIdField = relatedObj.getClass().getDeclaredFields()[0]; // Se asume que la PK es el primer campo
            relatedIdField.setAccessible(true);
            Object relatedIdValue = relatedIdField.get(relatedObj);

            String sql = "INSERT INTO " + joinTable + " (" + joinColumn + ", " + inverseJoinColumn + ") VALUES (?, ?)";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setObject(1, idValue);
                stmt.setObject(2, relatedIdValue);
                stmt.executeUpdate();
            }
        }
    }
}
