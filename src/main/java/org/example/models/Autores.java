package org.example.models;

import org.example.annotations.FieldInfo;
import org.example.annotations.ManyToMany;
import org.example.annotations.PrimaryKey;

import java.math.BigInteger;
import java.util.List;

public class Autores {
    @PrimaryKey
    private
    BigInteger autor_id;

    @FieldInfo(size = 255)
    private
    String nombre;

    @ManyToMany(joinTable = "libros_autores", foreignKey = "autor_id", references = "libro_id")
    private
    List<Libros> libros;

    public Autores(String nombre, List<Libros> libros) {
        this.setNombre(nombre);
        this.setLibros(libros);
    }

    public Autores() {

    }

    public Autores(String nombre) {
        this.setNombre(nombre);
    }

    @Override
    public String toString() {
        return "Autores{" +
                "autor_id=" + getAutor_id() +
                ", nombre='" + getNombre() + '\'' +
                '}';
    }

    public BigInteger getAutor_id() {
        return autor_id;
    }

    public void setAutor_id(BigInteger autor_id) {
        this.autor_id = autor_id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public List<Libros> getLibros() {
        return libros;
    }

    public void setLibros(List<Libros> libros) {
        this.libros = libros;
    }
}
