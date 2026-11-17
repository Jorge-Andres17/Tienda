package com.bigjohn.ventas;

import java.time.LocalDate;

public class Factura {
    private final int idFactura;
    private final String numero;
    private final LocalDate fecha;
    private final double total;

    public Factura(int idFactura, String numero, LocalDate fecha, double total) {
        this.idFactura = idFactura;
        this.numero = numero;
        this.fecha = fecha;
        this.total = total;
    }

    public int getIdFactura() { return idFactura; }
    public String getNumero() { return numero; }
    public LocalDate getFecha() { return fecha; }
    public double getTotal() { return total; }
}
