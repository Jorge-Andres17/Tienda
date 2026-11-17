package com.bigjohn.comun;

/** Quien atiende y registra las ventas. En el diagrama de garantias aparece como "Empleado". */
public class Asesor extends Persona {
    private final String rol;

    public Asesor(int id, String nombre, String documento, String telefono, String correo, String clave, String rol) {
        super(id, nombre, documento, telefono, correo, clave);
        this.rol = rol;
    }

    public String getRol() { return rol; }
}
