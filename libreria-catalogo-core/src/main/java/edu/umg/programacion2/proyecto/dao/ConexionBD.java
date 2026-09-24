package edu.umg.programacion2.proyecto.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Punto unico para abrir conexiones JDBC. Centralizar la URL/usuario/
 * password aqui evita repetirlos en cada metodo del DAO.
 *
 * IMPORTANTE: ajusta USUARIO y PASSWORD a los de tu instalacion local de
 * MySQL/MariaDB antes de correr la aplicacion.
 */
public final class ConexionBD {

    private static final String URL =
            "jdbc:mysql://localhost:3306/libreria_catalogo?useSSL=false&serverTimezone=UTC";
    private static final String USUARIO = "root";
    private static final String PASSWORD = "";

    private ConexionBD() {
        // Clase utilitaria, no se instancia.
    }

    public static Connection obtenerConexion() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, PASSWORD);
    }
}
