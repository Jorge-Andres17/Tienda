package com.bigjohn.garantias;

import com.bigjohn.comun.RelojSimulado;
import com.bigjohn.comun.Repositorio;
import com.bigjohn.comun.RepositorioMemoria;
import com.bigjohn.comun.Sucursal;
import com.bigjohn.ventas.DetalleVenta;
import com.bigjohn.ventas.PagoConBono;
import com.bigjohn.ventas.Venta;
import com.bigjohn.ventas.VentaServicio;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Proceso de garantia: se registra la solicitud, se envia a Medellin y
 * Medellin responde DIAS_RESPUESTA dias despues (segun el reloj simulado).
 * La garantia solo se puede pedir dentro de MESES_PLAZO mes(es) desde la compra.
 * Este modulo depende de ventas (consulta la venta e implementa PagoConBono), pero ventas no conoce a garantias.
 */
public class GarantiaServicio implements PagoConBono {
    public static final int DIAS_RESPUESTA = 8;
    public static final int MESES_PLAZO = 1;

    private final VentaServicio ventas;
    private final RelojSimulado reloj;
    private final Sucursal principal;
    private final RevisionMedellin revision;
    private final Repositorio<SolicitudGarantia> solicitudes = new RepositorioMemoria<>(SolicitudGarantia::getIdSolicitud);
    private final Repositorio<Bono> bonos = new RepositorioMemoria<>(Bono::getIdBono);
    private int sigSolicitud = 1, sigBono = 1;

    public GarantiaServicio(VentaServicio ventas, RelojSimulado reloj, Sucursal principal, RevisionMedellin revision) {
        this.ventas = ventas;
        this.reloj = reloj;
        this.principal = principal;
        this.revision = revision;
    }

    /** Registra la solicitud y la envia a la sucursal principal. */
    public SolicitudGarantia solicitar(int idVenta, int idProducto, Sucursal origen, String motivo) {
        if (origen == null) throw new IllegalArgumentException("Seleccione la sucursal donde se registra.");
        if (motivo == null || motivo.isBlank()) throw new IllegalArgumentException("Describa el defecto del producto.");

        Venta venta = ventas.buscarPorId(idVenta)
                .orElseThrow(() -> new IllegalArgumentException("La venta no existe."));
        LocalDate limite = fechaLimite(venta);
        if (reloj.hoy().isAfter(limite)) {
            throw new IllegalArgumentException("El plazo para solicitar la garantía venció el " + limite
                    + " (" + MESES_PLAZO + " mes desde la compra).");
        }
        DetalleVenta detalle = venta.getDetalles().stream()
                .filter(d -> d.getProducto().getIdProducto() == idProducto)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("El producto no pertenece a esa venta."));

        boolean yaExiste = solicitudes.listar().stream()
                .anyMatch(s -> s.getIdVenta() == idVenta && s.getProducto().getIdProducto() == idProducto);
        if (yaExiste) throw new IllegalArgumentException("Ya existe una solicitud de garantía para este producto de la venta.");

        SolicitudGarantia s = new SolicitudGarantia(sigSolicitud++, idVenta, venta.getUsuario(),
                detalle.getProducto(), origen, motivo.trim(), detalle.getPrecioUnitario(), reloj.hoy());
        s.enviarAMedellin(reloj.hoy(), DIAS_RESPUESTA);
        solicitudes.guardar(s);
        return s;
    }

    /** Adelanta el reloj y procesa las respuestas que ya "llegaron" de Medellin. Devuelve cuantas se resolvieron. */
    public int avanzarDias(int dias) {
        reloj.avanzarDias(dias);
        return procesarRespuestas();
    }

    public int procesarRespuestas() {
        LocalDate hoy = reloj.hoy();
        int resueltas = 0;
        for (SolicitudGarantia s : solicitudes.listar()) {
            if (s.getEstado() == EstadoGarantia.EN_REVISION && !s.getFechaRespuestaEstimada().isAfter(hoy)) {
                resolver(s);
                resueltas++;
            }
        }
        return resueltas;
    }

    private void resolver(SolicitudGarantia s) {
        ResultadoRevision r = revision.revisar(s);
        Bono bono = null;
        if (r.resolucion() == ResolucionGarantia.BONO) {
            int id = sigBono++;
            bono = new Bono(id, String.format("BONO-%04d", id), s.getValorPrenda(),
                    s.getFechaRespuestaEstimada(), s.getIdSolicitud());
            bonos.guardar(bono);
        }
        s.resolver(s.getFechaRespuestaEstimada(), r.resolucion(), r.observacion(), bono);
    }

    /** Ultimo dia en que se puede pedir garantia por una venta. */
    public LocalDate fechaLimite(Venta venta) { return venta.getFecha().plusMonths(MESES_PLAZO); }

    public boolean dentroDePlazo(Venta venta) { return !reloj.hoy().isAfter(fechaLimite(venta)); }

    // ---- Bonos como medio de pago (contrato PagoConBono de ventas) ----

    @Override
    public double valorDisponible(String codigo) {
        Bono bono = buscarBono(codigo);
        if (bono.isRedimido()) throw new IllegalArgumentException("El bono " + bono.getCodigo() + " ya fue redimido.");
        return bono.getValor();
    }

    @Override
    public void redimir(String codigo, Sucursal sucursal, int idVenta) {
        buscarBono(codigo).redimir(reloj.hoy(), sucursal, idVenta);
    }

    private Bono buscarBono(String codigo) {
        String c = codigo == null ? "" : codigo.trim();
        Optional<Bono> bono = bonos.listar().stream().filter(b -> b.getCodigo().equalsIgnoreCase(c)).findFirst();
        return bono.orElseThrow(() -> new IllegalArgumentException("El bono \"" + c + "\" no existe."));
    }

    public List<SolicitudGarantia> listarSolicitudes() { return solicitudes.listar(); }
    public List<Bono> listarBonos() { return bonos.listar(); }
    public Sucursal getPrincipal() { return principal; }
}
