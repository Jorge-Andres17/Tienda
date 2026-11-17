package com.bigjohn.ventas;

import com.bigjohn.comun.Asesor;
import com.bigjohn.comun.Sucursal;
import com.bigjohn.comun.Usuario;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Venta {
    private final int idVenta;
    private final LocalDate fecha;
    private final LocalTime hora;
    private final Usuario usuario;
    private final Asesor asesor;
    private final Sucursal sucursal;
    private final List<DetalleVenta> detalles = new ArrayList<>();
    private final List<Pago> pagos = new ArrayList<>();
    private Factura factura;
    private double subtotal;
    private double total;
    private EstadoVenta estado = EstadoVenta.PENDIENTE;

    Venta(int idVenta, LocalDate fecha, LocalTime hora, Usuario usuario, Asesor asesor, Sucursal sucursal) {
        this.idVenta = idVenta;
        this.fecha = fecha;
        this.hora = hora;
        this.usuario = usuario;
        this.asesor = asesor;
        this.sucursal = sucursal;
    }

    // Los cambios solo los hace VentaServicio (mismo paquete)
    void agregarDetalle(DetalleVenta d) {
        detalles.add(d);
        subtotal += d.getSubtotal();
        total = subtotal;
    }

    void agregarPago(Pago p) {
        pagos.add(p);
        double pagado = pagos.stream().mapToDouble(Pago::getMonto).sum();
        if (pagado >= total - 0.005) estado = EstadoVenta.PAGADA;
    }

    void setFactura(Factura f) { this.factura = f; }

    /** Ej.: "Bono de garantía + Efectivo". */
    public String getMediosDePago() {
        return pagos.stream().map(p -> p.getMetodo().toString()).collect(java.util.stream.Collectors.joining(" + "));
    }

    public int getIdVenta() { return idVenta; }
    public LocalDate getFecha() { return fecha; }
    public LocalTime getHora() { return hora; }
    public Usuario getUsuario() { return usuario; }
    public Asesor getAsesor() { return asesor; }
    public Sucursal getSucursal() { return sucursal; }
    public List<DetalleVenta> getDetalles() { return Collections.unmodifiableList(detalles); }
    public List<Pago> getPagos() { return Collections.unmodifiableList(pagos); }
    public Factura getFactura() { return factura; }
    public double getSubtotal() { return subtotal; }
    public double getTotal() { return total; }
    public EstadoVenta getEstado() { return estado; }

    @Override
    public String toString() { return "Venta #" + idVenta + " - " + usuario.getNombre() + " (" + fecha + ")"; }
}
