package Inventario.DAO;

import Inventario.config.DatabaseConfig;
import Inventario.exceptions.DatabaseException;
import Inventario.models.Producto;
import java.sql.*;
import java.util.*;

public class ProductoDAO {
    private Connection connection;
    private static final String[] TIPOS_JUGUETES = {
            "Figuras de Acción", "Juegos Lúdicos", "Peluches",
            "Vehículos", "Construcción", "Electrónicos"
    };

    public ProductoDAO() {
        this.conectar();
        this.crearTablasSiNoExisten();
    }

    private void conectar() {
        try {
            this.connection = DriverManager.getConnection(DatabaseConfig.getUrl(), DatabaseConfig.getUsername(), DatabaseConfig.getPassword());
            System.out.println("Conexión exitosa a MySQL");
        } catch (SQLException e) {
            throw new DatabaseException("Error al conectar con la base de datos", e);
        }
    }
    // Crear una tabla por cada tipo de juguete
    private void crearTablasSiNoExisten() {
        for (String tipo : TIPOS_JUGUETES) {
            String nombreTabla = this.getNombreTabla(tipo);
            String sql = "CREATE TABLE IF NOT EXISTS " + nombreTabla + " (\n" +
                    "    id INT AUTO_INCREMENT PRIMARY KEY,\n" +
                    "    nombre VARCHAR(255) NOT NULL,\n" +
                    "    precio DECIMAL(10,2) NOT NULL,\n" +
                    "    stock INT NOT NULL,\n" +
                    "    fecha_registro DATETIME DEFAULT CURRENT_TIMESTAMP\n" +
                    ")";

            try (Statement stmt = this.connection.createStatement()) {
                stmt.execute(sql);
                System.out.println("Tabla " + nombreTabla + " creada/verificada");
            } catch (SQLException e) {
                throw new DatabaseException("Error al crear la tabla " + nombreTabla, e);
            }
        }
    }
    // Convertir el tipo a un nombre de tabla válido (sin espacios y en minúsculas)
    private String getNombreTabla(String tipo) {

        return "productos_" + tipo.toLowerCase().replace(" ", "_").replace("ó", "o");
    }

    public List<Producto> cargarInventario() {
        List<Producto> inventario = new ArrayList<>();

        // Cargar productos de todas las tablas
        for (String tipo : TIPOS_JUGUETES) {
            String nombreTabla = this.getNombreTabla(tipo);
            String sql = "SELECT * FROM " + nombreTabla + " ORDER BY id";

            try (Statement stmt = this.connection.createStatement();
                 //Un resulset es un objeto que guarda resultado de codigo sql
                 ResultSet rs = stmt.executeQuery(sql)) {

                while (rs.next()) {
                    Producto producto = new Producto(
                            rs.getInt("id"),
                            rs.getString("nombre"),
                            tipo, // El tipo viene del nombre de la tabla
                            rs.getDouble("precio"),
                            rs.getInt("stock"),
                            //Timestamp guarda fecha y hora,toString cambia a texto,subString corta a decimales
                            rs.getTimestamp("fecha_registro").toString().substring(0, 19)
                    );
                    inventario.add(producto);
                }
            } catch (SQLException e) {
                throw new DatabaseException("Error al cargar inventario de " + nombreTabla, e);
            }
        }

        return inventario;
    }

    public boolean guardarProducto(Producto producto) {
        String nombreTabla = this.getNombreTabla(producto.getTipo());
        String sql = "INSERT INTO " + nombreTabla + " (nombre,precio, stock) VALUES (?, ?, ?)";

        try (PreparedStatement pstmt = this.connection.prepareStatement(sql)) {
            pstmt.setString(1, producto.getNombre());
            pstmt.setDouble(2, producto.getPrecio());
            pstmt.setInt(3, producto.getStock());

            //EL RETURN DEVUELVE CUANTAS FILAS FUERON AFECTADAS POR ESO EL MAS 0
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error al guardar producto en " + nombreTabla + ": " + producto.getNombre(), e);
        }
    }

    public boolean eliminarProducto(int id, String tipo) {
        String nombreTabla = this.getNombreTabla(tipo);
        String sql = "DELETE FROM " + nombreTabla + " WHERE id = ?";

        try (PreparedStatement pstmt = this.connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error al eliminar producto ID: " + id + " de " + nombreTabla, e);
        }
    }

    public int obtenerProximoId(String tipo) {
        String nombreTabla = this.getNombreTabla(tipo);
        String sql = "SELECT MAX(id) as max_id FROM " + nombreTabla;

        try (Statement stmt = this.connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt("max_id") + 1;
            }
            return 1;
        } catch (SQLException e) {
            throw new DatabaseException("Error al obtener próximo ID de " + nombreTabla, e);
        }
    }

    // Método para obtener productos por tipo específico
    public List<Producto> cargarProductosPorTipo(String tipo) {
        List<Producto> productos = new ArrayList<>();
        String nombreTabla = this.getNombreTabla(tipo);
        String sql = "SELECT * FROM " + nombreTabla + " ORDER BY id";

        try (Statement stmt = this.connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Producto producto = new Producto(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        tipo,
                        rs.getDouble("precio"),
                        rs.getInt("stock"),
                        rs.getTimestamp("fecha_registro").toString().substring(0, 19)
                );
                productos.add(producto);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error al cargar productos de " + nombreTabla, e);
        }

        return productos;
    }

    public Producto obtenerProductoPorIdYTipo(int id, String tipo) {
        String nombreTabla = this.getNombreTabla(tipo);
        String sql = "SELECT * FROM " + nombreTabla + " WHERE id = ?";

        try (PreparedStatement pstmt = this.connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Producto(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        tipo,
                        rs.getDouble("precio"),
                        rs.getInt("stock"),
                        rs.getTimestamp("fecha_registro").toString().substring(0, 19)
                );
            }
            return null;
        } catch (SQLException e) {
            throw new DatabaseException("Error al obtener producto ID: " + id + " de " + nombreTabla, e);
        }
    }

    // actualizar productos existentes
    public boolean actualizarProducto(Producto producto) {
        String nombreTabla = this.getNombreTabla(producto.getTipo());
        String sql = "UPDATE " + nombreTabla + " SET nombre = ?, precio = ?, stock = ? WHERE id = ?";

        try (PreparedStatement pstmt = this.connection.prepareStatement(sql)) {
            pstmt.setString(1, producto.getNombre());
            pstmt.setDouble(2, producto.getPrecio());
            pstmt.setInt(3, producto.getStock());
            pstmt.setInt(4, producto.getId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error al actualizar producto ID: " + producto.getId() + " en " + nombreTabla, e);
        }
    }

    public void cerrar() {
        try {
            if (this.connection != null && !this.connection.isClosed()) {
                this.connection.close();
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error al cerrar conexión", e);
        }
    }
}
