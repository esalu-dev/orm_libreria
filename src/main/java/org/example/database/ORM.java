package org.example.database;

import org.example.annotations.ManyToMany;
import org.example.annotations.ManyToOne;
import org.example.annotations.PrimaryKey;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;

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
                var type = ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
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

    private void disableForeignKeysCheck() throws SQLException {
        var stmt = connection.prepareStatement("SET FOREIGN_KEY_CHECKS=0");
        stmt.execute();
    }

    private void enableForeignKeysCheck() throws SQLException {
        var stmt = connection.prepareStatement("SET FOREIGN_KEY_CHECKS=1");
        stmt.execute();
    }


    public <T> void insert(T obj) throws Exception {
        Class<?> clazz = obj.getClass();
        String tableName = clazz.getSimpleName().toLowerCase();
        StringJoiner columns = new StringJoiner(", ");
        StringJoiner values = new StringJoiner(", ");

        try {
            disableForeignKeysCheck();

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
                    var primaryKeyField = Arrays.stream(relatedObj.getClass().getDeclaredFields())
                            .filter(f -> {
                                f.setAccessible(true);
                                return f.isAnnotationPresent(PrimaryKey.class);
                            })
                            .findFirst()
                            .orElseThrow(RuntimeException::new);

                    var stmt = connection.prepareStatement("SELECT 1 FROM " + tableName + " WHERE " + "`" + field.getName() + "_id`" + " = ?");
                    var value = primaryKeyField.get(relatedObj);

                    columns.add("`" + field.getName() + "_id`");
                    if (value != null) {
                        stmt.setObject(1, value);
                        values.add(String.valueOf(primaryKeyField.get(relatedObj)));
                        continue;
                    }

                    insert(relatedObj);
                    values.add(String.valueOf(primaryKeyField.get(relatedObj)));
                    continue;
                } else if (field.isAnnotationPresent(ManyToMany.class)) {
//                    insertManyToMany(obj, field);
                    continue;
                }
                // Campos normales

                columns.add("`" + field.getName() + "`");
                var parsedType = SqlTypesMapper.getSqlType(field.getType().getTypeName());

                if (parsedType.equals("VARCHAR")) {
                    values.add("'" + field.get(obj) + "'");
                } else if (parsedType.equals("INTEGER")) {
                    values.add(field.get(obj).toString());
                } else {
                    throw new RuntimeException("Unsupported type");
                }
            }

            // Construir e insertar
            String sql = "INSERT INTO " + tableName + " (" + columns + ") VALUES (" + values + ")";
            System.out.println(sql);

            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.executeUpdate(sql, PreparedStatement.RETURN_GENERATED_KEYS);

                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        var primaryKeyField = Arrays.stream(obj.getClass().getDeclaredFields())
                                .filter(f -> f.isAnnotationPresent(PrimaryKey.class))
                                .findFirst()
                                .orElseThrow(RuntimeException::new);

                        primaryKeyField.setAccessible(true);
                        primaryKeyField.set(obj, generatedKeys.getObject(1));
                    }
                }
            }

            Arrays.stream(obj.getClass().getDeclaredFields())
                    .filter(f -> f.isAnnotationPresent(ManyToMany.class))
                    .forEach(f -> {
                        try {
                            insertManyToMany(obj, f);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    });
        } catch (Exception e) {
            throw new Exception(e);
        } finally {
            enableForeignKeysCheck();
        }
    }

    private <T> void insertManyToMany(T obj, Field field) throws Exception {
        ManyToMany annotation = field.getAnnotation(ManyToMany.class);
        String joinTable = annotation.joinTable();
        String joinColumn = annotation.foreignKey();
        String inverseJoinColumn = annotation.references();

        field.setAccessible(true);
        List<?> relatedObjects = (List<?>) field.get(obj);
        if (relatedObjects == null) return;

        var primaryKeyField = Arrays.stream(obj.getClass().getDeclaredFields())
                .filter(f -> f.isAnnotationPresent(PrimaryKey.class))
                .findFirst()
                .orElseThrow(RuntimeException::new);
        primaryKeyField.setAccessible(true);
        Object idValue = primaryKeyField.get(obj);

        for (Object relatedObj : relatedObjects) {
            var primaryRelationField = Arrays.stream(relatedObj.getClass().getDeclaredFields())
                    .filter(f -> f.isAnnotationPresent(PrimaryKey.class))
                    .findFirst()
                    .orElseThrow(RuntimeException::new);
            primaryRelationField.setAccessible(true);
            Object relatedIdValue = primaryRelationField.get(relatedObj);

            if (relatedIdValue == null) {
                insert(relatedObj);
                relatedIdValue = primaryRelationField.get(relatedObj);
            }

            String sql = "INSERT INTO " + joinTable + " (" + joinColumn + ", " + inverseJoinColumn + ") VALUES (?, ?)";
            System.out.println(sql);
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setObject(1, idValue);
                stmt.setObject(2, relatedIdValue);
                stmt.executeUpdate();
            }
        }
    }
}
