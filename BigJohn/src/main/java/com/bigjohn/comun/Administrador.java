package com.bigjohn.comun;

/** La administradora de la sucursal tambien puede actuar como asesora (hereda de Asesor). */
public class Administrador extends Asesor {
    private final String cargo;

    public Administrador(int id, String nombre, String documento, String telefono, String correo, String clave, String cargo) {
        super(id, nombre, documento, telefono, correo, clave, "Administrador");
        this.cargo = cargo;
    }

    public String getCargo() { return cargo; }
}
