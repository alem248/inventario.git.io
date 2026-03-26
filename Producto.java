package Inventario.models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

//CLASE PARA DEFINIR A UN PRODUCTO
public class Producto {
    private int id;
    private String nombre;
    private String tipo;
    private double precio;
    private int stock;
    private String fechaRegistro;
    //VARIABLE QUE GUARDA UN PATRON PARA ACCEDER A LA HORA DEL SISTEMA
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Constructor para productos nuevos
    public Producto(int id, String nombre, String tipo, double precio, int stock) {
        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
        this.precio = precio;
        this.stock = stock;
        this.fechaRegistro = LocalDateTime.now().format(formatter);
    }
    // constructor para productos existentes
    public Producto(int id, String nombre, String tipo, double precio, int stock, String fechaRegistro) {
        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
        this.precio = precio;
        this.stock = stock;
        this.fechaRegistro = fechaRegistro;
    }

    // Getters y Setters
    public int getId() {
        return this.id;
    }

    public String getNombre() {
        return this.nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipo() {
        return this.tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }


    public double getPrecio() {
        return this.precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public int getStock() {
        return this.stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getFechaRegistro() {
        return this.fechaRegistro;
    }

    public String getPrecioFormateado() {
        return String.format("$%.2f", this.precio);
    }
}
