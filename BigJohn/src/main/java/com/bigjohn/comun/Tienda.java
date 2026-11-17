package com.bigjohn.comun;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Tienda {
    private final String nombre;
    private final List<Sucursal> sucursales = new ArrayList<>();

    public Tienda(String nombre) { this.nombre = nombre; }

    public void agregarSucursal(Sucursal s) { sucursales.add(s); }

    public String getNombre() { return nombre; }
    public List<Sucursal> getSucursales() { return Collections.unmodifiableList(sucursales); }

    @Override
    public String toString() { return nombre; }
}
