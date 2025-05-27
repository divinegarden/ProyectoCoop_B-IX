// Archivo: ModeloPais.java
package Actividad;

/**
 * Clase modelo para encapsular los datos de un país.
 * Contiene todos los campos relevantes de la tabla 'country' y 'city' (para la capital).
 * Se utilizan tipos de objeto (Integer, Double) para campos numéricos que pueden ser nulos.
 */
public class ModeloPais {

    // --- DATOS RELEVANTES ---
    private String codigo; // Código del país (PK)
    private String nombre; // Nombre del país
    private String continente; // Continente al que pertenece
    private String region; // Región específica del país
    private String nombreLocal; // Nombre del país en su idioma local
    private Integer anioIndependencia; // Año de independencia (puede ser nulo)
    private Double superficie;       // Superficie del país en km² (puede ser nulo)

    // --- ECONOMÍA Y POBLACIÓN ---
    private Double expectativaVida; // Expectativa de vida media (puede ser nulo)
    private Integer poblacion; // Población total del país (puede ser nulo)
    private Double pnb; // Producto Nacional Bruto (puede ser nulo)
    private String formaGobierno; // Forma de gobierno
    private String cabezaEstado; // Jefe de estado

    // --- CAPITAL ---
    private Integer capitalID; // ID de la ciudad capital (FK a la tabla city)
    private String capitalNombre; // Nombre de la ciudad capital
    private String capitalDistrito; // Distrito de la capital
    private Integer capitalPoblacion; // Población de la capital (puede ser nulo)

    /**
     * Constructor por defecto.
     */
    public ModeloPais() {}

    // Getters y Setters para todos los campos.
    // Estos métodos permiten acceder y modificar los valores de los atributos de la clase.

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getContinente() { return continente; }
    public void setContinente(String continente) { this.continente = continente; }

    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }

    public String getNombreLocal() { return nombreLocal; }
    public void setNombreLocal(String nombreLocal) { this.nombreLocal = nombreLocal; }

    public Integer getAnioIndependencia() { return anioIndependencia; }
    public void setAnioIndependencia(Integer anioIndependencia) { this.anioIndependencia = anioIndependencia; }

    public Double getSuperficie() { return superficie; }
    public void setSuperficie(Double superficie) { this.superficie = superficie; }

    public Double getExpectativaVida() { return expectativaVida; }
    public void setExpectativaVida(Double expectativaVida) { this.expectativaVida = expectativaVida; }

    public Integer getPoblacion() { return poblacion; }
    public void setPoblacion(Integer poblacion) { this.poblacion = poblacion; }

    public Double getPnb() { return pnb; }
    public void setPnb(Double pnb) { this.pnb = pnb; }

    public String getFormaGobierno() { return formaGobierno; }
    public void setFormaGobierno(String formaGobierno) { this.formaGobierno = formaGobierno; }

    public String getCabezaEstado() { return cabezaEstado; }
    public void setCabezaEstado(String cabezaEstado) { this.cabezaEstado = cabezaEstado; }
    
    public Integer getCapitalID() { return capitalID; }
    public void setCapitalID(Integer capitalID) { this.capitalID = capitalID; }

    public String getCapitalNombre() { return capitalNombre; }
    public void setCapitalNombre(String capitalNombre) { this.capitalNombre = capitalNombre; }

    public String getCapitalDistrito() { return capitalDistrito; }
    public void setCapitalDistrito(String capitalDistrito) { this.capitalDistrito = capitalDistrito; }

    public Integer getCapitalPoblacion() { return capitalPoblacion; }
    public void setCapitalPoblacion(Integer capitalPoblacion) { this.capitalPoblacion = capitalPoblacion; }
}
