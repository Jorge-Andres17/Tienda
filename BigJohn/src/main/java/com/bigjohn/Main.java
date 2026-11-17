package com.bigjohn;

import com.bigjohn.comun.Administrador;
import com.bigjohn.comun.Asesor;
import com.bigjohn.comun.Producto;
import com.bigjohn.comun.RelojSimulado;
import com.bigjohn.comun.Sucursal;
import com.bigjohn.comun.Tienda;
import com.bigjohn.comun.UsuarioServicio;
import com.bigjohn.garantias.GarantiaServicio;
import com.bigjohn.garantias.ResolucionGarantia;
import com.bigjohn.garantias.RevisionMedellin;
import com.bigjohn.ui.VentanaPrincipal;
import com.bigjohn.ventas.Inventario;
import com.bigjohn.ventas.VentaServicio;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.util.List;
import java.util.Random;

/** Punto de entrada: crea los datos de prueba, conecta los modulos y abre la ventana. */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignorada) { }

            // --- Datos comunes ---
            RelojSimulado reloj = new RelojSimulado();
            Tienda tienda = new Tienda("Big John");
            Sucursal medellin = new Sucursal(1, "Medellín", "Calle 10 # 20-30", "604 000 0001", true);
            Sucursal bogota = new Sucursal(2, "Bogotá", "Carrera 15 # 85-20", "601 000 0002", false);
            Sucursal cali = new Sucursal(3, "Cali", "Avenida 6N # 25-10", "602 000 0003", false);
            List<Sucursal> sucursales = List.of(medellin, bogota, cali);

            int idPersona = 1;
            for (Sucursal s : sucursales) {
                tienda.agregarSucursal(s);
                s.setAdministrador(new Administrador(idPersona++, "Administradora " + s.getNombre(),
                        "10" + idPersona, "300 000 000" + idPersona, "admin." + s.getNombre().toLowerCase().charAt(0) + "@bigjohn.com",
                        "1234", "Administradora de sucursal"));
                s.agregarAsesor(new Asesor(idPersona++, "Asesor " + s.getNombre(),
                        "10" + idPersona, "310 000 000" + idPersona, "asesor." + s.getNombre().toLowerCase().charAt(0) + "@bigjohn.com",
                        "1234", "Asesor de ventas"));
            }

            UsuarioServicio usuarios = new UsuarioServicio(idPersona);
            usuarios.registrar("Carlos Pérez", "1001", "320 111 1111", "carlos@mail.com", "1234");
            usuarios.registrar("María Rodríguez", "1002", "320 222 2222", "maria@mail.com", "1234");
            usuarios.registrar("Andrés López", "1003", "320 333 3333", "andres@mail.com", "1234");

            List<Producto> productos = List.of(
                    new Producto(1, "Camisa polo", "Camisa", 59900),
                    new Producto(2, "Jean clásico", "Pantalón", 129900),
                    new Producto(3, "Chaqueta de cuero", "Chaqueta", 189900),
                    new Producto(4, "Camiseta básica", "Camiseta", 39900),
                    new Producto(5, "Gorra", "Accesorio", 29900));

            // --- Modulo ventas: inventario de cada sucursal ---
            VentaServicio ventas = new VentaServicio(reloj);
            int idInventario = 1;
            for (Sucursal s : sucursales) {
                Inventario inv = new Inventario(idInventario++, s);
                productos.forEach(p -> inv.agregarStock(p, 10));
                ventas.registrarInventario(inv);
            }

            // --- Modulo garantias (depende de ventas, no al reves) ---
            // Para una demo rapida del bono: java -cp out com.bigjohn.Main bono  (Medellin siempre da un bono)
            RevisionMedellin revision = args.length > 0 && args[0].equalsIgnoreCase("bono")
                    ? RevisionMedellin.siempre(ResolucionGarantia.BONO)
                    : new RevisionMedellin(new Random());
            GarantiaServicio garantias = new GarantiaServicio(ventas, reloj, medellin, revision);
            ventas.setPagoConBono(garantias); // ventas usa los bonos solo a traves de la interfaz PagoConBono

            new VentanaPrincipal(reloj, ventas, garantias, usuarios, sucursales, productos).setVisible(true);
        });
    }
}
