// Archivo: ConexionBD.java
package Actividad;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clase de utilidad para conectar con la base de datos MySQL 'world'.
 * Es importante cambiar los valores de USUARIO y CLAVE si son diferentes
 * a los predeterminados o a los configurados en tu entorno local.
 */
public class ConexionBD {

    // URL de conexión JDBC para MySQL. Especifica el host (localhost), puerto (3306) y nombre de la base de datos (world).
    private static final String URL = "jdbc:mysql://localhost:3306/world"; 
    // Usuario para la conexión a la base de datos.
    private static final String USUARIO = "root"; // Cambia este valor si tu usuario de MySQL es otro.
    // Contraseña para la conexión a la base de datos.
    private static final String CLAVE = "inca.2025"; // Cambia este valor por tu contraseña de MySQL. Si no tiene, dejar ""

    /**
     * Establece y devuelve una conexión con la base de datos MySQL.
     * Utiliza los parámetros definidos en las constantes URL, USUARIO y CLAVE.
     * * @return un objeto Connection si la conexión es exitosa, o null si ocurre un error.
     */
    public static Connection conectar() {
        Connection conn = null; // Inicializa la conexión como null
        try {
            // Intenta establecer la conexión usando el DriverManager
            conn = DriverManager.getConnection(URL, USUARIO, CLAVE);
            // Si la conexión es exitosa, imprime un mensaje en la consola.
            System.out.println("✅ Conexión exitosa a la base de datos 'world'.");
        } catch (SQLException e) {
            // Si ocurre una SQLException (ej. base de datos no disponible, credenciales incorrectas),
            // imprime un mensaje de error en la consola de errores.
            System.err.println("❌ Error al conectar con la base de datos 'world': " + e.getMessage());
            // Opcionalmente, podrías lanzar una excepción personalizada aquí o mostrar un JOptionPane.
        }
        return conn; // Devuelve la conexión (o null si falló)
    }
}
