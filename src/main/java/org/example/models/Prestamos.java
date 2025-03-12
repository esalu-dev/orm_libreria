package org.example.models;

import org.example.annotations.ManyToOne;
import org.example.annotations.PrimaryKey;

import java.math.BigInteger;
import java.sql.Timestamp;

public class Prestamos {
    @PrimaryKey
    private BigInteger prestamo_id;

    @ManyToOne(column = "idUsuario")
    private Usuarios usuario;  // Relación 1:N con Usuario

    @ManyToOne(column = "idLibro")
    private Libros libro;  // Relación 1:N con Libro

    private Timestamp fecha_prestamo;
    private Timestamp fecha_devolucion;

    public Prestamos() {
    }

    public Prestamos(Usuarios usuario, Libros libro, Timestamp fecha_prestamo) {
        this.setUsuario(usuario);
        this.setLibro(libro);
        this.setFecha_prestamo(fecha_prestamo);
    }

    public Prestamos(Usuarios usuario, Libros libro, Timestamp fecha_prestamo, Timestamp fecha_devolucion) {
        this.setUsuario(usuario);
        this.setLibro(libro);
        this.setFecha_prestamo(fecha_prestamo);
        this.setFecha_devolucion(fecha_devolucion);
    }

    public BigInteger getPrestamo_id() {
        return prestamo_id;
    }

    public void setPrestamo_id(BigInteger prestamo_id) {
        this.prestamo_id = prestamo_id;
    }

    public Usuarios getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuarios usuario) {
        this.usuario = usuario;
    }

    public Libros getLibro() {
        return libro;
    }

    public void setLibro(Libros libro) {
        this.libro = libro;
    }

    public Timestamp getFecha_prestamo() {
        return fecha_prestamo;
    }

    public void setFecha_prestamo(Timestamp fecha_prestamo) {
        this.fecha_prestamo = fecha_prestamo;
    }

    public Timestamp getFecha_devolucion() {
        return fecha_devolucion;
    }

    public void setFecha_devolucion(Timestamp fecha_devolucion) {
        this.fecha_devolucion = fecha_devolucion;
    }
}
