package com.bigjohn.garantias;

import com.bigjohn.comun.Producto;
import com.bigjohn.comun.Sucursal;
import com.bigjohn.comun.Usuario;

import java.time.LocalDate;

/** Solicitud de garantia (en el diagrama de garantias seria la "Devolucion"). */
public class SolicitudGarantia {
    private final int idSolicitud;
    private final int idVenta;            // solo se guarda el id de la venta (modulo ventas)
    private final Usuario usuario;
    private final Producto producto;
    private final Sucursal sucursalOrigen;
    private final String motivo;
    private final double valorPrenda;
    private final LocalDate fechaRegistro;

    private EstadoGarantia estado = EstadoGarantia.REGISTRADA;
    private LocalDate fechaEnvio;
    private LocalDate fechaRespuestaEstimada;
    private LocalDate fechaRespuesta;
    private ResolucionGarantia resolucion = ResolucionGarantia.PENDIENTE;
    private String observacion = "";
    private Bono bono;

    SolicitudGarantia(int idSolicitud, int idVenta, Usuario usuario, Producto producto,
                      Sucursal sucursalOrigen, String motivo, double valorPrenda, LocalDate fechaRegistro) {
        this.idSolicitud = idSolicitud;
        this.idVenta = idVenta;
        this.usuario = usuario;
        this.producto = producto;
        this.sucursalOrigen = sucursalOrigen;
        this.motivo = motivo;
        this.valorPrenda = valorPrenda;
        this.fechaRegistro = fechaRegistro;
    }

    /** Se envia a la sucursal principal (Medellin) y queda esperando respuesta. */
    void enviarAMedellin(LocalDate hoy, int diasRespuesta) {
        estado = EstadoGarantia.EN_REVISION;
        fechaEnvio = hoy;
        fechaRespuestaEstimada = hoy.plusDays(diasRespuesta);
    }

    void resolver(LocalDate fecha, ResolucionGarantia resolucion, String observacion, Bono bono) {
        this.estado = EstadoGarantia.RESUELTA;
        this.fechaRespuesta = fecha;
        this.resolucion = resolucion;
        this.observacion = observacion;
        this.bono = bono;
    }

    public int getIdSolicitud() { return idSolicitud; }
    public int getIdVenta() { return idVenta; }
    public Usuario getUsuario() { return usuario; }
    public Producto getProducto() { return producto; }
    public Sucursal getSucursalOrigen() { return sucursalOrigen; }
    public String getMotivo() { return motivo; }
    public double getValorPrenda() { return valorPrenda; }
    public LocalDate getFechaRegistro() { return fechaRegistro; }
    public EstadoGarantia getEstado() { return estado; }
    public LocalDate getFechaEnvio() { return fechaEnvio; }
    public LocalDate getFechaRespuestaEstimada() { return fechaRespuestaEstimada; }
    public LocalDate getFechaRespuesta() { return fechaRespuesta; }
    public ResolucionGarantia getResolucion() { return resolucion; }
    public String getObservacion() { return observacion; }
    public Bono getBono() { return bono; }
}
