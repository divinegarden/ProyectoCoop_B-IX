// Archivo: Controlador.java
package Actividad;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Clase Controlador: maneja la lógica de la aplicación, interactuando entre la Vista (Paises.java)
 * y los Modelos/Gestores de datos (ModeloPais, GestorPaises, ModeloIdioma, GestorIdiomas).
 * Controla el estado de la aplicación (navegando, insertando, modificando) y actualiza la GUI.
 */
public class Controlador {

    private final Paises vista; // Referencia a la interfaz gráfica (JFrame)
    private enum Estado { NAVEGANDO, INSERTANDO_PAIS, MODIFICANDO_PAIS, INSERTANDO_LENGUA } // Estados posibles de la aplicación
    private Estado estadoActual; // Estado actual de la aplicación
    private ModeloPais paisActualParaModificar; // Almacena el país seleccionado para restaurar en caso de cancelación de modificación

    /**
     * Constructor del Controlador.
     * @param vista La instancia de la clase Paises (la GUI).
     */
    public Controlador(Paises vista) {
        this.vista = vista;
        inicializarEventos(); // Configura los listeners para los componentes de la GUI
        configurarEstadoInicial(); // Establece el estado inicial de la GUI y la aplicación
        cargarPaises(); // Carga la lista inicial de países en la tabla
        cargarComboBoxContinentes(); // Carga los continentes en el JComboBox
    }

    /**
     * Establece el estado inicial de la aplicación y la GUI.
     * Habilita/deshabilita botones y campos según corresponda.
     */
    private void configurarEstadoInicial() {
        this.estadoActual = Estado.NAVEGANDO; // Estado por defecto
        
        // Configuración de la barra de herramientas principal para países
        vista.getjButton1().setEnabled(true);  // Botón Añadir País habilitado
        vista.getjButton3().setEnabled(false); // Botón Modificar País deshabilitado (hasta seleccionar uno)
        vista.getjButton2().setEnabled(false); // Botón Borrar País deshabilitado (hasta seleccionar uno)
        vista.getjButton4().setEnabled(false); // Botón Guardar País deshabilitado
        vista.getjButton5().setEnabled(false); // Botón Cancelar País deshabilitado

        // Configuración de los campos de detalle del país
        habilitarCamposPais(false); // Campos no editables
        limpiarCamposPais(); // Limpiar los campos de texto

        // Configuración de la sección de lenguas
        vista.getjTable2().setModel(new TablaIdiomasModel(new ArrayList<>())); // Tabla de idiomas vacía
        habilitarCamposLengua(false); // Campos de lengua no editables
        limpiarCamposLengua(); // Limpiar campos de lengua
        vista.getjButton6().setEnabled(false); // Botón Añadir lengua deshabilitado (hasta seleccionar país)
        vista.getjButton7().setEnabled(false); // Botón Borrar lengua deshabilitado (hasta seleccionar país e idioma)
        vista.getjButton8().setEnabled(false); // Botón Aceptar lengua deshabilitado
        vista.getjButton9().setEnabled(false); // Botón Cancelar lengua deshabilitado
        
        // Habilitar la ordenación en la tabla de países al hacer clic en las cabeceras
        vista.getjTable1().setAutoCreateRowSorter(true);
    }
    
    /**
     * Carga los datos de los países (Código, Nombre, Continente, Región) en la JTable principal.
     * La tabla se ordena por nombre de país.
     */
    private void cargarPaises() {
        DefaultTableModel modelo = (DefaultTableModel) vista.getjTable1().getModel();
        modelo.setRowCount(0); // Limpia la tabla antes de cargar nuevos datos
        String sql = "SELECT Code, Name, Continent, Region FROM country ORDER BY Name";

        try (Connection conn = ConexionBD.conectar();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) { // Itera sobre los resultados de la consulta
                Vector<String> fila = new Vector<>(); // Crea un vector para la fila de la tabla
                fila.add(rs.getString("Code"));
                fila.add(rs.getString("Name"));
                fila.add(rs.getString("Continent"));
                fila.add(rs.getString("Region"));
                modelo.addRow(fila); // Añade la fila al modelo de la tabla
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(vista, "Error al cargar países: " + e.getMessage(), "Error DB", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Carga la lista de continentes distintos en el JComboBox de la interfaz.
     */
    private void cargarComboBoxContinentes() {
        List<String> continentes = GestorPaises.obtenerContinentes();
        DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) vista.getjComboBox1().getModel();
        model.removeAllElements(); // Limpia el ComboBox
        for (String continente : continentes) {
            model.addElement(continente); // Añade cada continente
        }
        vista.getjComboBox1().setSelectedIndex(-1); // Deja sin selección por defecto
    }

    /**
     * Carga los detalles completos de un país seleccionado en los campos de texto correspondientes de la GUI.
     * @param codigoPais El código del país cuyos detalles se van a cargar.
     */
    private void cargarDetallesPais(String codigoPais) {
        ModeloPais pais = GestorPaises.cargarDetallesPais(codigoPais); // Obtiene el objeto país
        if (pais != null) {
            // Rellena los campos de la sección "DATOS RELEVANTES"
            vista.getjTextField1().setText(pais.getCodigo());
            vista.getjTextField2().setText(pais.getNombre());
            vista.getjComboBox1().setSelectedItem(pais.getContinente());
            vista.getjTextField3().setText(pais.getRegion());
            vista.getjTextField4().setText(pais.getNombreLocal());
            vista.getjTextField5().setText(pais.getAnioIndependencia() != null ? String.valueOf(pais.getAnioIndependencia()) : "");
            vista.getjTextField6().setText(pais.getSuperficie() != null ? String.valueOf(pais.getSuperficie()) : "");
            
            // Rellena los campos de la sección "ECONOMÍA Y POBLACIÓN"
            vista.getjTextField7().setText(pais.getExpectativaVida() != null ? String.valueOf(pais.getExpectativaVida()) : "");
            vista.getjTextField8().setText(pais.getPoblacion() != null ? String.valueOf(pais.getPoblacion()) : "");
            vista.getjTextField9().setText(pais.getPnb() != null ? String.valueOf(pais.getPnb()) : "");
            vista.getjTextField10().setText(pais.getFormaGobierno());
            vista.getjTextField11().setText(pais.getCabezaEstado());
            
            // Rellena los campos de la capital (parte de "ECONOMÍA Y POBLACIÓN" en la GUI)
            vista.getjTextField12().setText(pais.getCapitalNombre());
            vista.getjTextField13().setText(pais.getCapitalDistrito());
            vista.getjTextField14().setText(pais.getCapitalPoblacion() != null ? String.valueOf(pais.getCapitalPoblacion()) : "");
            
            this.paisActualParaModificar = pais; // Guarda el estado actual del país para posible modificación/cancelación
        } else {
            limpiarCamposPais(); // Si no se encuentra el país, limpia los campos
            this.paisActualParaModificar = null;
        }
    }

    /**
     * Carga los idiomas hablados en un país seleccionado en la JTable de idiomas.
     * @param codigoPais El código del país cuyos idiomas se van a cargar.
     */
    private void cargarIdiomas(String codigoPais) {
        List<ModeloIdioma> idiomas = GestorIdiomas.cargarIdiomas(codigoPais);
        TablaIdiomasModel modelo = new TablaIdiomasModel(idiomas);
        vista.getjTable2().setModel(modelo); // Establece el nuevo modelo en la tabla de idiomas
        
        // Habilita los botones para gestionar lenguas ahora que un país está seleccionado
        vista.getjButton6().setEnabled(true); // Añadir lengua
        vista.getjButton7().setEnabled(true); // Borrar lengua
        habilitarCamposLengua(false); // Campos de edición de lengua deshabilitados por defecto
        limpiarCamposLengua(); // Limpia los campos de edición de lengua
    }

    /**
     * Inicializa todos los listeners de eventos para los componentes interactivos de la GUI.
     */
    private void inicializarEventos() {
        // Listener para la selección de filas en la tabla de países
        vista.getjTable1().getSelectionModel().addListSelectionListener(e -> {
            // Se ejecuta solo cuando la selección se ha estabilizado y estamos en modo NAVEGANDO
            if (!e.getValueIsAdjusting() && estadoActual == Estado.NAVEGANDO) {
                int filaSeleccionadaVista = vista.getjTable1().getSelectedRow();
                if (filaSeleccionadaVista != -1) {
                    // Convierte el índice de la fila de la vista al índice del modelo (importante si la tabla está ordenada)
                    int filaModelo = vista.getjTable1().convertRowIndexToModel(filaSeleccionadaVista);
                    String codigoPais = vista.getjTable1().getModel().getValueAt(filaModelo, 0).toString();
                    
                    cargarDetallesPais(codigoPais); // Carga los detalles del país seleccionado
                    cargarIdiomas(codigoPais); // Carga los idiomas del país seleccionado
                    
                    // Habilita botones de Modificar y Borrar país
                    vista.getjButton3().setEnabled(true); 
                    vista.getjButton2().setEnabled(true); 
                } else { // Si no hay fila seleccionada
                    limpiarCamposPais();
                    ((DefaultTableModel) vista.getjTable2().getModel()).setRowCount(0); // Limpia tabla de idiomas
                    // Deshabilita botones que dependen de una selección
                    vista.getjButton3().setEnabled(false);
                    vista.getjButton2().setEnabled(false);
                    vista.getjButton6().setEnabled(false); 
                    vista.getjButton7().setEnabled(false); 
                }
            }
        });

        // Listeners para los botones de CRUD de Países
        vista.getjButton1().addActionListener(e -> prepararParaInsertarPais()); // Botón Añadir
        vista.getjButton3().addActionListener(e -> prepararParaModificarPais()); // Botón Modificar
        vista.getjButton4().addActionListener(e -> guardarPais()); // Botón Guardar
        vista.getjButton5().addActionListener(e -> cancelarAccionPais()); // Botón Cancelar
        vista.getjButton2().addActionListener(e -> eliminarPais()); // Botón Borrar
        
        // Listeners para los botones de CRUD de Lenguas
        vista.getjButton6().addActionListener(e -> prepararParaInsertarLengua()); // Botón Añadir Lengua
        vista.getjButton8().addActionListener(e -> guardarLengua()); // Botón Aceptar Lengua
        vista.getjButton9().addActionListener(e -> cancelarEdicionLengua()); // Botón Cancelar Lengua
        vista.getjButton7().addActionListener(e -> eliminarLengua()); // Botón Borrar Lengua
    }

    // --- Lógica de Estados y Acciones para PAÍSES ---

    /**
     * Prepara la GUI para la inserción de un nuevo país.
     * Cambia el estado de la aplicación a INSERTANDO_PAIS.
     */
    private void prepararParaInsertarPais() {
        estadoActual = Estado.INSERTANDO_PAIS;
        limpiarCamposPais(); // Limpia todos los campos de detalle del país
        habilitarCamposPais(true); // Hace los campos editables
        
        vista.getjTable1().clearSelection(); // Deselecciona cualquier fila en la tabla de países
        vista.getjTable1().setEnabled(false); // Deshabilita la tabla de países durante la inserción
        
        // Configura el estado de los botones de la barra de herramientas principal
        vista.getjButton1().setEnabled(false); // Añadir
        vista.getjButton3().setEnabled(false); // Modificar
        vista.getjButton2().setEnabled(false); // Borrar
        vista.getjButton4().setEnabled(true);  // Guardar (habilitado)
        vista.getjButton5().setEnabled(true);  // Cancelar (habilitado)
        
        // Deshabilita la gestión de lenguas durante la inserción de un país
        vista.getjButton6().setEnabled(false);
        vista.getjButton7().setEnabled(false);
        ((DefaultTableModel) vista.getjTable2().getModel()).setRowCount(0); // Limpia tabla de idiomas
    }

    /**
     * Prepara la GUI para la modificación de un país existente.
     * Cambia el estado de la aplicación a MODIFICANDO_PAIS.
     */
    private void prepararParaModificarPais() {
        if (paisActualParaModificar == null) return; // No hacer nada si no hay país seleccionado
        estadoActual = Estado.MODIFICANDO_PAIS;
        habilitarCamposPais(true); // Hace los campos editables
        vista.getjTextField1().setEditable(false); // El código del país (PK) no se puede modificar

        vista.getjTable1().setEnabled(false); // Deshabilita la tabla de países
        
        // Configura el estado de los botones
        vista.getjButton1().setEnabled(false);
        vista.getjButton3().setEnabled(false);
        vista.getjButton2().setEnabled(false);
        vista.getjButton4().setEnabled(true); // Guardar (habilitado)
        vista.getjButton5().setEnabled(true); // Cancelar (habilitado)

        // Deshabilita la gestión de lenguas
        vista.getjButton6().setEnabled(false);
        vista.getjButton7().setEnabled(false);
    }
    
    /**
     * Guarda los datos del país (ya sea una inserción o una modificación) en la base de datos.
     * Realiza validaciones básicas antes de proceder.
     */
    private void guardarPais() {
        // Validación: Código y Nombre no pueden estar vacíos
        if (vista.getjTextField1().getText().trim().isEmpty() || vista.getjTextField2().getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(vista, "El código y el nombre del país son obligatorios.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // Validación: Código no más de 3 caracteres (según esquema de BD 'world')
        if (estadoActual == Estado.INSERTANDO_PAIS && vista.getjTextField1().getText().trim().length() > 3) {
             JOptionPane.showMessageDialog(vista, "El código del país no puede tener más de 3 caracteres.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Crea un objeto ModeloPais y lo rellena con los datos de la GUI
        ModeloPais pais = new ModeloPais();
        pais.setCodigo(vista.getjTextField1().getText().trim().toUpperCase()); // Código en mayúsculas
        pais.setNombre(vista.getjTextField2().getText().trim());
        pais.setContinente(vista.getjComboBox1().getSelectedItem() != null ? vista.getjComboBox1().getSelectedItem().toString() : null);
        pais.setRegion(vista.getjTextField3().getText().trim());
        pais.setNombreLocal(vista.getjTextField4().getText().trim());

        // Conversión y validación de campos numéricos
        try {
            // Solo intenta parsear si el campo no está vacío, sino, se considera null (o 0 para primitivos si es necesario)
            if (!vista.getjTextField5().getText().trim().isEmpty()) pais.setAnioIndependencia(Integer.parseInt(vista.getjTextField5().getText().trim()));
            if (!vista.getjTextField6().getText().trim().isEmpty()) pais.setSuperficie(Double.parseDouble(vista.getjTextField6().getText().trim()));
            if (!vista.getjTextField7().getText().trim().isEmpty()) pais.setExpectativaVida(Double.parseDouble(vista.getjTextField7().getText().trim()));
            if (!vista.getjTextField8().getText().trim().isEmpty()) pais.setPoblacion(Integer.parseInt(vista.getjTextField8().getText().trim()));
            if (!vista.getjTextField9().getText().trim().isEmpty()) pais.setPnb(Double.parseDouble(vista.getjTextField9().getText().trim()));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(vista, "Verifique los campos numéricos (Año, Superficie, Exp. Vida, Población, PNB).\nDeben ser números válidos o estar vacíos si se permiten nulos.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        pais.setFormaGobierno(vista.getjTextField10().getText().trim());
        pais.setCabezaEstado(vista.getjTextField11().getText().trim());
        
        // La gestión del ID de la capital es más compleja y se omite en esta simplificación.
        // Para una implementación completa, se necesitaría seleccionar una ciudad existente o crear una nueva.
        // pais.setCapitalID( obtenerIDCapitalDeAlgunModo() ); 


        boolean exito = false;
        if (estadoActual == Estado.INSERTANDO_PAIS) {
            exito = GestorPaises.insertarPais(pais);
            if (exito) JOptionPane.showMessageDialog(vista, "País insertado correctamente.");
        } else if (estadoActual == Estado.MODIFICANDO_PAIS) {
            exito = GestorPaises.modificarPais(pais);
             if (exito) JOptionPane.showMessageDialog(vista, "País modificado correctamente.");
        }

        if (exito) {
            cargarPaises(); // Recarga la tabla de países para reflejar los cambios
        }
        cancelarAccionPais(); // Restaura la GUI al estado de navegación
    }
    
    /**
     * Elimina el país seleccionado de la base de datos, previa confirmación del usuario.
     * También elimina los idiomas asociados a ese país.
     */
    private void eliminarPais() {
        int filaSeleccionadaVista = vista.getjTable1().getSelectedRow();
        if (filaSeleccionadaVista == -1) {
            JOptionPane.showMessageDialog(vista, "Por favor, seleccione un país para borrar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int filaModelo = vista.getjTable1().convertRowIndexToModel(filaSeleccionadaVista);
        String codigoPais = vista.getjTable1().getModel().getValueAt(filaModelo, 0).toString();
        String nombrePais = vista.getjTable1().getModel().getValueAt(filaModelo, 1).toString();

        // Pide confirmación antes de borrar
        int confirmacion = JOptionPane.showConfirmDialog(vista, 
                "¿Seguro que quieres borrar el país '" + nombrePais + "' (código: " + codigoPais + ") y todos sus idiomas asociados?",
                "Confirmar Borrado", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirmacion == JOptionPane.YES_OPTION) {
            if (GestorPaises.eliminarPais(codigoPais)) {
                JOptionPane.showMessageDialog(vista, "País eliminado correctamente.");
                cargarPaises(); // Recarga la tabla de países
                limpiarCamposPais(); // Limpia los detalles
                ((DefaultTableModel) vista.getjTable2().getModel()).setRowCount(0); // Limpia la tabla de idiomas
                configurarEstadoInicial(); // Restaura el estado de los botones
            } else {
                JOptionPane.showMessageDialog(vista, "No se pudo eliminar el país.", "Error de Eliminación", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Cancela la acción actual de inserción o modificación de un país.
     * Restaura la GUI al estado de navegación.
     */
    private void cancelarAccionPais() {
        estadoActual = Estado.NAVEGANDO;
        habilitarCamposPais(false); // Bloquea los campos de detalle del país
        
        vista.getjTable1().setEnabled(true); // Habilita la tabla de países
        
        // Si había un país seleccionado antes de la acción, recarga sus datos originales
        int filaSeleccionadaVista = vista.getjTable1().getSelectedRow();
        if (filaSeleccionadaVista != -1 && paisActualParaModificar != null) {
             // Usar el código del país que se estaba modificando/viendo para recargar
             cargarDetallesPais(paisActualParaModificar.getCodigo());
             cargarIdiomas(paisActualParaModificar.getCodigo());
        } else { // Si no había selección o se estaba insertando, limpia todo
            limpiarCamposPais();
            ((DefaultTableModel) vista.getjTable2().getModel()).setRowCount(0);
        }
        
        // Restaura el estado de los botones de la barra de herramientas principal
        vista.getjButton1().setEnabled(true); // Añadir
        vista.getjButton3().setEnabled(filaSeleccionadaVista != -1); // Modificar (solo si hay selección)
        vista.getjButton2().setEnabled(filaSeleccionadaVista != -1); // Borrar (solo si hay selección)
        vista.getjButton4().setEnabled(false); // Guardar
        vista.getjButton5().setEnabled(false); // Cancelar

        // Habilita/deshabilita botones de lengua según si hay país seleccionado
        if (filaSeleccionadaVista != -1) {
            vista.getjButton6().setEnabled(true); 
            vista.getjButton7().setEnabled(true); 
        } else {
            vista.getjButton6().setEnabled(false);
            vista.getjButton7().setEnabled(false);
        }
    }

    // --- Lógica de Estados y Acciones para LENGUAS ---

    /**
     * Prepara la GUI para la inserción de una nueva lengua para el país actualmente seleccionado.
     */
    private void prepararParaInsertarLengua() {
        if (vista.getjTable1().getSelectedRow() == -1) {
             JOptionPane.showMessageDialog(vista, "Debe seleccionar un país primero para añadirle un idioma.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        estadoActual = Estado.INSERTANDO_LENGUA;
        limpiarCamposLengua(); // Limpia los campos de edición de lengua
        habilitarCamposLengua(true); // Hace los campos de lengua editables
        
        // Configura los botones de la barra de herramientas de lenguas
        vista.getjButton8().setEnabled(true); // Aceptar Lengua
        vista.getjButton9().setEnabled(true); // Cancelar Lengua
        vista.getjButton6().setEnabled(false); // Deshabilita "Añadir Lengua" para evitar inserciones múltiples simultáneas
        vista.getjButton7().setEnabled(false); // Deshabilita "Borrar Lengua"
    }
    
    /**
     * Guarda la nueva lengua introducida en la base de datos, asociándola al país seleccionado.
     * Corresponde a la acción del botón "Aceptar" de la sección de lenguas.
     */
    private void guardarLengua() { 
        int filaPaisVista = vista.getjTable1().getSelectedRow();
        if (filaPaisVista == -1) { 
            JOptionPane.showMessageDialog(vista, "Selecciona un país primero."); 
            return; 
        }
        // Obtiene el código del país del modelo de la tabla (por si está ordenada)
        String codigoPais = vista.getjTable1().getModel().getValueAt(vista.getjTable1().convertRowIndexToModel(filaPaisVista), 0).toString();
        String idiomaNombre = vista.getjTextField15().getText().trim(); // Nombre de la lengua
        
        if(idiomaNombre.isEmpty()){ // Validación: nombre de lengua no vacío
            JOptionPane.showMessageDialog(vista, "El nombre del idioma no puede estar vacío.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean oficial = vista.getjCheckBox1().isSelected(); // Si es oficial
        double porcentaje;
        try {
            porcentaje = Double.parseDouble(vista.getjTextField16().getText().trim()); // Porcentaje de hablantes
            if (porcentaje < 0 || porcentaje > 100) { // Validación: porcentaje entre 0 y 100
                JOptionPane.showMessageDialog(vista, "El porcentaje debe estar entre 0 y 100.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch(NumberFormatException e) { // Validación: porcentaje es un número
            JOptionPane.showMessageDialog(vista, "El porcentaje debe ser un número válido.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
            return;
        }

        ModeloIdioma nuevoIdioma = new ModeloIdioma(idiomaNombre, oficial, porcentaje);
        if (GestorIdiomas.insertarIdioma(codigoPais, nuevoIdioma)) {
            JOptionPane.showMessageDialog(vista, "Idioma añadido correctamente.");
            cargarIdiomas(codigoPais); // Recarga la tabla de idiomas
        } else {
            JOptionPane.showMessageDialog(vista, "No se pudo añadir el idioma.", "Error de Inserción", JOptionPane.ERROR_MESSAGE);
        }
        cancelarEdicionLengua(); // Restaura la GUI de la sección de lenguas
        estadoActual = Estado.NAVEGANDO; // Vuelve al estado de navegación general
    }
    
    /**
     * Elimina la lengua seleccionada de la base de datos para el país actual.
     */
    private void eliminarLengua() {
        int filaPaisVista = vista.getjTable1().getSelectedRow(); 
        int filaIdiomaVista = vista.getjTable2().getSelectedRow(); // Índice en la vista de la tabla de idiomas
        
        if (filaPaisVista == -1 || filaIdiomaVista == -1) { 
            JOptionPane.showMessageDialog(vista, "Selecciona un país y un idioma para borrar."); 
            return; 
        }
        String codigoPais = vista.getjTable1().getModel().getValueAt(vista.getjTable1().convertRowIndexToModel(filaPaisVista), 0).toString();
        
        // Obtener el idioma del modelo de la tabla de idiomas (no de la vista, por si está ordenada)
        TablaIdiomasModel modeloIdiomas = (TablaIdiomasModel) vista.getjTable2().getModel();
        // Es importante obtener el índice correcto del modelo si la tabla de idiomas permite ordenación.
        // Si jTable2 no tiene un RowSorter, filaIdiomaVista y filaIdiomaModelo serán iguales.
        // Para este ejemplo, asumimos que jTable2 no se ordena o que el índice de vista y modelo coinciden.
        // Si jTable2 tuviera RowSorter: int filaIdiomaModelo = vista.getjTable2().convertRowIndexToModel(filaIdiomaVista);
        String idiomaNombre = modeloIdiomas.getIdiomaAt(filaIdiomaVista).getIdioma();


        int confirm = JOptionPane.showConfirmDialog(vista, "¿Seguro que quieres borrar el idioma '" + idiomaNombre + "' para este país?", "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (GestorIdiomas.eliminarIdioma(codigoPais, idiomaNombre)) {
                JOptionPane.showMessageDialog(vista, "Idioma eliminado correctamente.");
                cargarIdiomas(codigoPais); // Recarga la tabla de idiomas
            } else {
                 JOptionPane.showMessageDialog(vista, "No se pudo eliminar el idioma.", "Error de Eliminación", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Cancela la edición o inserción de una lengua.
     * Restaura la GUI de la sección de lenguas a su estado por defecto.
     */
    private void cancelarEdicionLengua() {
        limpiarCamposLengua(); // Limpia los campos de texto de lengua
        habilitarCamposLengua(false); // Bloquea los campos de texto de lengua
        
        // Restaura el estado de los botones de la barra de herramientas de lenguas
        vista.getjButton8().setEnabled(false); // Aceptar Lengua
        vista.getjButton9().setEnabled(false); // Cancelar Lengua
        
        // Solo rehabilita "Añadir" y "Borrar" lengua si hay un país seleccionado
        if (vista.getjTable1().getSelectedRow() != -1) {
            vista.getjButton6().setEnabled(true); // Añadir Lengua
            vista.getjButton7().setEnabled(true); // Borrar Lengua
        }
        estadoActual = Estado.NAVEGANDO; // Vuelve al estado de navegación general
    }

    // --- Métodos de utilidad para la GUI ---

    /**
     * Habilita o deshabilita la edición de los campos de detalle del país.
     * @param habilitar true para hacerlos editables, false para hacerlos de solo lectura.
     */
    private void habilitarCamposPais(boolean habilitar) {
        vista.getjTextField1().setEditable(habilitar); // Código
        vista.getjTextField2().setEditable(habilitar); // Nombre
        vista.getjComboBox1().setEnabled(habilitar);   // Continente
        vista.getjTextField3().setEditable(habilitar); // Región
        vista.getjTextField4().setEditable(habilitar); // Nombre Local
        vista.getjTextField5().setEditable(habilitar); // Año Indep.
        vista.getjTextField6().setEditable(habilitar); // Superficie
        vista.getjTextField7().setEditable(habilitar); // Exp. Vida
        vista.getjTextField8().setEditable(habilitar); // Población
        vista.getjTextField9().setEditable(habilitar); // PNB
        vista.getjTextField10().setEditable(habilitar);// Forma Gob.
        vista.getjTextField11().setEditable(habilitar);// Cabeza Estado
        // Los campos de la capital son siempre de solo lectura en esta implementación
        vista.getjTextField12().setEditable(false); 
        vista.getjTextField13().setEditable(false); 
        vista.getjTextField14().setEditable(false); 
    }
    
    /**
     * Limpia el contenido de todos los campos de texto de detalle del país.
     */
    private void limpiarCamposPais() {
        vista.getjTextField1().setText("");
        vista.getjTextField2().setText("");
        vista.getjComboBox1().setSelectedIndex(-1); // Sin selección
        vista.getjTextField3().setText("");
        vista.getjTextField4().setText("");
        vista.getjTextField5().setText("");
        vista.getjTextField6().setText("");
        vista.getjTextField7().setText("");
        vista.getjTextField8().setText("");
        vista.getjTextField9().setText("");
        vista.getjTextField10().setText("");
        vista.getjTextField11().setText("");
        vista.getjTextField12().setText("");
        vista.getjTextField13().setText("");
        vista.getjTextField14().setText("");
    }
    
    /**
     * Habilita o deshabilita la edición de los campos de lengua.
     * @param habilitar true para hacerlos editables, false para hacerlos de solo lectura.
     */
    private void habilitarCamposLengua(boolean habilitar) {
        vista.getjTextField15().setEditable(habilitar); // Nombre Lengua
        vista.getjTextField16().setEditable(habilitar); // Porcentaje
        vista.getjCheckBox1().setEnabled(habilitar);    // Oficial (Checkbox)
    }

    /**
     * Limpia el contenido de los campos de edición de lengua.
     */
    private void limpiarCamposLengua() {
        vista.getjTextField15().setText("");
        vista.getjTextField16().setText("");
        vista.getjCheckBox1().setSelected(false);
    }
}
