package org.example.models;

import org.example.annotations.FieldInfo;
import org.example.annotations.ManyToMany;
import org.example.annotations.PrimaryKey;

import java.math.BigInteger;
import java.util.List;

public class Generos {
    @PrimaryKey
    BigInteger genero_id;

    @FieldInfo(size = 255)
    String nombre;

    @ManyToMany(table = "libros_generos", joinColumn = "genero_id", inverseJoinColumn = "libro_id")
    List<Libros> libros;

    public Generos(String nombre) {
        this.nombre = nombre;
    }

    public Generos() {

    }

    @Override
    public String toString() {
        return "Generos{" +
                "genero_id=" + genero_id +
                ", nombre='" + nombre + '\'' +
                ", libros=" + libros +
                '}';
    }
}
