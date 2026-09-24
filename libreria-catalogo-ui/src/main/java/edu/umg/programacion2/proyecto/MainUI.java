package edu.umg.programacion2.proyecto;

import edu.umg.programacion2.proyecto.ui.VentanaPrincipal;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Punto de entrada de la aplicacion. Solo arranca la ventana -- toda la
 * logica vive en VentanaPrincipal (UI) y en el modulo -core (datos).
 */
public class MainUI {

    public static void main(String[] args) {
        // Look&Feel del sistema operativo en vez del gris feo por defecto de Swing.
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Si falla, seguimos con el look&feel por defecto; no es motivo para no arrancar.
            System.err.println("No se pudo aplicar el Look&Feel del sistema: " + e.getMessage());
        }

        SwingUtilities.invokeLater(() -> {
            VentanaPrincipal ventana = new VentanaPrincipal();
            ventana.setVisible(true);
        });
    }
}
