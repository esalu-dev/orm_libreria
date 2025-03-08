package org.example;

import org.example.database.MysqlDriver;
import org.example.database.ORM;
import org.example.models.*;

import java.math.BigInteger;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        try (MysqlDriver driver = MysqlDriver.getInstance("jdbc:mysql://localhost:3306/biblioteca-test", "naturalh", "password")) {
            ORM orm = new ORM(driver);
//            Generos genero = orm.find(Generos.class, 11, "libros");
//            Autores autor = orm.find(Autores.class, 21, "libros");
//            Editoriales editorial = orm.find(Editoriales.class, 21);

            var editorial = new Editoriales("Penguin 12");
            var autores = List.of(new Autores("Among 1"));
            var generos = List.of(new Generos("Us 2"));

            Libros libro = new Libros("It", "2005",
                    editorial,
                    autores,
                    generos);

            orm.insert(libro);
//            var a = new Editoriales("among us");
//            a.setEditorial_id(BigInteger.valueOf(21));
//            orm.insert(a);
//            System.out.println(editorial);
//            System.out.println(genero);
//            System.out.println(autor);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }

    public static void testFinds(ORM orm) throws Exception {
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
    }
}