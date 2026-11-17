package com.bigjohn.garantias;

public enum EstadoGarantia {
    REGISTRADA("Registrada"),
    EN_REVISION("En revisión en Medellín"),
    RESUELTA("Resuelta");

    private final String etiqueta;
    EstadoGarantia(String etiqueta) { this.etiqueta = etiqueta; }

    @Override public String toString() { return etiqueta; }
}
