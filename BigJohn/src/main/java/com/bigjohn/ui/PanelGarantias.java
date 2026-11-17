package com.bigjohn.ui;

import com.bigjohn.comun.Sucursal;
import com.bigjohn.garantias.Bono;
import com.bigjohn.garantias.GarantiaServicio;
import com.bigjohn.garantias.SolicitudGarantia;
import com.bigjohn.ventas.DetalleVenta;
import com.bigjohn.ventas.Venta;
import com.bigjohn.ventas.VentaServicio;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/** Pantalla del proceso de garantia. */
public class PanelGarantias extends JPanel {
    private final GarantiaServicio garantias;
    private final VentaServicio ventas;
    private final JComboBox<Venta> cmbVenta = new JComboBox<>();
    private final JComboBox<DetalleVenta> cmbProducto = new JComboBox<>();
    private final JComboBox<Sucursal> cmbOrigen;
    private final JLabel lblPlazo = new JLabel(" ");
    private final JTextField txtMotivo = new JTextField(30);
    private final DefaultTableModel modelo = Util.modelo("N°", "Venta", "Producto", "Usuario", "Origen",
            "Estado", "Enviada", "Respuesta estimada", "Resolución");
    private final JTable tabla = new JTable(modelo);
    private final JTextArea txtDetalle = new JTextArea(5, 40);
    private List<SolicitudGarantia> filas = new ArrayList<>();

    public PanelGarantias(GarantiaServicio garantias, VentaServicio ventas, List<Sucursal> sucursales) {
        this.garantias = garantias;
        this.ventas = ventas;
        setLayout(new BorderLayout(8, 8));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        cmbOrigen = new JComboBox<>(sucursales.toArray(new Sucursal[0]));
        cmbVenta.addActionListener(e -> cargarProductosDeVenta());

        // --- Formulario de solicitud ---
        JButton btnSolicitar = new JButton("Registrar y enviar a Medellín");
        btnSolicitar.addActionListener(e -> solicitar());
        JPanel fila1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        fila1.add(new JLabel("Venta:")); fila1.add(cmbVenta);
        fila1.add(new JLabel("Producto:")); fila1.add(cmbProducto);
        fila1.add(new JLabel("Se registra en:")); fila1.add(cmbOrigen);
        JPanel fila2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        fila2.add(new JLabel("Defecto / motivo:")); fila2.add(txtMotivo);
        fila2.add(btnSolicitar);
        JPanel fila0 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        fila0.add(lblPlazo);
        JPanel superior = new JPanel(new GridLayout(3, 1));
        superior.setBorder(new TitledBorder("Nueva solicitud de garantía (plazo: " + GarantiaServicio.MESES_PLAZO
                + " mes desde la compra; Medellín responde " + GarantiaServicio.DIAS_RESPUESTA + " días después)"));
        superior.add(fila1);
        superior.add(fila0);
        superior.add(fila2);
        add(superior, BorderLayout.NORTH);

        // --- Tabla de solicitudes ---
        JScrollPane scTabla = new JScrollPane(tabla);
        scTabla.setBorder(new TitledBorder("Solicitudes"));
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.getSelectionModel().addListSelectionListener(e -> { if (!e.getValueIsAdjusting()) mostrarDetalle(); });
        add(scTabla, BorderLayout.CENTER);

        // --- Detalle y redencion del bono ---
        txtDetalle.setEditable(false);
        txtDetalle.setLineWrap(true);
        txtDetalle.setWrapStyleWord(true);
        JScrollPane scDetalle = new JScrollPane(txtDetalle);
        scDetalle.setBorder(new TitledBorder("Detalle de la solicitud seleccionada"));
        add(scDetalle, BorderLayout.SOUTH);

        refrescar();
    }

    /** Vuelve a cargar ventas y solicitudes (se llama al abrir la pestaña o al avanzar el reloj). */
    public void refrescar() {
        Venta seleccionada = (Venta) cmbVenta.getSelectedItem();
        cmbVenta.removeAllItems();
        ventas.listar().forEach(cmbVenta::addItem);
        if (seleccionada != null) cmbVenta.setSelectedItem(seleccionada);
        cargarProductosDeVenta();

        int filaSel = tabla.getSelectedRow();
        filas = garantias.listarSolicitudes();
        modelo.setRowCount(0);
        for (SolicitudGarantia s : filas) {
            modelo.addRow(new Object[]{s.getIdSolicitud(), "#" + s.getIdVenta(), s.getProducto().getNombreProducto(),
                    s.getUsuario().getNombre(), s.getSucursalOrigen().getNombre(), s.getEstado(),
                    s.getFechaEnvio(), s.getFechaRespuestaEstimada(), s.getResolucion()});
        }
        if (filaSel >= 0 && filaSel < filas.size()) tabla.setRowSelectionInterval(filaSel, filaSel);
        mostrarDetalle();
    }

    private void cargarProductosDeVenta() {
        cmbProducto.removeAllItems();
        Venta v = (Venta) cmbVenta.getSelectedItem();
        if (v != null) v.getDetalles().forEach(cmbProducto::addItem);
        mostrarPlazo(v);
    }

    private void mostrarPlazo(Venta v) {
        if (v == null) { lblPlazo.setText(" "); return; }
        boolean vigente = garantias.dentroDePlazo(v);
        lblPlazo.setForeground(vigente ? new Color(0, 120, 0) : Color.RED);
        lblPlazo.setText((vigente ? "Se puede pedir garantía hasta el " : "Plazo vencido el ") + garantias.fechaLimite(v));
    }

    private void solicitar() {
        Venta v = (Venta) cmbVenta.getSelectedItem();
        DetalleVenta d = (DetalleVenta) cmbProducto.getSelectedItem();
        if (v == null || d == null) { Util.aviso(this, "Primero registre una venta y seleccione el producto."); return; }
        try {
            SolicitudGarantia s = garantias.solicitar(v.getIdVenta(), d.getProducto().getIdProducto(),
                    (Sucursal) cmbOrigen.getSelectedItem(), txtMotivo.getText());
            txtMotivo.setText("");
            refrescar();
            Util.info(this, "Solicitud #" + s.getIdSolicitud() + " registrada y enviada a "
                    + garantias.getPrincipal().getNombre() + ".\nRespuesta estimada: " + s.getFechaRespuestaEstimada()
                    + "\n(Use \"+8 días\" arriba para simular la respuesta.)");
        } catch (IllegalArgumentException ex) {
            Util.aviso(this, ex.getMessage());
        }
    }

    private void mostrarDetalle() {
        int fila = tabla.getSelectedRow();
        if (fila < 0 || fila >= filas.size()) { txtDetalle.setText("Seleccione una solicitud para ver el detalle."); return; }
        SolicitudGarantia s = filas.get(fila);
        StringBuilder sb = new StringBuilder();
        sb.append("Motivo: ").append(s.getMotivo()).append("\n");
        sb.append("Valor de la prenda: ").append(Util.dinero(s.getValorPrenda())).append("\n");
        sb.append("Estado: ").append(s.getEstado()).append("\n");
        if (s.getFechaRespuesta() != null) {
            sb.append("Respuesta de ").append(garantias.getPrincipal().getNombre()).append(" (")
                    .append(s.getFechaRespuesta()).append("): ").append(s.getResolucion()).append("\n");
            sb.append(s.getObservacion()).append("\n");
        } else {
            sb.append("Esperando respuesta de ").append(garantias.getPrincipal().getNombre())
                    .append(" (estimada: ").append(s.getFechaRespuestaEstimada()).append(")\n");
        }
        Bono b = s.getBono();
        if (b != null) {
            sb.append("Bono ").append(b.getCodigo()).append(" por ").append(Util.dinero(b.getValor())).append(": ");
            sb.append(b.isRedimido()
                    ? "redimido el " + b.getFechaRedencion() + " en " + b.getSucursalRedencion().getNombre()
                            + " (venta #" + b.getIdVentaRedencion() + ")"
                    : "disponible. Se paga en la pestaña Ventas con el código.");
        }
        txtDetalle.setText(sb.toString());
    }
}
