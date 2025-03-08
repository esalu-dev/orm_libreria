package org.example.models;

import org.example.annotations.FieldInfo;
import org.example.annotations.ManyToMany;
import org.example.annotations.PrimaryKey;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class Generos {
    @PrimaryKey
    private
    BigInteger genero_id;

    @FieldInfo
    private String nombre;

    @ManyToMany(joinTable = "libros_generos", foreignKey = "genero_id", references = "libro_id")
    private List<Libros> libros;

    public Generos(String nombre) {
        this.setNombre(nombre);
    }

    public Generos() {
    }

    @Override
    public String toString() {
        return "Generos{" +
                "genero_id=" + getGenero_id() +
                ", nombre='" + getNombre() + '\'' +
                '}';
    }

    public BigInteger getGenero_id() {
        return genero_id;
    }

    public void setGenero_id(BigInteger genero_id) {
        this.genero_id = genero_id;
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
