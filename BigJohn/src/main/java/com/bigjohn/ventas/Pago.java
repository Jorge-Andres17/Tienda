package com.bigjohn.ventas;

import java.time.LocalDate;

public class Pago {
    private final int idPago;
    private final double monto;
    private final LocalDate fecha;
    private final MetodoPago metodo;

    public Pago(int idPago, double monto, LocalDate fecha, MetodoPago metodo) {
        this.idPago = idPago;
        this.monto = monto;
        this.fecha = fecha;
        this.metodo = metodo;
    }

    public int getIdPago() { return idPago; }
    public double getMonto() { return monto; }
    public LocalDate getFecha() { return fecha; }
    public MetodoPago getMetodo() { return metodo; }
}
