package org.example;

import org.example.database.MysqlDriver;
import org.example.database.ORM;
import org.example.models.*;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        try (MysqlDriver driver = MysqlDriver.getInstance("jdbc:mysql://localhost:3306/biblioteca_copy", "root", "admin")) {
            ORM orm = new ORM(driver);
            Generos genero = orm.find(Generos.class, 11, "libros");
            Autores autor = orm.find(Autores.class, 21, "libros");
            Editoriales editorial = orm.find(Editoriales.class, 21);
            Libros libro = new Libros("It", "2005", editorial, List.of(autor), List.of(genero));
            orm.insert(libro);
            System.out.println(editorial);
            System.out.println(genero);
            System.out.println(autor);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}