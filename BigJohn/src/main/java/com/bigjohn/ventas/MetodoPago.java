package com.bigjohn.ventas;

public enum MetodoPago {
    EFECTIVO("Efectivo"), TARJETA("Tarjeta"), TRANSFERENCIA("Transferencia"), BONO("Bono de garantía");

    private final String etiqueta;
    MetodoPago(String etiqueta) { this.etiqueta = etiqueta; }

    @Override public String toString() { return etiqueta; }
}
