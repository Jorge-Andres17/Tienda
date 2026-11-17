package com.bigjohn.ventas;

import com.bigjohn.comun.Sucursal;

/**
 * Lo que ventas necesita saber de los bonos. La interfaz esta en ventas y la implementa
 * el modulo de garantias, asi ventas NO depende de garantias.
 */
public interface PagoConBono {
    /** Valor del bono si existe y no ha sido redimido; si no, lanza IllegalArgumentException. */
    double valorDisponible(String codigo);

    /** Marca el bono como redimido en la venta indicada. */
    void redimir(String codigo, Sucursal sucursal, int idVenta);
}
