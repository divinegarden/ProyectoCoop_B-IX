// Archivo: TestConexion.java
package Actividad;

/**
 * Clase simple para probar la conexión a la base de datos.
 * Al ejecutar el método main, intentará conectar usando la clase ConexionBD.
 */
public class TestConexion {
    
    /**
     * Método principal para ejecutar la prueba de conexión.
     * @param args Argumentos de línea de comandos (no utilizados).
     */
    public static void main(String[] args) {
        // Llama al método estático 'conectar' de la clase ConexionBD.
        // Este método intentará establecer una conexión y mostrará un mensaje
        // en la consola indicando si fue exitosa o si hubo un error.
        ConexionBD.conectar(); 
    }
}