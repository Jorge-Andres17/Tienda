package com.bigjohn.comun;

public class Producto {
    private final int idProducto;
    private final String nombreProducto;
    private final String tipoProducto;
    private final double precio;

    public Producto(int idProducto, String nombreProducto, String tipoProducto, double precio) {
        this.idProducto = idProducto;
        this.nombreProducto = nombreProducto;
        this.tipoProducto = tipoProducto;
        this.precio = precio;
    }

    public int getIdProducto() { return idProducto; }
    public String getNombreProducto() { return nombreProducto; }
    public String getTipoProducto() { return tipoProducto; }
    public double getPrecio() { return precio; }

    @Override
    public String toString() { return nombreProducto; }
}
