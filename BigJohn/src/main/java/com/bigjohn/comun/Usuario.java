package com.bigjohn.comun;

/** Quien compra. En el diagrama de garantias aparece como "Cliente". */
public class Usuario extends Persona {
    public Usuario(int id, String nombre, String documento, String telefono, String correo, String clave) {
        super(id, nombre, documento, telefono, correo, clave);
    }
}
