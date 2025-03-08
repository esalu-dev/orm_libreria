package org.example.models;

import org.example.annotations.ManyToOne;
import org.example.annotations.PrimaryKey;

public class Prestamos {
    @PrimaryKey
    int prestamo_id;

    @ManyToOne(column = "idUsuario")
    Usuarios usuario;  // Relación 1:N con Usuario

    @ManyToOne(column = "idLibro")
    Libros libro;  // Relación 1:N con Libro
}
