package org.example.models;

import org.example.annotations.FieldInfo;
import org.example.annotations.ManyToMany;
import org.example.annotations.ManyToOne;
import org.example.annotations.PrimaryKey;

import java.util.List;

public class Libros {
    @PrimaryKey
    int libro_id;

    @FieldInfo(size = 255)
    String titulo;

    @FieldInfo(size = 255)
    String anio_publicacion;

    @ManyToOne(column = "editorial_id")
    Editoriales editorial;

    @ManyToMany(table = "libros_autores", joinColumn = "libro_id", inverseJoinColumn = "autor_id")
    List<Autores> autores;

    @ManyToMany(table = "libros_generos", joinColumn = "libro_id", inverseJoinColumn = "genero_id")
    List<Generos> generos;

    public Libros(String titulo, String anio_publicacion, Editoriales editorial, List<Autores> autores, List<Generos> generos) {
        this.titulo = titulo;
        this.anio_publicacion = anio_publicacion;
        this.editorial = editorial;
        this.autores = autores;
        this.generos = generos;
    }

    public Libros() {

    }

    @Override
    public String toString() {
        return "Libros{" +
                "libro_id=" + libro_id +
                ", titulo='" + titulo + '\'' +
                ", editorial=" + editorial +
                ", autores=" + autores +
                ", generos=" + generos +
                '}';
    }
}
