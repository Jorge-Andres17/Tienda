package com.bigjohn.ventas;

import com.bigjohn.comun.Producto;

public class DetalleVenta {
    private final int idDetalleVenta;
    private final Producto producto;
    private final int cantidad;
    private final double precioUnitario; // precio al momento de la venta

    public DetalleVenta(int idDetalleVenta, Producto producto, int cantidad, double precioUnitario) {
        this.idDetalleVenta = idDetalleVenta;
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
    }

    public int getIdDetalleVenta() { return idDetalleVenta; }
    public Producto getProducto() { return producto; }
    public int getCantidad() { return cantidad; }
    public double getPrecioUnitario() { return precioUnitario; }
    public double getSubtotal() { return cantidad * precioUnitario; }

    @Override
    public String toString() { return producto.getNombreProducto() + " x" + cantidad; }
}
