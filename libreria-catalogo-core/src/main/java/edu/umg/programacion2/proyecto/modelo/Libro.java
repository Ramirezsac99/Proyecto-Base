package edu.umg.programacion2.proyecto.modelo;

import java.math.BigDecimal;

/**
 * Modelo (POJO) que representa un libro del catalogo.
 *
 * Esta clase vive en el modulo "-core" y no sabe nada de Swing ni de
 * JDBC: solo guarda datos. Es el objeto que viaja entre el DAO y la UI.
 */
public class Libro {

    private int id;
    private String titulo;
    private String autor;
    private String categoria;
    private BigDecimal precio;
    private int existencias;
    private int anioPublicacion;
    private boolean esBestSeller;

    public Libro() {
    }

    /** Constructor sin id, útil al crear un libro nuevo (el id lo asigna la BD). */
    public Libro(String titulo, String autor, String categoria,
                 BigDecimal precio, int existencias, int anioPublicacion, boolean esBestSeller) {
        this.titulo = titulo;
        this.autor = autor;
        this.categoria = categoria;
        this.precio = precio;
        this.existencias = existencias;
        this.anioPublicacion = anioPublicacion;
        this.esBestSeller = esBestSeller;
    }

    /** Constructor completo, usado al leer una fila ya existente de la BD. */
    public Libro(int id, String titulo, String autor, String categoria,
                 BigDecimal precio, int existencias, int anioPublicacion, boolean esBestSeller) {
        this.id = id;
        this.titulo = titulo;
        this.autor = autor;
        this.categoria = categoria;
        this.precio = precio;
        this.existencias = existencias;
        this.anioPublicacion = anioPublicacion;
        this.esBestSeller = esBestSeller;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public int getExistencias() {
        return existencias;
    }

    public void setExistencias(int existencias) {
        this.existencias = existencias;
    }

    public int getAnioPublicacion() {
        return anioPublicacion;
    }

    public void setAnioPublicacion(int anioPublicacion) {
        this.anioPublicacion = anioPublicacion;
    }
    
    public boolean isEsBestSeller() {
        return esBestSeller;
    }

    public void setEsBestSeller(boolean esBestSeller) {
        this.esBestSeller = esBestSeller;
    }

    @Override
    public String toString() {
        return "Libro{" +
                "id=" + id +
                ", titulo='" + titulo + '\'' +
                ", autor='" + autor + '\'' +
                ", categoria='" + categoria + '\'' +
                ", precio=" + precio +
                ", existencias=" + existencias +
                ", anioPublicacion=" + anioPublicacion +
                '}';
    }
}
