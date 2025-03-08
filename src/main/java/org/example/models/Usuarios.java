package org.example.models;

import org.example.annotations.FieldInfo;
import org.example.annotations.PrimaryKey;
import org.example.annotations.Unique;

import java.math.BigInteger;

public class Usuarios {
    @PrimaryKey
    private BigInteger usuario_id;

    @Unique
    @FieldInfo(size = 128)
    private String email;

    @Unique
    @FieldInfo(size = 128)
    private String nombre_usuario;

    @FieldInfo(size = 128)
    private String contrasenia;

    @FieldInfo(size = 128)
    private String nombre;

    public Usuarios(String email, String nombre_usuario, String contrasenia, String nombre) {
        this.email = email;
        this.nombre_usuario = nombre_usuario;
        this.contrasenia = contrasenia;
        this.nombre = nombre;
    }

    public Usuarios() {

    }

    @Override
    public String toString() {
        return "Usuarios{" +
                "usuario_id=" + usuario_id +
                ", email='" + email + '\'' +
                ", nombre_usuario='" + nombre_usuario + '\'' +
                ", contrasenia='" + contrasenia + '\'' +
                ", nombre='" + nombre + '\'' +
                '}';
    }
}
