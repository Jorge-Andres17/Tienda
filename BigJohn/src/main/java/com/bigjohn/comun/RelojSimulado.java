package com.bigjohn.comun;

import java.time.LocalDate;

/**
 * Reloj del sistema. Permite "adelantar dias" para simular que pasan los 8 dias
 * que tarda la sucursal de Medellin en responder una garantia.
 */
public class RelojSimulado {
    private LocalDate hoy = LocalDate.now();

    public LocalDate hoy() { return hoy; }

    public void avanzarDias(int dias) {
        if (dias <= 0) throw new IllegalArgumentException("Los dias deben ser mayores a cero.");
        hoy = hoy.plusDays(dias);
    }
}
