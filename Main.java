// Archivo: Main.java
package Actividad;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Clase principal que inicia la aplicación.
 * Crea la interfaz gráfica (Vista) y el Controlador, y los enlaza.
 */
public class Main {
    public static void main(String[] args) {
        // Se recomienda ejecutar la creación de la GUI en el Event Dispatch Thread (EDT) de Swing
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    // Intenta establecer un LookAndFeel más moderno si está disponible (Nimbus)
                    // Esto es opcional y mejora la apariencia de la GUI.
                    for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                        if ("Nimbus".equals(info.getName())) {
                            UIManager.setLookAndFeel(info.getClassName());
                            break;
                        }
                    }
                } catch (Exception e) {
                    // Si Nimbus no está disponible o falla, usa el LookAndFeel por defecto.
                    // No es un error crítico, la aplicación funcionará igualmente.
                    System.err.println("No se pudo aplicar el LookAndFeel Nimbus, usando el predeterminado.");
                }

                // Crear la instancia de la interfaz gráfica (Paises.java)
                Paises vista = new Paises();

                // Crear la instancia del Controlador, pasándole la vista
                Controlador controlador = new Controlador(vista);

                // Configurar y mostrar la ventana principal
                vista.setTitle("Gestor de Países, Capitales y Lenguas del Mundo"); // Establece el título de la ventana
                vista.setLocationRelativeTo(null); // Centra la ventana en la pantalla
                vista.setVisible(true); // Hace visible la ventana
            }
        });
    }
}
