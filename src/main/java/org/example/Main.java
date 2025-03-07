package org.example;

import org.example.database.MysqlDriver;
import org.example.database.ORM;
import org.example.models.Usuarios;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        try {
            MysqlDriver driver = MysqlDriver.getInstance("jdbc:mysql://localhost:3306/biblioteca", "root", "admin");
            ORM orm  = new ORM(driver);
            Usuarios usuario = new Usuarios("test@test.com", "test", "password", "Test");
            orm.insert(usuario);
            System.out.println("Usuario insertado");
        } catch (SQLException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }


    }
}