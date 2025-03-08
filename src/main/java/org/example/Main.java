package org.example;

import org.example.database.MysqlDriver;
import org.example.database.ORM;
import org.example.models.*;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        try (MysqlDriver driver = MysqlDriver.getInstance("jdbc:mysql://localhost:3306/biblioteca", "naturalh", "password")) {
            ORM orm = new ORM(driver);
            Libros libro = new Libros();
            Generos genero = new Generos();
            Autores autor = new Autores();
            Editoriales editorial = new Editoriales();

            orm.findFirst(libro);
            orm.findFirst(genero);
            orm.findFirst(autor);
            orm.findFirst(editorial);

            System.out.println(libro);
            System.out.println(genero);
            System.out.println(autor);
            System.out.println(editorial);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}