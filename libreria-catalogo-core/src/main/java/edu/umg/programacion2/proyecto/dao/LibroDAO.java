package edu.umg.programacion2.proyecto.dao;

import edu.umg.programacion2.proyecto.modelo.Libro;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO del catalogo de libros. Mismo contrato que se uso en clase08
 * (crear, listarTodos, buscarPorId, actualizar, eliminar), ahora viviendo
 * en el modulo "-core" para que el modulo "-ui" lo consuma como
 * dependencia en vez de tener el JDBC mezclado con Swing.
 *
 * Todos los metodos usan PreparedStatement (nunca concatenan SQL con
 * strings) y dejan que la SQLException suba hacia quien los llama -- es
 * la UI la que decide como mostrarsela al usuario (JOptionPane), este DAO
 * no debe saber nada de eso.
 */
public class LibroDAO {

    public Libro crear(Libro libro) throws SQLException {
    	String sql = "INSERT INTO libros (titulo, autor, categoria, precio, existencias, anio_publicacion, es_best_seller) "
    	        + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement stmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, libro.getTitulo());
            stmt.setString(2, libro.getAutor());
            stmt.setString(3, libro.getCategoria());
            stmt.setBigDecimal(4, libro.getPrecio());
            stmt.setInt(5, libro.getExistencias());
            stmt.setInt(6, libro.getAnioPublicacion());
            stmt.setBoolean(7, libro.isEsBestSeller());

            stmt.executeUpdate();

            try (ResultSet generadas = stmt.getGeneratedKeys()) {
                if (generadas.next()) {
                    libro.setId(generadas.getInt(1));
                }
            }
        }
        return libro;
    }

    public List<Libro> listarTodos() throws SQLException {
        String sql = "SELECT id, titulo, autor, categoria, precio, existencias, anio_publicacion, es_best_seller "
                + "FROM libros ORDER BY id";

        List<Libro> libros = new ArrayList<>();

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                libros.add(mapearFila(rs));
            }
        }
        return libros;
    }

    public Optional<Libro> buscarPorId(int id) throws SQLException {
        String sql = "SELECT id, titulo, autor, categoria, precio, existencias, anio_publicacion, es_best_seller "
                + "FROM libros WHERE id = ?";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearFila(rs));
                }
            }
        }
        return Optional.empty();
    }

    public boolean actualizar(Libro libro) throws SQLException {
    	String sql = "UPDATE libros SET titulo = ?, autor = ?, categoria = ?, "
    	        + "precio = ?, existencias = ?, anio_publicacion = ?, es_best_seller = ? WHERE id = ?";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, libro.getTitulo());
            stmt.setString(2, libro.getAutor());
            stmt.setString(3, libro.getCategoria());
            stmt.setBigDecimal(4, libro.getPrecio());
            stmt.setInt(5, libro.getExistencias());
            stmt.setInt(6, libro.getAnioPublicacion());
            stmt.setInt(7, libro.getId());
            stmt.setBoolean(7, libro.isEsBestSeller());

            int filasAfectadas = stmt.executeUpdate();
            return filasAfectadas > 0;
        }
    }

    public boolean eliminar(int id) throws SQLException {
        String sql = "DELETE FROM libros WHERE id = ?";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, id);
            int filasAfectadas = stmt.executeUpdate();
            return filasAfectadas > 0;
        }
    }

    /** Convierte la fila actual de un ResultSet en un objeto Libro. */
    private Libro mapearFila(ResultSet rs) throws SQLException {
        return new Libro(
                rs.getInt("id"),
                rs.getString("titulo"),
                rs.getString("autor"),
                rs.getString("categoria"),
                rs.getBigDecimal("precio"),
                rs.getInt("existencias"),
                rs.getInt("anio_publicacion")
                rs.getBoolean("es_best_seller")
        );
    }
}
