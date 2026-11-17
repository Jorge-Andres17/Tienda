package com.bigjohn.ventas;

import com.bigjohn.comun.Asesor;
import com.bigjohn.comun.Producto;
import com.bigjohn.comun.RelojSimulado;
import com.bigjohn.comun.Repositorio;
import com.bigjohn.comun.RepositorioMemoria;
import com.bigjohn.comun.Sucursal;
import com.bigjohn.comun.Usuario;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Logica del proceso de venta: validar stock y bono, registrar la venta, los pagos y la factura. */
public class VentaServicio {
    private final RelojSimulado reloj;
    private final Repositorio<Venta> ventas = new RepositorioMemoria<>(Venta::getIdVenta);
    private final Map<Integer, Inventario> inventarios = new HashMap<>(); // idSucursal -> inventario
    private PagoConBono bonos; // lo conecta Main (lo implementa el modulo de garantias)
    private int sigVenta = 1, sigDetalle = 1, sigPago = 1, sigFactura = 1;

    public VentaServicio(RelojSimulado reloj) { this.reloj = reloj; }

    public void setPagoConBono(PagoConBono bonos) { this.bonos = bonos; }

    public void registrarInventario(Inventario inv) {
        inventarios.put(inv.getSucursal().getId(), inv);
    }

    public Inventario inventarioDe(Sucursal s) {
        Inventario inv = inventarios.get(s.getId());
        if (inv == null) throw new IllegalArgumentException("La sucursal no tiene inventario.");
        return inv;
    }

    /** Valor del bono si existe y esta disponible (no redimido). Lanza IllegalArgumentException si no. */
    public double valorBonoDisponible(String codigo) {
        if (codigo == null || codigo.isBlank()) throw new IllegalArgumentException("Ingrese el código del bono.");
        if (bonos == null) throw new IllegalArgumentException("No hay un servicio de bonos disponible.");
        return bonos.valorDisponible(codigo.trim());
    }

    public Venta registrarVenta(Usuario usuario, Asesor asesor, Sucursal sucursal,
                                Map<Producto, Integer> items, MetodoPago metodo) {
        return registrarVenta(usuario, asesor, sucursal, items, metodo, null);
    }

    /**
     * Registra la venta. Si se da un codigo de bono, el bono paga hasta su valor (y se redime completo);
     * lo que falte se paga con "metodo". Si no hay bono, "metodo" paga todo.
     */
    public Venta registrarVenta(Usuario usuario, Asesor asesor, Sucursal sucursal,
                                Map<Producto, Integer> items, MetodoPago metodo, String codigoBono) {
        // ---- 1. Validar todo antes de cambiar nada ----
        if (usuario == null || asesor == null || sucursal == null) {
            throw new IllegalArgumentException("Faltan datos de la venta.");
        }
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Agregue al menos un producto al carrito.");
        }
        Inventario inv = inventarioDe(sucursal);
        double total = 0;
        for (Map.Entry<Producto, Integer> e : items.entrySet()) {
            if (!inv.disponible(e.getKey(), e.getValue())) {
                throw new IllegalArgumentException("Stock insuficiente de " + e.getKey().getNombreProducto() + ".");
            }
            total += e.getKey().getPrecio() * e.getValue();
        }

        String codigo = codigoBono == null ? "" : codigoBono.trim();
        double aplicadoBono = codigo.isEmpty() ? 0 : Math.min(valorBonoDisponible(codigo), total);
        double resto = total - aplicadoBono;
        if (resto > 0 && (metodo == null || metodo == MetodoPago.BONO)) {
            throw new IllegalArgumentException(codigo.isEmpty()
                    ? "Seleccione un método de pago (efectivo, tarjeta o transferencia)."
                    : String.format("El bono no cubre toda la compra. Seleccione cómo pagar los $%,.0f restantes.", resto));
        }

        // ---- 2. Registrar ----
        Venta venta = new Venta(sigVenta++, reloj.hoy(), LocalTime.now().withNano(0), usuario, asesor, sucursal);
        for (Map.Entry<Producto, Integer> e : items.entrySet()) {
            inv.descontar(e.getKey(), e.getValue());
            venta.agregarDetalle(new DetalleVenta(sigDetalle++, e.getKey(), e.getValue(), e.getKey().getPrecio()));
        }
        if (aplicadoBono > 0) venta.agregarPago(new Pago(sigPago++, aplicadoBono, reloj.hoy(), MetodoPago.BONO));
        if (resto > 0) venta.agregarPago(new Pago(sigPago++, resto, reloj.hoy(), metodo));

        int idFactura = sigFactura++;
        venta.setFactura(new Factura(idFactura, String.format("FAC-%04d", idFactura), reloj.hoy(), venta.getTotal()));
        if (!codigo.isEmpty()) bonos.redimir(codigo, sucursal, venta.getIdVenta());

        ventas.guardar(venta);
        return venta;
    }

    public Optional<Venta> buscarPorId(int idVenta) { return ventas.buscarPorId(idVenta); }
    public List<Venta> listar() { return ventas.listar(); }
}
