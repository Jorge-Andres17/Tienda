package com.bigjohn.comun;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Sucursal {
    private final int id;
    private final String nombre;      // ej. "Medellín"
    private final String direccion;
    private final String telefono;
    private final boolean principal;  // la sucursal de Medellín es la principal
    private Administrador administrador;
    private final List<Asesor> asesores = new ArrayList<>();

    public Sucursal(int id, String nombre, String direccion, String telefono, boolean principal) {
        this.id = id;
        this.nombre = nombre;
        this.direccion = direccion;
        this.telefono = telefono;
        this.principal = principal;
    }

    public void setAdministrador(Administrador a) {
        this.administrador = a;
        if (!asesores.contains(a)) asesores.add(a); // la administradora tambien puede asesorar
    }

    public void agregarAsesor(Asesor a) { asesores.add(a); }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getDireccion() { return direccion; }
    public String getTelefono() { return telefono; }
    public boolean isPrincipal() { return principal; }
    public Administrador getAdministrador() { return administrador; }
    public List<Asesor> getAsesores() { return Collections.unmodifiableList(asesores); }

    @Override
    public String toString() { return nombre + (principal ? " (principal)" : ""); }
}
