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
        this.setEmail(email);
        this.setNombre_usuario(nombre_usuario);
        this.setContrasenia(contrasenia);
        this.setNombre(nombre);
    }

    public Usuarios() {

    }

    @Override
    public String toString() {
        return "Usuarios{" +
                "usuario_id=" + getUsuario_id() +
                ", email='" + getEmail() + '\'' +
                ", nombre_usuario='" + getNombre_usuario() + '\'' +
                ", contrasenia='" + getContrasenia() + '\'' +
                ", nombre='" + getNombre() + '\'' +
                '}';
    }

    public BigInteger getUsuario_id() {
        return usuario_id;
    }

    public void setUsuario_id(BigInteger usuario_id) {
        this.usuario_id = usuario_id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNombre_usuario() {
        return nombre_usuario;
    }

    public void setNombre_usuario(String nombre_usuario) {
        this.nombre_usuario = nombre_usuario;
    }

    public String getContrasenia() {
        return contrasenia;
    }

    public void setContrasenia(String contrasenia) {
        this.contrasenia = contrasenia;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
