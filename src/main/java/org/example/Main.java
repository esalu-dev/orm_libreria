package org.example;

import org.example.database.MysqlDriver;
import org.example.database.ORM;
import org.example.models.*;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        try (MysqlDriver driver = MysqlDriver.getInstance("jdbc:mysql://localhost:3306/biblioteca-test", "naturalh", "password")) {
            ORM orm = new ORM(driver);
            testFinds(orm);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }

    public static void testFinds(ORM orm) throws Exception {
        Usuarios usuario = new Usuarios("example@domain.com", "user", "password", "this is a name");
        var editorial = new Editoriales("Penguin 12");
        var autores = List.of(new Autores("Among 1"), new Autores("Among 2"));
        var generos = List.of(new Generos("Us 1"), new Generos("Us 2"));

        Libros libro = new Libros("It", "2005",
                editorial,
                autores,
                generos);

        Prestamos prestamo = new Prestamos(usuario, libro, new Timestamp(new Date().getTime()));

//        orm.insert(usuario); // can work with this uncommented too
//        orm.insert(libro);
        orm.insert(prestamo);
    }
}