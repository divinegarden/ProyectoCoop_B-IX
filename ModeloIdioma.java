// Archivo: ModeloIdioma.java
package Actividad;

/**
 * Clase que representa un idioma asociado a un país, tal como se almacena 
 * en la tabla 'countrylanguage'.
 * Contiene el nombre del idioma, si es oficial y el porcentaje de hablantes.
 */
public class ModeloIdioma {

    private String idioma;       // Nombre del idioma (Ej: "Spanish")
    private boolean esOficial;   // true si el idioma es oficial en el país, false si no
    private double porcentaje;   // Porcentaje de la población del país que habla este idioma

    /**
     * Constructor para crear un objeto ModeloIdioma.
     * @param idioma El nombre del idioma.
     * @param esOficial true si es oficial, false en caso contrario.
     * @param porcentaje El porcentaje de hablantes.
     */
    public ModeloIdioma(String idioma, boolean esOficial, double porcentaje) {
        this.idioma = idioma;
        this.esOficial = esOficial;
        this.porcentaje = porcentaje;
    }

    // Getters y Setters para acceder y modificar los atributos de la clase.

    public String getIdioma() {
        return idioma;
    }

    public void setIdioma(String idioma) {
        this.idioma = idioma;
    }

    public boolean isEsOficial() {
        return esOficial;
    }

    public void setEsOficial(boolean esOficial) {
        this.esOficial = esOficial;
    }

    public double getPorcentaje() {
        return porcentaje;
    }

    public void setPorcentaje(double porcentaje) {
        this.porcentaje = porcentaje;
    }

    /**
     * Devuelve una representación en String del objeto ModeloIdioma.
     * Útil para debugging o para mostrar información del idioma de forma simple.
     * @return Una cadena formateada con los datos del idioma.
     */
    @Override
    public String toString() {
        String texto = idioma + " (";
        if (esOficial) {
            texto += "Oficial";
        } else {
            texto += "No Oficial";
        }
        texto += ", " + porcentaje + "%)";
        return texto;
    }
}
