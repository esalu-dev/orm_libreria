package org.example.models;

import org.example.annotations.FieldInfo;
import org.example.annotations.PrimaryKey;
import org.example.annotations.Unique;

public class Usuarios {
    @PrimaryKey
    private int usuario_id;

    @Unique
    @FieldInfo (size = 128)
    private String email;

    @Unique
    @FieldInfo (size = 128)
    private String nombre_usuario;

    @FieldInfo (size = 128)
    private String contrasenia;

    @FieldInfo (size = 128)
    private String nombre;

    public Usuarios(String email, String nombre_usuario, String contrasenia, String nombre) {
        this.email = email;
        this.nombre_usuario = nombre_usuario;
        this.contrasenia = contrasenia;
        this.nombre = nombre;
    }


}
