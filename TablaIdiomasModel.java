// Archivo: TablaIdiomasModel.java
package Actividad;

import javax.swing.table.AbstractTableModel;
import java.util.List;

/**
 * Modelo personalizado para la JTable que muestra los idiomas.
 * Hereda de AbstractTableModel para proporcionar los datos y la estructura de la tabla.
 */
public class TablaIdiomasModel extends AbstractTableModel {

    // Nombres de las columnas para la tabla de idiomas
    private final String[] columnas = {"Lengua", "Oficial", "%"};
    // Lista de objetos ModeloIdioma que contiene los datos a mostrar
    private List<ModeloIdioma> listaIdiomas;

    /**
     * Constructor que inicializa el modelo con una lista de idiomas.
     * @param lista La lista de objetos ModeloIdioma.
     */
    public TablaIdiomasModel(List<ModeloIdioma> lista) {
        this.listaIdiomas = lista;
    }

    /**
     * Actualiza los datos del modelo con una nueva lista de idiomas y notifica
     * a la tabla que los datos han cambiado para que se repinte.
     * @param nuevaLista La nueva lista de ModeloIdioma.
     */
    public void setDatos(List<ModeloIdioma> nuevaLista) {
        this.listaIdiomas = nuevaLista;
        fireTableDataChanged(); // Notifica a la JTable que los datos han cambiado
    }

    /**
     * Devuelve el número de filas en la tabla (igual al tamaño de la lista de idiomas).
     * @return El número de filas.
     */
    @Override
    public int getRowCount() {
        return listaIdiomas.size();
    }

    /**
     * Devuelve el número de columnas en la tabla.
     * @return El número de columnas (definido en el array 'columnas').
     */
    @Override
    public int getColumnCount() {
        return columnas.length;
    }

    /**
     * Devuelve el nombre de la columna en el índice especificado.
     * @param col El índice de la columna.
     * @return El nombre de la columna.
     */
    @Override
    public String getColumnName(int col) {
        return columnas[col];
    }

    /**
     * Devuelve el valor que se mostrará en la celda especificada por fila y columna.
     * @param fila El índice de la fila.
     * @param col El índice de la columna.
     * @return El objeto valor para la celda.
     */
    @Override
    public Object getValueAt(int fila, int col) {
        ModeloIdioma idioma = listaIdiomas.get(fila); // Obtiene el objeto idioma para la fila actual
        switch (col) { // Determina qué dato devolver según la columna
            case 0: return idioma.getIdioma(); // Columna "Lengua"
            case 1: return idioma.isEsOficial() ? "T" : "F"; // Columna "Oficial" (muestra 'T' o 'F')
            case 2: return idioma.getPorcentaje(); // Columna "%"
            default: return null; // En caso de índice de columna inválido
        }
    }

    /**
     * Define si una celda es editable. En este modelo, ninguna celda es editable.
     * @param rowIndex El índice de la fila.
     * @param columnIndex El índice de la columna.
     * @return false, ya que las celdas no son editables.
     */
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false; // Hace que todas las celdas de la tabla no sean editables directamente
    }

    /**
     * Método de conveniencia para obtener el objeto ModeloIdioma completo en una fila específica.
     * Útil si se necesita acceder a todos los datos del idioma de una fila seleccionada.
     * @param fila El índice de la fila.
     * @return El objeto ModeloIdioma en esa fila.
     */
    public ModeloIdioma getIdiomaAt(int fila) {
        if (fila >= 0 && fila < listaIdiomas.size()) {
            return listaIdiomas.get(fila);
        }
        return null;
    }
}