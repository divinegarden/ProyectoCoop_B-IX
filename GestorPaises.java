// Archivo: GestorPaises.java
package Actividad;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

/**
 * Clase para gestionar las operaciones CRUD (Crear, Leer, Actualizar, Borrar)
 * relacionadas con la tabla 'country' y su interacción con 'city' para la capital.
 */
public class GestorPaises {

    /**
     * Carga los detalles completos de un país específico desde la base de datos.
     * Incluye información de la tabla 'country' y los detalles de su capital desde la tabla 'city'.
     * @param codigoPais El código de 3 letras del país a cargar (ej. "ESP").
     * @return Un objeto ModeloPais con los datos del país, o null si no se encuentra o hay un error.
     */
    public static ModeloPais cargarDetallesPais(String codigoPais) {
        ModeloPais pais = null;
        // Consulta SQL para obtener datos del país y su capital.
        // Se usa LEFT JOIN para asegurar que se devuelvan los datos del país incluso si no tiene capital asignada.
        String sql = "SELECT c.*, ci.ID as CapitalID, ci.Name as CapitalName, ci.District as CapitalDistrict, ci.Population as CapitalPopulation " +
                     "FROM country c LEFT JOIN city ci ON c.Capital = ci.ID " +
                     "WHERE c.Code = ?";

        try (Connection conn = ConexionBD.conectar(); // Obtiene la conexión a la BD
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, codigoPais); // Establece el parámetro de la consulta
            try (ResultSet rs = ps.executeQuery()) { // Ejecuta la consulta
                if (rs.next()) { // Si hay resultados
                    pais = new ModeloPais(); // Crea un nuevo objeto ModeloPais
                    // Asigna los valores del ResultSet al objeto ModeloPais
                    pais.setCodigo(rs.getString("Code"));
                    pais.setNombre(rs.getString("Name"));
                    pais.setContinente(rs.getString("Continent"));
                    pais.setRegion(rs.getString("Region"));
                    pais.setNombreLocal(rs.getString("LocalName"));
                    
                    // Manejo de campos numéricos que pueden ser nulos en la BD
                    int anioIndep = rs.getInt("IndepYear");
                    pais.setAnioIndependencia(rs.wasNull() ? null : anioIndep);
                    
                    double superficie = rs.getDouble("SurfaceArea");
                    pais.setSuperficie(rs.wasNull() ? null : superficie);
                    
                    double expVida = rs.getDouble("LifeExpectancy");
                    pais.setExpectativaVida(rs.wasNull() ? null : expVida);
                    
                    int poblacion = rs.getInt("Population");
                    pais.setPoblacion(rs.wasNull() ? null : poblacion);

                    double pnb = rs.getDouble("GNP");
                    pais.setPnb(rs.wasNull() ? null : pnb);
                    
                    pais.setFormaGobierno(rs.getString("GovernmentForm"));
                    pais.setCabezaEstado(rs.getString("HeadOfState"));
                    
                    int capitalId = rs.getInt("CapitalID");
                    pais.setCapitalID(rs.wasNull() ? null : capitalId);
                    pais.setCapitalNombre(rs.getString("CapitalName"));
                    pais.setCapitalDistrito(rs.getString("CapitalDistrict"));
                    
                    int capitalPob = rs.getInt("CapitalPopulation");
                    pais.setCapitalPoblacion(rs.wasNull() ? null : capitalPob);
                }
            }
        } catch (SQLException e) {
            // Muestra un mensaje de error y lo imprime en la consola si falla la carga
            JOptionPane.showMessageDialog(null, "Error al cargar detalles del país: " + e.getMessage(), "Error DB", JOptionPane.ERROR_MESSAGE);
            System.err.println("❌ Error al cargar detalles del país: " + e.getMessage());
        }
        return pais; // Devuelve el objeto país (puede ser null)
    }
    
    /**
     * Elimina un país de la base de datos, incluyendo sus idiomas asociados en 'countrylanguage'.
     * Realiza la operación dentro de una transacción para asegurar la integridad de los datos.
     * @param codigoPais El código del país a eliminar.
     * @return true si el país y sus idiomas fueron eliminados correctamente, false en caso contrario.
     */
    public static boolean eliminarPais(String codigoPais) {
        // SQL para borrar idiomas (dependencias) y luego el país
        String sqlLenguas = "DELETE FROM countrylanguage WHERE CountryCode = ?";
        String sqlPais = "DELETE FROM country WHERE Code = ?";
        
        try (Connection conn = ConexionBD.conectar()) { 
            conn.setAutoCommit(false); // Iniciar transacción para asegurar atomicidad
            
            try (PreparedStatement psLenguas = conn.prepareStatement(sqlLenguas);
                 PreparedStatement psPais = conn.prepareStatement(sqlPais)) {
                
                // Borrar idiomas asociados al país
                psLenguas.setString(1, codigoPais);
                psLenguas.executeUpdate();
                
                // Borrar el país
                psPais.setString(1, codigoPais);
                int filasAfectadas = psPais.executeUpdate();
                
                conn.commit(); // Confirmar la transacción si todo fue bien
                return filasAfectadas > 0; // Devuelve true si se eliminó al menos una fila (el país)

            } catch (SQLException e) {
                conn.rollback(); // Deshacer la transacción en caso de error
                JOptionPane.showMessageDialog(null, "Error al eliminar país (transacción revertida): " + e.getMessage(), "Error DB", JOptionPane.ERROR_MESSAGE);
                System.err.println("❌ Error al eliminar país (rollback): " + e.getMessage());
                return false;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error en la conexión al eliminar país: " + e.getMessage(), "Error DB", JOptionPane.ERROR_MESSAGE);
            System.err.println("❌ Error en la conexión al eliminar: " + e.getMessage());
            return false;
        }
    }

    /**
     * Inserta un nuevo país en la base de datos.
     * @param pais Objeto ModeloPais con los datos del país a insertar.
     * @return true si la inserción fue exitosa, false en caso contrario.
     */
    public static boolean insertarPais(ModeloPais pais) {
        String sql = "INSERT INTO country (Code, Name, Continent, Region, SurfaceArea, IndepYear, Population, LifeExpectancy, GNP, LocalName, GovernmentForm, HeadOfState, Capital) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // Establece los parámetros de la consulta de inserción
            ps.setString(1, pais.getCodigo());
            ps.setString(2, pais.getNombre());
            ps.setString(3, pais.getContinente());
            ps.setString(4, pais.getRegion());
            // ps.setObject permite pasar null si el valor en el objeto ModeloPais es null
            ps.setObject(5, pais.getSuperficie()); 
            ps.setObject(6, pais.getAnioIndependencia());
            ps.setObject(7, pais.getPoblacion());
            ps.setObject(8, pais.getExpectativaVida());
            ps.setObject(9, pais.getPnb());
            ps.setString(10, pais.getNombreLocal());
            ps.setString(11, pais.getFormaGobierno());
            ps.setString(12, pais.getCabezaEstado());
            ps.setObject(13, pais.getCapitalID()); // ID de la capital

            int filasAfectadas = ps.executeUpdate(); // Ejecuta la inserción
            return filasAfectadas > 0; // Devuelve true si se insertó al menos una fila

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al insertar país: " + e.getMessage(), "Error DB", JOptionPane.ERROR_MESSAGE);
            System.err.println("❌ Error al insertar país: " + e.getMessage());
            return false;
        }
    }

    /**
     * Modifica los datos de un país existente en la base de datos.
     * @param pais Objeto ModeloPais con los datos actualizados del país. El código del país no se modifica.
     * @return true si la modificación fue exitosa, false en caso contrario.
     */
    public static boolean modificarPais(ModeloPais pais) {
        String sql = "UPDATE country SET Name = ?, Continent = ?, Region = ?, SurfaceArea = ?, IndepYear = ?, Population = ?, LifeExpectancy = ?, GNP = ?, LocalName = ?, GovernmentForm = ?, HeadOfState = ?, Capital = ? " +
                     "WHERE Code = ?"; // La cláusula WHERE usa el código del país
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // Establece los parámetros para la actualización
            ps.setString(1, pais.getNombre());
            ps.setString(2, pais.getContinente());
            ps.setString(3, pais.getRegion());
            ps.setObject(4, pais.getSuperficie());
            ps.setObject(5, pais.getAnioIndependencia());
            ps.setObject(6, pais.getPoblacion());
            ps.setObject(7, pais.getExpectativaVida());
            ps.setObject(8, pais.getPnb());
            ps.setString(9, pais.getNombreLocal());
            ps.setString(10, pais.getFormaGobierno());
            ps.setString(11, pais.getCabezaEstado());
            ps.setObject(12, pais.getCapitalID());
            ps.setString(13, pais.getCodigo()); // Parámetro para la cláusula WHERE

            int filasAfectadas = ps.executeUpdate(); // Ejecuta la modificación
            return filasAfectadas > 0; // Devuelve true si se modificó al menos una fila

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al modificar país: " + e.getMessage(), "Error DB", JOptionPane.ERROR_MESSAGE);
            System.err.println("❌ Error al modificar país: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Obtiene una lista de todos los continentes distintos presentes en la tabla 'country'.
     * @return Una lista de Strings, cada uno representando un nombre de continente.
     */
    public static List<String> obtenerContinentes() {
        List<String> continentes = new ArrayList<>();
        String sql = "SELECT DISTINCT Continent FROM country ORDER BY Continent ASC"; // DISTINCT para evitar duplicados
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                continentes.add(rs.getString("Continent")); // Añade cada continente a la lista
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al obtener continentes: " + e.getMessage());
        }
        return continentes; // Devuelve la lista de continentes
    }
}
