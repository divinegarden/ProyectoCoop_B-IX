// Archivo: GestorIdiomas.java
package Actividad;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

/**
 * Clase que gestiona las operaciones CRUD (Crear, Leer, Actualizar, Borrar) 
 * sobre la tabla 'countrylanguage' de la base de datos.
 */
public class GestorIdiomas {

    /**
     * Carga todos los idiomas asociados a un país específico, dado su código.
     * @param codigoPais El código de 3 letras del país (ej. "ESP").
     * @return Una lista de objetos ModeloIdioma que representan los idiomas de ese país.
     * Devuelve una lista vacía si el país no tiene idiomas registrados o si ocurre un error.
     */
    public static List<ModeloIdioma> cargarIdiomas(String codigoPais) {
        List<ModeloIdioma> lista = new ArrayList<>(); // Inicializa la lista de idiomas
        String sql = "SELECT Language, IsOfficial, Percentage FROM countrylanguage WHERE CountryCode = ?";

        // Try-with-resources para asegurar el cierre automático de Connection, PreparedStatement y ResultSet
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, codigoPais); // Establece el código del país en la consulta preparada
            try (ResultSet rs = ps.executeQuery()) { // Ejecuta la consulta
                while (rs.next()) { // Itera sobre cada idioma encontrado
                    String idiomaNombre = rs.getString("Language");
                    // El campo IsOfficial es un ENUM('T','F') en la BD, se convierte a boolean
                    boolean oficial = rs.getString("IsOfficial").equalsIgnoreCase("T"); 
                    double porcentaje = rs.getDouble("Percentage");
                    // Añade un nuevo objeto ModeloIdioma a la lista
                    lista.add(new ModeloIdioma(idiomaNombre, oficial, porcentaje));
                }
            }
        } catch (SQLException e) {
            // Muestra un mensaje de error y lo imprime en la consola si falla la carga
            JOptionPane.showMessageDialog(null, "Error al cargar idiomas: " + e.getMessage(), "Error DB", JOptionPane.ERROR_MESSAGE);
            System.err.println("❌ Error al cargar idiomas para " + codigoPais + ": " + e.getMessage());
        }
        return lista; // Devuelve la lista de idiomas (puede estar vacía)
    }

    /**
     * Inserta un nuevo idioma para un país específico en la base de datos.
     * @param codigoPais El código del país al que se asociará el idioma.
     * @param idioma El objeto ModeloIdioma con los datos del nuevo idioma.
     * @return true si la inserción fue exitosa, false en caso contrario.
     */
    public static boolean insertarIdioma(String codigoPais, ModeloIdioma idioma) {
        String sql = "INSERT INTO countrylanguage (CountryCode, Language, IsOfficial, Percentage) VALUES (?, ?, ?, ?)";

        try (Connection conn = ConexionBD.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            // Establece los parámetros para la inserción
            ps.setString(1, codigoPais);
            ps.setString(2, idioma.getIdioma());
            ps.setString(3, idioma.isEsOficial() ? "T" : "F"); // Convierte boolean a 'T' o 'F'
            ps.setDouble(4, idioma.getPorcentaje());
            
            int filasAfectadas = ps.executeUpdate(); // Ejecuta la inserción
            if (filasAfectadas > 0) {
                System.out.println("✅ Idioma '" + idioma.getIdioma() + "' insertado correctamente para el país " + codigoPais);
                return true;
            }
            return false;

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al insertar idioma: " + e.getMessage(), "Error DB", JOptionPane.ERROR_MESSAGE);
            System.err.println("❌ Error al insertar idioma '" + idioma.getIdioma() + "' para " + codigoPais + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Modifica los datos de un idioma existente para un país específico.
     * NOTA: Esta funcionalidad no está directamente requerida por la interfaz gráfica del proyecto,
     * pero se incluye por completitud del gestor. La interfaz solo añade y borra idiomas.
     * @param codigoPais El código del país.
     * @param idioma El objeto ModeloIdioma con los datos actualizados. El nombre del idioma se usa para identificar el registro a modificar.
     * @return true si la modificación fue exitosa, false en caso contrario.
     */
    public static boolean modificarIdioma(String codigoPais, ModeloIdioma idioma) {
        String sql = "UPDATE countrylanguage SET IsOfficial = ?, Percentage = ? WHERE CountryCode = ? AND Language = ?";

        try (Connection conn = ConexionBD.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, idioma.isEsOficial() ? "T" : "F");
            ps.setDouble(2, idioma.getPorcentaje());
            ps.setString(3, codigoPais);
            ps.setString(4, idioma.getIdioma()); // El nombre del idioma es parte de la PK compuesta
            
            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                System.out.println("✅ Idioma '" + idioma.getIdioma() + "' modificado correctamente para el país " + codigoPais);
                return true;
            }
            return false;

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al modificar idioma: " + e.getMessage(), "Error DB", JOptionPane.ERROR_MESSAGE);
            System.err.println("❌ Error al modificar idioma '" + idioma.getIdioma() + "' para " + codigoPais + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Elimina un idioma específico de un país de la base de datos.
     * @param codigoPais El código del país.
     * @param nombreIdioma El nombre del idioma a eliminar.
     * @return true si la eliminación fue exitosa, false en caso contrario.
     */
    public static boolean eliminarIdioma(String codigoPais, String nombreIdioma) {
        String sql = "DELETE FROM countrylanguage WHERE CountryCode = ? AND Language = ?";

        try (Connection conn = ConexionBD.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, codigoPais);
            ps.setString(2, nombreIdioma);
            
            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                System.out.println("✅ Idioma '" + nombreIdioma + "' eliminado correctamente del país " + codigoPais);
                return true;
            }
            return false;

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al eliminar idioma: " + e.getMessage(), "Error DB", JOptionPane.ERROR_MESSAGE);
            System.err.println("❌ Error al eliminar idioma '" + nombreIdioma + "' del país " + codigoPais + ": " + e.getMessage());
            return false;
        }
    }
}
