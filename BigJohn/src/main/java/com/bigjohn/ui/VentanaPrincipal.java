package com.bigjohn.ui;

import com.bigjohn.comun.Producto;
import com.bigjohn.comun.RelojSimulado;
import com.bigjohn.comun.Sucursal;
import com.bigjohn.comun.UsuarioServicio;
import com.bigjohn.garantias.GarantiaServicio;
import com.bigjohn.ventas.VentaServicio;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class VentanaPrincipal extends JFrame {
    private final RelojSimulado reloj;
    private final GarantiaServicio garantias;
    private final JLabel lblFecha = new JLabel();
    private final PanelGarantias panelGarantias;

    public VentanaPrincipal(RelojSimulado reloj, VentaServicio ventas, GarantiaServicio garantias,
                            UsuarioServicio usuarios, List<Sucursal> sucursales, List<Producto> productos) {
        super("Big John - Ventas y Garantías");
        this.reloj = reloj;
        this.garantias = garantias;

        PanelVentas panelVentas = new PanelVentas(ventas, usuarios, sucursales, productos);
        panelGarantias = new PanelGarantias(garantias, ventas, sucursales);

        JTabbedPane pestanas = new JTabbedPane();
        pestanas.addTab("Ventas", panelVentas);
        pestanas.addTab("Garantías", panelGarantias);
        pestanas.addChangeListener(e -> { if (pestanas.getSelectedComponent() == panelGarantias) panelGarantias.refrescar(); });

        // Reloj simulado (arriba, visible en las dos pestañas)
        JButton btn1 = new JButton("+1 día");
        JButton btn8 = new JButton("+" + GarantiaServicio.DIAS_RESPUESTA + " días");
        JButton btn30 = new JButton("+30 días");
        btn1.addActionListener(e -> avanzar(1));
        btn30.addActionListener(e -> avanzar(30));
        btn8.addActionListener(e -> avanzar(GarantiaServicio.DIAS_RESPUESTA));
        lblFecha.setFont(lblFecha.getFont().deriveFont(Font.BOLD, 14f));
        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT));
        barra.add(new JLabel("Fecha simulada:"));
        barra.add(lblFecha);
        barra.add(btn1);
        barra.add(btn8);
        barra.add(btn30);
        actualizarFecha();

        setLayout(new BorderLayout());
        add(barra, BorderLayout.NORTH);
        add(pestanas, BorderLayout.CENTER);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1050, 650);
        setLocationRelativeTo(null);
    }

    private void avanzar(int dias) {
        int resueltas = garantias.avanzarDias(dias);
        actualizarFecha();
        panelGarantias.refrescar();
        if (resueltas > 0) {
            Util.info(this, garantias.getPrincipal().getNombre() + " respondió " + resueltas
                    + " solicitud(es) de garantía. Revise la pestaña Garantías.");
        }
    }

    private void actualizarFecha() { lblFecha.setText(reloj.hoy().toString()); }
}
