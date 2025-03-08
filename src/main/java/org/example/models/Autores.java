package org.example.models;

import org.example.annotations.FieldInfo;
import org.example.annotations.ManyToMany;
import org.example.annotations.PrimaryKey;

import java.math.BigInteger;
import java.util.List;

public class Autores {
    @PrimaryKey
    BigInteger autor_id;

    @FieldInfo(size = 255)
    String nombre;

    @ManyToMany(table = "libros_autores", joinColumn = "autor_id", inverseJoinColumn = "libro_id")
    List<Libros> libros;

    public Autores(String nombre, List<Libros> libros) {
        this.nombre = nombre;
        this.libros = libros;
    }

    public Autores() {

    }

    public Autores(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public String toString() {
        return "Autores{" +
                "autor_id=" + autor_id +
                ", nombre='" + nombre + '\'' +
                ", libros=" + libros +
                '}';
    }
}
