package com.bigjohn.garantias;

import java.util.Random;

/**
 * Simula la decision de la sucursal de Medellin (la principal):
 * si la prenda tiene garantia o no y, si la tiene, que solucion se da.
 * Los porcentajes se pueden cambiar en elegirAlAzar().
 */
public class RevisionMedellin {
    private final Random random;
    private final ResolucionGarantia forzada; // solo para pruebas/demos

    public RevisionMedellin(Random random) { this(random, null); }

    private RevisionMedellin(Random random, ResolucionGarantia forzada) {
        this.random = random;
        this.forzada = forzada;
    }

    /** Version de prueba: Medellin siempre responde con la misma resolucion. */
    public static RevisionMedellin siempre(ResolucionGarantia resolucion) {
        return new RevisionMedellin(new Random(), resolucion);
    }

    public ResultadoRevision revisar(SolicitudGarantia s) {
        ResolucionGarantia r = forzada != null ? forzada : elegirAlAzar();
        return new ResultadoRevision(r, observacion(r));
    }

    private ResolucionGarantia elegirAlAzar() {
        int n = random.nextInt(100);
        if (n < 25) return ResolucionGarantia.NO_APLICA;      // 25 %
        if (n < 55) return ResolucionGarantia.PRENDA_NUEVA;   // 30 %
        if (n < 80) return ResolucionGarantia.REPARACION;     // 25 %
        return ResolucionGarantia.BONO;                       // 20 %
    }

    private String observacion(ResolucionGarantia r) {
        return switch (r) {
            case NO_APLICA -> "Tras la revisión en Medellín, el defecto no está cubierto por la garantía.";
            case PRENDA_NUEVA -> "Garantía aprobada. Se entregará una prenda nueva al cliente.";
            case REPARACION -> "Garantía aprobada. La prenda se vuelve a coser y se devuelve al cliente.";
            case BONO -> "Garantía aprobada. Se emite un bono por el valor de la prenda, que se paga como medio de pago en cualquier tienda.";
            default -> "";
        };
    }
}
