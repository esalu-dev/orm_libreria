package org.example.models;

import org.example.annotations.FieldInfo;
import org.example.annotations.PrimaryKey;

import java.math.BigInteger;

public class Editoriales {
    @PrimaryKey
    private BigInteger editorial_id;

    @FieldInfo(size = 255)
    private String nombre;

    public Editoriales(String nombre) {
        this.setNombre(nombre);
    }

    public Editoriales() {
    }

    @Override
    public String toString() {
        return "Editoriales{" + "editorial_id=" + getEditorial_id() + ", nombre='" + getNombre() + '\'' + '}';
    }

    public BigInteger getEditorial_id() {
        return editorial_id;
    }

    public void setEditorial_id(BigInteger editorial_id) {
        this.editorial_id = editorial_id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
