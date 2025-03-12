package org.example.models;

import org.example.annotations.FieldInfo;
import org.example.annotations.ManyToMany;
import org.example.annotations.ManyToOne;
import org.example.annotations.PrimaryKey;

import java.math.BigInteger;
import java.util.List;

public class Libros {
    @PrimaryKey
    private BigInteger libro_id;

    @FieldInfo(size = 255)
    private String titulo;

    @FieldInfo(size = 255)
    private String anio_publicacion;

    @ManyToOne(column = "editorial_id")
    private Editoriales editorial;

    @ManyToMany(joinTable = "libros_autores", foreignKey = "libro_id", references = "autor_id")
    private List<Autores> autores;

    @ManyToMany(joinTable = "libros_generos", foreignKey = "libro_id", references = "genero_id")
    private List<Generos> generos;

    public Libros() {
    }

    public Libros(String titulo, String anio_publicacion, Editoriales editorial, List<Autores> autores, List<Generos> generos) {
        this.setTitulo(titulo);
        this.setAnio_publicacion(anio_publicacion);
        this.setEditorial(editorial);
        this.setAutores(autores);
        this.setGeneros(generos);
    }

    @Override
    public String toString() {
        return "Libros{" + "libro_id=" + getLibro_id() + ", titulo='" + getTitulo() + '\'' + ", editorial=" + getEditorial() + ", autores=" + getAutores() + ", generos=" + getGeneros() + '}';
    }

    public BigInteger getLibro_id() {
        return libro_id;
    }

    public void setLibro_id(BigInteger libro_id) {
        this.libro_id = libro_id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAnio_publicacion() {
        return anio_publicacion;
    }

    public void setAnio_publicacion(String anio_publicacion) {
        this.anio_publicacion = anio_publicacion;
    }

    public Editoriales getEditorial() {
        return editorial;
    }

    public void setEditorial(Editoriales editorial) {
        this.editorial = editorial;
    }

    public List<Autores> getAutores() {
        return autores;
    }

    public void setAutores(List<Autores> autores) {
        this.autores = autores;
    }

    public List<Generos> getGeneros() {
        return generos;
    }

    public void setGeneros(List<Generos> generos) {
        this.generos = generos;
    }
}
