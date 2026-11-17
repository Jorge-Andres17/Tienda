package com.bigjohn.comun;

/** Clase base de las personas del sistema (comun a ventas y garantias). */
public class Persona {
    private final int id;
    private final String nombre;
    private final String documento;
    private final String telefono;
    private final String correo;
    private final String clave;

    public Persona(int id, String nombre, String documento, String telefono, String correo, String clave) {
        this.id = id;
        this.nombre = nombre;
        this.documento = documento;
        this.telefono = telefono;
        this.correo = correo;
        this.clave = clave;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getDocumento() { return documento; }
    public String getTelefono() { return telefono; }
    public String getCorreo() { return correo; }
    public String getClave() { return clave; }

    @Override
    public String toString() { return nombre; }
}
