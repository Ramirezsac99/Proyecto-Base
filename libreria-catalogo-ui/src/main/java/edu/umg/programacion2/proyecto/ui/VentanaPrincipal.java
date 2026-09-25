package edu.umg.programacion2.proyecto.ui;

import edu.umg.programacion2.proyecto.dao.LibroDAO;
import edu.umg.programacion2.proyecto.modelo.Libro;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.Year;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Ventana principal del catalogo de libros.
 *
 * Importante para la evaluacion: esta clase NUNCA importa java.sql.* --
 * solo llama a metodos de LibroDAO (que vive en el modulo -core) y recibe
 * o entrega objetos Libro. Si una SQLException ocurre dentro del DAO,
 * aqui se atrapa y se muestra en un JOptionPane, sin tumbar la aplicacion
 * y sin mostrarle el stacktrace completo al usuario.
 */
public class VentanaPrincipal extends JFrame {

    private final LibroDAO libroDAO = new LibroDAO();

    private JTable tablaLibros;
    private DefaultTableModel modeloTabla;

    private JTextField campoTitulo;
    private JTextField campoAutor;
    private JTextField campoCategoria;
    private JTextField campoPrecio;
    private JTextField campoExistencias;
    private JTextField campoAnio;
    private JCheckBox campoEsBestSeller;

    private JButton botonNuevo;
    private JButton botonGuardar;
    private JButton botonEliminar;
    private JButton botonRefrescar;
    private JButton botonResumenCategorias;

    /** Id del libro actualmente seleccionado en la tabla, o null si estamos creando uno nuevo. */
    private Integer idSeleccionado = null;

    public VentanaPrincipal() {
        super("Catalogo de Libreria");
        construirInterfaz();
        cargarLibros();
    }

    // -----------------------------------------------------------------
    // Construccion de la interfaz
    // -----------------------------------------------------------------

    private void construirInterfaz() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(950, 520);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        add(construirPanelTabla(), BorderLayout.CENTER);
        add(construirPanelFormulario(), BorderLayout.SOUTH);
    }

    private JComponent construirPanelTabla() {
        String[] columnas = {"ID", "Titulo", "Autor", "Categoria", "Precio", "Existencias", "Año", "Best seller"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                // La tabla es solo de lectura; la edicion se hace por el formulario.
                return false;
            }
        };
        tablaLibros = new JTable(modeloTabla);
        tablaLibros.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaLibros.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                cargarSeleccionEnFormulario();
            }
        });

        JScrollPane scroll = new JScrollPane(tablaLibros);
        scroll.setBorder(BorderFactory.createTitledBorder("Catalogo"));
        return scroll;
    }

    private JComponent construirPanelFormulario() {
        JPanel panelCampos = new JPanel(new GridLayout(2, 7, 5, 5));
        panelCampos.setBorder(BorderFactory.createTitledBorder("Datos del libro"));

        campoTitulo = new JTextField();
        campoAutor = new JTextField();
        campoCategoria = new JTextField();
        campoPrecio = new JTextField();
        campoExistencias = new JTextField();
        campoAnio = new JTextField();
        campoEsBestSeller = new JCheckBox("Es best seller");

        panelCampos.add(new JLabel("Titulo:"));
        panelCampos.add(new JLabel("Autor:"));
        panelCampos.add(new JLabel("Categoria:"));
        panelCampos.add(new JLabel("Precio:"));
        panelCampos.add(new JLabel("Existencias:"));
        panelCampos.add(new JLabel("Año publicacion:"));
        panelCampos.add(new JLabel(""));   // celda vacia para alinear con el checkbox

        panelCampos.add(campoTitulo);
        panelCampos.add(campoAutor);
        panelCampos.add(campoCategoria);
        panelCampos.add(campoPrecio);
        panelCampos.add(campoExistencias);
        panelCampos.add(campoAnio);
        panelCampos.add(campoEsBestSeller);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        botonNuevo = new JButton("Nuevo");
        botonGuardar = new JButton("Guardar");
        botonEliminar = new JButton("Eliminar");
        botonRefrescar = new JButton("Refrescar");
        botonResumenCategorias = new JButton("Ver resumen por categoria");

        botonNuevo.addActionListener(e -> limpiarFormulario());
        botonGuardar.addActionListener(e -> guardar());
        botonEliminar.addActionListener(e -> eliminar());
        botonRefrescar.addActionListener(e -> cargarLibros());
        botonResumenCategorias.addActionListener(e -> mostrarResumenPorCategoria());

        panelBotones.add(botonNuevo);
        panelBotones.add(botonGuardar);
        panelBotones.add(botonEliminar);
        panelBotones.add(botonRefrescar);
        panelBotones.add(botonResumenCategorias);

        JPanel panelInferior = new JPanel(new BorderLayout());
        panelInferior.add(panelCampos, BorderLayout.CENTER);
        panelInferior.add(panelBotones, BorderLayout.SOUTH);
        return panelInferior;
    }

    // -----------------------------------------------------------------
    // Carga de datos (Read)
    // -----------------------------------------------------------------

    private void cargarLibros() {
        try {
            List<Libro> libros = libroDAO.listarTodos();
            modeloTabla.setRowCount(0);
            for (Libro libro : libros) {
                modeloTabla.addRow(new Object[]{
                        libro.getId(),
                        libro.getTitulo(),
                        libro.getAutor(),
                        libro.getCategoria(),
                        libro.getPrecio(),
                        libro.getExistencias(),
                        libro.getAnioPublicacion(),
                        libro.isEsBestSeller() ? "Si" : "No"
                });
            }
        } catch (SQLException ex) {
            mostrarErrorBD("No se pudo cargar el catalogo.", ex);
        }
    }

    private void cargarSeleccionEnFormulario() {
        int filaSeleccionada = tablaLibros.getSelectedRow();
        if (filaSeleccionada == -1) {
            return;
        }
        idSeleccionado = (Integer) modeloTabla.getValueAt(filaSeleccionada, 0);
        campoTitulo.setText(String.valueOf(modeloTabla.getValueAt(filaSeleccionada, 1)));
        campoAutor.setText(String.valueOf(modeloTabla.getValueAt(filaSeleccionada, 2)));
        Object categoria = modeloTabla.getValueAt(filaSeleccionada, 3);
        campoCategoria.setText(categoria == null ? "" : categoria.toString());
        campoPrecio.setText(String.valueOf(modeloTabla.getValueAt(filaSeleccionada, 4)));
        campoExistencias.setText(String.valueOf(modeloTabla.getValueAt(filaSeleccionada, 5)));
        campoAnio.setText(String.valueOf(modeloTabla.getValueAt(filaSeleccionada, 6)));
        campoEsBestSeller.setSelected("Si".equals(modeloTabla.getValueAt(filaSeleccionada, 7)));
    }

    private void limpiarFormulario() {
        idSeleccionado = null;
        tablaLibros.clearSelection();
        campoTitulo.setText("");
        campoAutor.setText("");
        campoCategoria.setText("");
        campoPrecio.setText("");
        campoExistencias.setText("");
        campoAnio.setText("");
        campoEsBestSeller.setSelected(false);
        campoTitulo.requestFocus();
    }

    // -----------------------------------------------------------------
    // Create / Update
    // -----------------------------------------------------------------

    private void guardar() {
        Libro libro = validarYConstruirLibro();
        if (libro == null) {
            // La validacion ya mostro el JOptionPane correspondiente.
            return;
        }

        try {
            if (idSeleccionado == null) {
                libroDAO.crear(libro);
                JOptionPane.showMessageDialog(this, "Libro registrado.");
            } else {
                libro.setId(idSeleccionado);
                boolean actualizado = libroDAO.actualizar(libro);
                if (actualizado) {
                    JOptionPane.showMessageDialog(this, "Libro actualizado.");
                } else {
                    JOptionPane.showMessageDialog(this,
                            "No se encontro el libro (puede que ya lo hayan eliminado).",
                            "Aviso", JOptionPane.WARNING_MESSAGE);
                }
            }
            limpiarFormulario();
            cargarLibros();
        } catch (SQLException ex) {
            mostrarErrorBD("No se pudo guardar el libro.", ex);
        }
    }

    /**
     * Valida todos los campos del formulario ANTES de tocar la base de
     * datos. Si algo esta mal, muestra el JOptionPane correspondiente y
     * devuelve null; si todo esta bien, devuelve un Libro listo para
     * enviar al DAO (sin id todavia).
     */
    private Libro validarYConstruirLibro() {
        String titulo = campoTitulo.getText().trim();
        String autor = campoAutor.getText().trim();
        String categoria = campoCategoria.getText().trim();

        if (titulo.isEmpty() || autor.isEmpty()) {
            mostrarError("El titulo y el autor son obligatorios.");
            return null;
        }

        BigDecimal precio;
        try {
            precio = new BigDecimal(campoPrecio.getText().trim());
        } catch (NumberFormatException ex) {
            mostrarError("El precio debe ser un numero (ej. 145.00).");
            return null;
        }
        if (precio.compareTo(BigDecimal.ZERO) <= 0) {
            mostrarError("El precio debe ser mayor a cero.");
            return null;
        }

        int existencias;
        try {
            existencias = Integer.parseInt(campoExistencias.getText().trim());
        } catch (NumberFormatException ex) {
            mostrarError("Las existencias deben ser un numero entero.");
            return null;
        }
        if (existencias < 0) {
            mostrarError("Las existencias no pueden ser negativas.");
            return null;
        }

        int anio;
        try {
            anio = Integer.parseInt(campoAnio.getText().trim());
        } catch (NumberFormatException ex) {
            mostrarError("El año de publicacion debe ser un numero entero (ej. 1967).");
            return null;
        }
        int anioActual = Year.now().getValue();
        if (anio > anioActual) {
            mostrarError("El año de publicacion no puede ser mayor a " + anioActual + ".");
            return null;
        }

        return new Libro(titulo, autor, categoria.isEmpty() ? null : categoria,
                precio, existencias, anio, campoEsBestSeller.isSelected());
    }

    // -----------------------------------------------------------------
    // Delete
    // -----------------------------------------------------------------

    private void eliminar() {
        if (idSeleccionado == null) {
            mostrarError("Selecciona primero un libro de la tabla para eliminarlo.");
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(
                this,
                "¿Seguro que deseas eliminar \"" + campoTitulo.getText() + "\" del catalogo?\n"
                        + "Esta accion no se puede deshacer.",
                "Confirmar eliminacion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            boolean eliminado = libroDAO.eliminar(idSeleccionado);
            if (eliminado) {
                JOptionPane.showMessageDialog(this, "Libro eliminado.");
            } else {
                JOptionPane.showMessageDialog(this,
                        "No se encontro el libro (puede que ya lo hayan eliminado).",
                        "Aviso", JOptionPane.WARNING_MESSAGE);
            }
            limpiarFormulario();
            cargarLibros();
        } catch (SQLException ex) {
            mostrarErrorBD("No se pudo eliminar el libro.", ex);
        }
    }

    // -----------------------------------------------------------------
    // Mejora #10: agrupar y contar por categoria (en Java, sin GROUP BY)
    // -----------------------------------------------------------------

    private void mostrarResumenPorCategoria() {
        try {
            List<Libro> libros = libroDAO.listarTodos();

            Map<String, Integer> conteoPorCategoria = new LinkedHashMap<>();

            for (Libro libro : libros) {
                String categoria = libro.getCategoria();
                if (categoria == null || categoria.trim().isEmpty()) {
                    categoria = "(sin categoria)";
                }
                int contadorActual = conteoPorCategoria.getOrDefault(categoria, 0);
                conteoPorCategoria.put(categoria, contadorActual + 1);
            }

            if (conteoPorCategoria.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Todavia no hay libros en el catalogo.",
                        "Resumen por categoria", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            StringBuilder resumen = new StringBuilder("Libros por categoria:\n\n");
            for (Map.Entry<String, Integer> entrada : conteoPorCategoria.entrySet()) {
                resumen.append(String.format("%-20s %d%n", entrada.getKey(), entrada.getValue()));
            }

            JOptionPane.showMessageDialog(this, resumen.toString(),
                    "Resumen por categoria", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            mostrarErrorBD("No se pudo calcular el resumen por categoria.", ex);
        }
    }

    // -----------------------------------------------------------------
    // Utilidades de mensajes
    // -----------------------------------------------------------------

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Datos invalidos", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Muestra un mensaje amigable al usuario para errores de base de
     * datos, SIN exponer el stacktrace completo. El detalle tecnico se
     * manda a consola para que el desarrollador lo pueda revisar.
     */
    private void mostrarErrorBD(String mensajeAmigable, SQLException ex) {
        System.err.println("Error de base de datos: " + ex.getMessage());
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this,
                mensajeAmigable + "\nRevisa que la base de datos este disponible.",
                "Error de base de datos",
                JOptionPane.ERROR_MESSAGE);
    }
}
