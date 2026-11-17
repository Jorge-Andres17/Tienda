package com.bigjohn.garantias;

import com.bigjohn.comun.Sucursal;

import java.time.LocalDate;

/** Bono por el valor de la prenda. Se usa una sola vez, como medio de pago en cualquier tienda. */
public class Bono {
    private final int idBono;
    private final String codigo;
    private final double valor;
    private final LocalDate fechaEmision;
    private final int idSolicitud;
    private boolean redimido;
    private LocalDate fechaRedencion;
    private Sucursal sucursalRedencion;
    private int idVentaRedencion;

    Bono(int idBono, String codigo, double valor, LocalDate fechaEmision, int idSolicitud) {
        this.idBono = idBono;
        this.codigo = codigo;
        this.valor = valor;
        this.fechaEmision = fechaEmision;
        this.idSolicitud = idSolicitud;
    }

    void redimir(LocalDate fecha, Sucursal sucursal, int idVenta) {
        if (redimido) throw new IllegalArgumentException("El bono " + codigo + " ya fue redimido.");
        redimido = true;
        fechaRedencion = fecha;
        sucursalRedencion = sucursal;
        idVentaRedencion = idVenta;
    }

    public int getIdBono() { return idBono; }
    public String getCodigo() { return codigo; }
    public double getValor() { return valor; }
    public LocalDate getFechaEmision() { return fechaEmision; }
    public int getIdSolicitud() { return idSolicitud; }
    public boolean isRedimido() { return redimido; }
    public LocalDate getFechaRedencion() { return fechaRedencion; }
    public Sucursal getSucursalRedencion() { return sucursalRedencion; }
    public int getIdVentaRedencion() { return idVentaRedencion; }
}
