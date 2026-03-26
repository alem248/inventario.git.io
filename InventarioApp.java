package Inventario.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import Inventario.DAO.ProductoDAO;
import Inventario.gui.utils.GUIUtils;
import Inventario.models.Producto;

import java.util.HashMap;
import java.util.List;
import java.awt.*;
import java.util.Map;

public class InventarioApp extends JFrame {
    private ProductoDAO productoDAO = new ProductoDAO();
    private List<Producto> inventario;
    private JTextField entryNombre;
    private JComboBox<String> comboTipo;
    private JTextField entryPrecio;
    private JTextField entryStock;
    private JTextField searchEntry;
    private JTable table;
    private DefaultTableModel tableModel;
    private JLabel productCount;
    //Varibales para el control de usuario
    private boolean esAdministrador = false;
    private String usuarioActual = "";
    private JLabel userLabel;
    private JButton logoutBtn;

    public InventarioApp() {
        if (!mostrarLogin()) {
            System.exit(0);
        }

        this.inventario = this.productoDAO.cargarInventario();
        this.setTitle("Gestión de Inventario Pro - Playtoy");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(1000, 700);
        this.setLocationRelativeTo(null);
        this.construirUI(); // PRIMERO construye la UI (inicializa productCount)
        this.actualizarTabla(); // LUEGO actualiza la tabla (ahora productCount no es null)
        this.setVisible(true);
    }
// metodo de usuario
private boolean mostrarLogin() {
    JPanel loginPanel = new JPanel(new GridLayout(3, 2, 5, 5));
    JTextField userField = new JTextField();
    JPasswordField passField = new JPasswordField();

    loginPanel.add(new JLabel("Usuario:"));
    loginPanel.add(userField);
    loginPanel.add(new JLabel("Contraseña:"));
    loginPanel.add(passField);
    loginPanel.add(new JLabel("")); // Espacio vacío
    loginPanel.add(new JLabel("")); // Espacio vacío

    // Usuarios predefinidos
    Map<String, String> usuarios = new HashMap<>();
    usuarios.put("admin", "admin123");
    usuarios.put("usuario", "user123");

    while (true) {
        int result = JOptionPane.showConfirmDialog(this, loginPanel, "Iniciar Sesión",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result != JOptionPane.OK_OPTION) {
            return false; // Usuario canceló
        }

        String usuario = userField.getText().trim();
        String password = new String(passField.getPassword());

        if (usuarios.containsKey(usuario) && usuarios.get(usuario).equals(password)) {
            this.usuarioActual = usuario;
            this.esAdministrador = usuario.equals("admin");
            return true;
        } else {
            GUIUtils.mostrarError("Usuario o contraseña incorrectos");
            userField.setText("");
            passField.setText("");
            userField.requestFocus();
        }
    }
}


    private void construirUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10)); //crea el borde
        mainPanel.setBorder(BorderFactory.createEmptyBorder(9, 9, 9, 9)); //agrega el margen interno
        mainPanel.setBackground(GUIUtils.LIGHT); //color de fondo
        mainPanel.add(this.construirHeader(), BorderLayout.NORTH); //agrega el encabezado en la parte superior
        mainPanel.add(this.construirPanelControl(), BorderLayout.CENTER); //centraliza el panel de control
        mainPanel.add(this.construirTablaInventario(), BorderLayout.SOUTH); //Agrega la tabla del inven abajo
        this.add(mainPanel); //agrega el panel princi al JFrame
    }

    private JPanel construirHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(GUIUtils.LIGHT);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        // Panel logo (existente)
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        logoPanel.setBackground(GUIUtils.LIGHT);
        JLabel logoLabel = new JLabel("\ud83d\udce6");
        logoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 24));
        logoLabel.setForeground(GUIUtils.PRIMARY);
        JLabel titleLabel = new JLabel("GESTIÓN DE INVENTARIO");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(GUIUtils.DARK);
        logoPanel.add(logoLabel);
        logoPanel.add(Box.createHorizontalStrut(10));
        logoPanel.add(titleLabel);

        // NUEVO: Panel de información del usuario
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        userPanel.setBackground(GUIUtils.LIGHT);

        String tipoUsuario = esAdministrador ? "Administrador" : "Usuario";
        userLabel = new JLabel(usuarioActual + " (" + tipoUsuario + ")");
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        userLabel.setForeground(GUIUtils.SECONDARY);

        logoutBtn = new JButton("Cerrar Sesión");
        logoutBtn.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        logoutBtn.setBackground(GUIUtils.DANGER);
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFocusPainted(false);
        logoutBtn.addActionListener(e -> cerrarSesion());

        userPanel.add(userLabel);
        userPanel.add(Box.createHorizontalStrut(10));
        userPanel.add(logoutBtn);

        headerPanel.add(logoPanel, BorderLayout.WEST);
        headerPanel.add(userPanel, BorderLayout.EAST);

        return headerPanel;
    }

    //metodo para cerrar sesion
    private void cerrarSesion() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Está seguro que desea cerrar sesión?",
                "Confirmar cierre de sesión",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            this.dispose();
            new InventarioApp().setVisible(true);
        }
    }


    private JPanel construirPanelControl() {
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        controlPanel.setBorder(BorderFactory.createTitledBorder("Control de Productos"));
        controlPanel.setBackground(Color.WHITE);


        JPanel formPanel = new JPanel(new GridLayout(5, 2, 5, 5));
        formPanel.setBackground(Color.WHITE);

        formPanel.add(new JLabel("Nombre:"));
        this.entryNombre = new JTextField();
        formPanel.add(this.entryNombre);

        formPanel.add(new JLabel("Tipo de Juguete:"));
        String[] tiposJuguetes = {"Figuras de Acción", "Juegos Lúdicos", "Peluches", "Vehículos", "Construcción", "Electrónicos"};
        this.comboTipo = new JComboBox<>(tiposJuguetes);
        formPanel.add(this.comboTipo);


        formPanel.add(new JLabel("Precio:"));
        this.entryPrecio = new JTextField();
        formPanel.add(this.entryPrecio);

        formPanel.add(new JLabel("Stock:"));
        this.entryStock = new JTextField();
        formPanel.add(this.entryStock);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(Color.WHITE);
        JButton addBtn = GUIUtils.crearBotonEstilizado("Agregar Producto", GUIUtils.SUCCESS);
        addBtn.addActionListener((e) -> this.agregarProducto());

        JButton clearBtn = GUIUtils.crearBotonEstilizado("Limpiar Campos", GUIUtils.SECONDARY);
        clearBtn.addActionListener((e) -> this.limpiarCampos());

        // NUEVO: Botón para filtrar por tipo
        JButton filterByTypeBtn = GUIUtils.crearBotonEstilizado("Filtrar por Tipo", GUIUtils.DANGER);
        filterByTypeBtn.addActionListener((e) -> this.filtrarPorTipo());

        buttonPanel.add(addBtn);
        buttonPanel.add(clearBtn);
        buttonPanel.add(filterByTypeBtn);

        JPanel searchPanel = new JPanel(new BorderLayout(5, 0));
        searchPanel.setBackground(Color.WHITE);
        this.searchEntry = new JTextField();
        JButton searchBtn = GUIUtils.crearBotonEstilizado("Buscar", GUIUtils.PRIMARY);
        JButton showAllBtn = GUIUtils.crearBotonEstilizado("Mostrar Todos", GUIUtils.WARNING);
        showAllBtn.addActionListener((e) -> this.actualizarTabla());
        JPanel searchButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchButtonPanel.setBackground(Color.WHITE);
        searchButtonPanel.add(searchBtn);
        searchButtonPanel.add(showAllBtn);
        searchPanel.add(this.searchEntry, BorderLayout.CENTER);
        searchPanel.add(searchButtonPanel, BorderLayout.EAST);

        controlPanel.add(formPanel);
        controlPanel.add(Box.createVerticalStrut(10));
        controlPanel.add(buttonPanel);
        controlPanel.add(Box.createVerticalStrut(10));
        controlPanel.add(searchPanel);

        return controlPanel;
    }
    private void cargarProductoParaEditar() {
        int selectedRow = this.table.getSelectedRow();
        if (!esAdministrador) {
            GUIUtils.mostrarError("No tiene permisos para editar productos");
            return;
        }
        if (selectedRow == -1) {
            GUIUtils.mostrarError("Seleccione un producto para editar");
            return;
        }

        try {
            int productoId = (Integer) this.tableModel.getValueAt(selectedRow, 0);
            String productoTipo = (String) this.tableModel.getValueAt(selectedRow, 2);

            // Obtener el producto directamente desde la base de datos
            Producto productoAEditar = productoDAO.obtenerProductoPorIdYTipo(productoId, productoTipo);

            if (productoAEditar != null) {
                this.mostrarDialogoEdicion(productoAEditar);
            } else {
                GUIUtils.mostrarError("Producto no encontrado en la base de datos");
            }
        } catch (Exception e) {
            GUIUtils.mostrarError("Error al cargar producto para editar: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void mostrarDialogoEdicion(Producto producto) {
        // Crear diálogo de edición
        JDialog editDialog = new JDialog(this, "Editar Producto", true);
        editDialog.setLayout(new BorderLayout(10, 10));
        editDialog.setSize(400, 300);
        editDialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridLayout(5, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Campos de edición
        JTextField nombreField = new JTextField(producto.getNombre());
        JComboBox<String> tipoCombo = new JComboBox<>(new String[]{"Figuras de Acción", "Juegos Lúdicos", "Peluches", "Vehículos", "Construcción", "Electrónicos"});
        tipoCombo.setSelectedItem(producto.getTipo());
        JTextField precioField = new JTextField(String.valueOf(producto.getPrecio()));
        JTextField stockField = new JTextField(String.valueOf(producto.getStock()));

        // Hacer que el tipo no sea editable (podría complicar la lógica de mover entre tablas)
        tipoCombo.setEnabled(false);

        formPanel.add(new JLabel("Nombre:"));
        formPanel.add(nombreField);
        formPanel.add(new JLabel("Tipo:"));
        formPanel.add(tipoCombo);
        formPanel.add(new JLabel("Precio:"));
        formPanel.add(precioField);
        formPanel.add(new JLabel("Stock:"));
        formPanel.add(stockField);

        // Panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton guardarBtn = GUIUtils.crearBotonEstilizado("Guardar Cambios", GUIUtils.SUCCESS);
        JButton cancelarBtn = GUIUtils.crearBotonEstilizado("Cancelar", GUIUtils.SECONDARY);

        guardarBtn.addActionListener(e -> {
            if (actualizarProducto(producto, nombreField.getText(), precioField.getText(), stockField.getText())) {
                editDialog.dispose();
                actualizarTabla();
            }
        });

        cancelarBtn.addActionListener(e -> editDialog.dispose());

        buttonPanel.add(guardarBtn);
        buttonPanel.add(cancelarBtn);

        editDialog.add(formPanel, BorderLayout.CENTER);
        editDialog.add(buttonPanel, BorderLayout.SOUTH);
        editDialog.setVisible(true);
    }

    private boolean actualizarProducto(Producto producto, String nuevoNombre, String nuevoPrecio, String nuevoStock) {
        try {
            // Validar campos
            if (nuevoNombre.trim().isEmpty() || nuevoPrecio.trim().isEmpty() || nuevoStock.trim().isEmpty()) {
                GUIUtils.mostrarError("Todos los campos son obligatorios");
                return false;
            }

            double precio = Double.parseDouble(nuevoPrecio);
            int stock = Integer.parseInt(nuevoStock);

            if (precio < 0 || stock < 0) {
                GUIUtils.mostrarError("Precio y Stock deben ser valores positivos");
                return false;
            }

            // Actualizar el producto
            producto.setNombre(nuevoNombre.trim());
            producto.setPrecio(precio);
            producto.setStock(stock);

            if (productoDAO.actualizarProducto(producto)) {
                GUIUtils.mostrarInfo("Producto actualizado correctamente");
                return true;
            } else {
                GUIUtils.mostrarError("Error al actualizar el producto");
                return false;
            }

        } catch (NumberFormatException e) {
            GUIUtils.mostrarError("Precio y Stock deben ser valores numéricos válidos");
            return false;
        } catch (Exception e) {
            GUIUtils.mostrarError("Error al actualizar producto: " + e.getMessage());
            return false;
        }
    }



    private JScrollPane construirTablaInventario() {
        String[] columnNames = new String[]{"ID", "Nombre del Producto", "Tipo", "Precio ($)", "Stock", "Fecha Registro"};
        this.tableModel = new DefaultTableModel(columnNames, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        this.table = new JTable(this.tableModel);
        this.table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.table.getTableHeader().setReorderingAllowed(false);
        this.table.getColumnModel().getColumn(0).setPreferredWidth(60);
        this.table.getColumnModel().getColumn(1).setPreferredWidth(250);
        this.table.getColumnModel().getColumn(2).setPreferredWidth(120);
        this.table.getColumnModel().getColumn(3).setPreferredWidth(80);
        this.table.getColumnModel().getColumn(4).setPreferredWidth(80);
        this.table.getColumnModel().getColumn(5).setPreferredWidth(150);

        // Agregar listener para doble clic - MODIFICADO
        this.table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2 && esAdministrador) { // === NUEVA CONDICIÓN ===
                    cargarProductoParaEditar();
                }
            }
        });

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("Inventario Actual"));
        JScrollPane scrollPane = new JScrollPane(this.table);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton deleteBtn = GUIUtils.crearBotonEstilizado("Eliminar Seleccionado", GUIUtils.DANGER);
        deleteBtn.addActionListener((e) -> this.eliminarProducto());
        deleteBtn.setEnabled(esAdministrador); // === NUEVA LÍNEA ===

        // NUEVO: Botón para editar producto
        JButton editBtn = GUIUtils.crearBotonEstilizado("Editar Seleccionado", GUIUtils.PRIMARY);
        editBtn.addActionListener((e) -> this.cargarProductoParaEditar());
        editBtn.setEnabled(esAdministrador); // === NUEVA LÍNEA ===

        actionPanel.add(editBtn);
        actionPanel.add(deleteBtn);
        tablePanel.add(actionPanel, BorderLayout.SOUTH);

        return new JScrollPane(tablePanel);
    }

    private void actualizarTabla() {
        this.inventario = this.productoDAO.cargarInventario();
        this.actualizarTabla(this.inventario);
    }

    private void actualizarTabla(List<Producto> productos) {
        this.tableModel.setRowCount(0);

        for(Producto producto : productos) {
            this.tableModel.addRow(new Object[]{
                    producto.getId(),
                    producto.getNombre(),
                    producto.getTipo(),
                    producto.getPrecioFormateado(),
                    producto.getStock(),
                    producto.getFechaRegistro()
            });
        }


        if (this.productCount != null) {
            this.productCount.setText("Productos: " + productos.size());
        }
    }

    private void agregarProducto() {
        if (!GUIUtils.validarCampos(new JTextField[]{this.entryNombre, this.entryPrecio, this.entryStock})) {
            GUIUtils.mostrarError("Todos los campos son obligatorios");
        } else {
            try {
                String tipoSeleccionado = (String) this.comboTipo.getSelectedItem();
                int proximoId = this.productoDAO.obtenerProximoId(tipoSeleccionado);

                Producto nuevoProducto = new Producto(
                        proximoId,
                        this.entryNombre.getText(),
                        tipoSeleccionado, // NUEVO: Pasar el tipo seleccionad
                        Double.parseDouble(this.entryPrecio.getText()),
                        Integer.parseInt(this.entryStock.getText())
                );

                if (this.productoDAO.guardarProducto(nuevoProducto)) {
                    this.actualizarTabla();
                    this.limpiarCampos();
                    GUIUtils.mostrarInfo("Producto agregado correctamente a la tabla: " + tipoSeleccionado);
                }
            } catch (NumberFormatException var2) {
                GUIUtils.mostrarError("Precio y Stock deben ser valores numéricos");
            } catch (Exception e) {
                GUIUtils.mostrarError("Error: " + e.getMessage());
            }
        }
    }

    // NUEVO: Método para filtrar por tipo
    private void filtrarPorTipo() {
        String tipoSeleccionado = (String) this.comboTipo.getSelectedItem();
        List<Producto> productosFiltrados = this.productoDAO.cargarProductosPorTipo(tipoSeleccionado);
        this.actualizarTabla(productosFiltrados);
        GUIUtils.mostrarInfo("Mostrando productos del tipo: " + tipoSeleccionado);
    }

    private void eliminarProducto() {
        if (!esAdministrador) {
            GUIUtils.mostrarError("No tiene permisos para eliminar productos");
            return;
        }
        int selectedRow = this.table.getSelectedRow();
        if (selectedRow == -1) {
            GUIUtils.mostrarError("Seleccione un producto para eliminar");
        } else {
            int productoId = (Integer)this.tableModel.getValueAt(selectedRow, 0);
            String productoTipo = (String)this.tableModel.getValueAt(selectedRow, 2); // NUEVO: Obtener tipo
            String productoNombre = (String)this.tableModel.getValueAt(selectedRow, 1);

            if (GUIUtils.confirmarEliminacion("¿Está seguro de eliminar el producto ID " + productoId + " - " + productoNombre + "?")) {
                if (this.productoDAO.eliminarProducto(productoId, productoTipo)) {
                    this.actualizarTabla();
                    GUIUtils.mostrarInfo("Producto eliminado correctamente");
                } else {
                    GUIUtils.mostrarError("Error al eliminar el producto");
                }
            }
        }
    }

    private void limpiarCampos() {
        GUIUtils.limpiarCampos(new JTextField[]{this.entryNombre, this.entryPrecio, this.entryStock, this.searchEntry});
        this.comboTipo.setSelectedIndex(0); // NUEVO: Resetear combobox
        this.actualizarTabla();
    }

    public void dispose() {
        if (this.productoDAO != null) {
            this.productoDAO.cerrar();
        }
        super.dispose();
    }
}
