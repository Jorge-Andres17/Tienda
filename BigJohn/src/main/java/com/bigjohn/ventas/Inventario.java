package com.bigjohn.ventas;

import com.bigjohn.comun.Producto;
import com.bigjohn.comun.Sucursal;

import java.util.HashMap;
import java.util.Map;

/** Inventario de una sucursal: cuantas unidades hay de cada producto. */
public class Inventario {
    private final int idInventario;
    private final Sucursal sucursal;
    private final Map<Integer, Integer> stock = new HashMap<>(); // idProducto -> unidades

    public Inventario(int idInventario, Sucursal sucursal) {
        this.idInventario = idInventario;
        this.sucursal = sucursal;
    }

    public void agregarStock(Producto p, int cantidad) {
        stock.merge(p.getIdProducto(), cantidad, Integer::sum);
    }

    public int getStock(Producto p) { return stock.getOrDefault(p.getIdProducto(), 0); }

    public boolean disponible(Producto p, int cantidad) { return getStock(p) >= cantidad; }

    public void descontar(Producto p, int cantidad) {
        if (!disponible(p, cantidad)) {
            throw new IllegalArgumentException("Stock insuficiente de " + p.getNombreProducto() + ".");
        }
        stock.merge(p.getIdProducto(), -cantidad, Integer::sum);
    }

    public int getIdInventario() { return idInventario; }
    public Sucursal getSucursal() { return sucursal; }
}
