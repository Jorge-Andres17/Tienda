package com.bigjohn.garantias;

public enum ResolucionGarantia {
    PENDIENTE("Pendiente"),
    NO_APLICA("No aplica garantía"),
    PRENDA_NUEVA("Prenda nueva"),
    REPARACION("Reparación (se vuelve a coser)"),
    BONO("Bono por el valor de la prenda");

    private final String etiqueta;
    ResolucionGarantia(String etiqueta) { this.etiqueta = etiqueta; }

    @Override public String toString() { return etiqueta; }
}
