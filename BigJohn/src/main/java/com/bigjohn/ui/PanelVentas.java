package com.bigjohn.ui;

import com.bigjohn.comun.Asesor;
import com.bigjohn.comun.Producto;
import com.bigjohn.comun.Sucursal;
import com.bigjohn.comun.Usuario;
import com.bigjohn.comun.UsuarioServicio;
import com.bigjohn.ventas.MetodoPago;
import com.bigjohn.ventas.Venta;
import com.bigjohn.ventas.VentaServicio;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Pantalla del proceso de venta. */
public class PanelVentas extends JPanel {
    private final VentaServicio servicio;
    private final UsuarioServicio usuarios;
    private final DefaultComboBoxModel<Usuario> modeloUsuarios = new DefaultComboBoxModel<>();
    private final JComboBox<Usuario> cmbUsuario = new JComboBox<>(modeloUsuarios);
    private final JComboBox<Sucursal> cmbSucursal;
    private final JComboBox<Asesor> cmbAsesor = new JComboBox<>();
    private final JComboBox<MetodoPago> cmbMetodo = new JComboBox<>(
            new MetodoPago[]{MetodoPago.EFECTIVO, MetodoPago.TARJETA, MetodoPago.TRANSFERENCIA});
    private final JComboBox<Producto> cmbProducto;
    private final JSpinner spCantidad = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
    private final JTextField txtBono = new JTextField(10);
    private final JLabel lblBono = new JLabel(" ");
    private final DefaultTableModel modeloCarrito = Util.modelo("Producto", "Cantidad", "Precio", "Subtotal");
    private final DefaultTableModel modeloVentas = Util.modelo("N°", "Fecha", "Usuario", "Asesor", "Sucursal", "Pago", "Total", "Factura");
    private final JTable tblCarrito = new JTable(modeloCarrito);
    private final JLabel lblTotal = new JLabel("Total: " + Util.dinero(0));
    private final Map<Producto, Integer> carrito = new LinkedHashMap<>();

    public PanelVentas(VentaServicio servicio, UsuarioServicio usuarios, List<Sucursal> sucursales, List<Producto> productos) {
        this.servicio = servicio;
        this.usuarios = usuarios;
        setLayout(new BorderLayout(8, 8));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        cmbSucursal = new JComboBox<>(sucursales.toArray(new Sucursal[0]));
        cmbProducto = new JComboBox<>(productos.toArray(new Producto[0]));
        cmbProducto.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean sel, boolean foco) {
                super.getListCellRendererComponent(list, value, index, sel, foco);
                Sucursal s = (Sucursal) cmbSucursal.getSelectedItem();
                if (value instanceof Producto p && s != null) {
                    setText(p.getNombreProducto() + " - " + Util.dinero(p.getPrecio())
                            + " (stock: " + servicio.inventarioDe(s).getStock(p) + ")");
                }
                return this;
            }
        });
        cmbSucursal.addActionListener(e -> cambiarSucursal());
        recargarUsuarios();

        // --- Formulario superior ---
        JButton btnNuevoUsuario = new JButton("Nuevo usuario...");
        btnNuevoUsuario.addActionListener(e -> nuevoUsuario());
        JPanel fila1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        fila1.add(new JLabel("Usuario:")); fila1.add(cmbUsuario); fila1.add(btnNuevoUsuario);
        fila1.add(new JLabel("Sucursal:")); fila1.add(cmbSucursal);
        fila1.add(new JLabel("Asesor:")); fila1.add(cmbAsesor);

        JButton btnAgregar = new JButton("Agregar al carrito");
        JButton btnQuitar = new JButton("Quitar seleccionado");
        btnAgregar.addActionListener(e -> agregarAlCarrito());
        btnQuitar.addActionListener(e -> quitarDelCarrito());
        JPanel fila2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        fila2.add(new JLabel("Producto:")); fila2.add(cmbProducto);
        fila2.add(new JLabel("Cantidad:")); fila2.add(spCantidad);
        fila2.add(btnAgregar); fila2.add(btnQuitar);

        JButton btnVerificar = new JButton("Verificar bono");
        btnVerificar.addActionListener(e -> verificarBono());
        JPanel fila3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        fila3.add(new JLabel("Código de bono (opcional):")); fila3.add(txtBono);
        fila3.add(btnVerificar); fila3.add(lblBono);
        fila3.add(new JLabel("   Pagar (o el resto) con:")); fila3.add(cmbMetodo);

        JPanel superior = new JPanel(new GridLayout(3, 1));
        superior.add(fila1);
        superior.add(fila2);
        superior.add(fila3);
        add(superior, BorderLayout.NORTH);

        // --- Carrito y lista de ventas ---
        JScrollPane scCarrito = new JScrollPane(tblCarrito);
        scCarrito.setBorder(new TitledBorder("Carrito"));
        JScrollPane scVentas = new JScrollPane(new JTable(modeloVentas));
        scVentas.setBorder(new TitledBorder("Ventas registradas"));
        JSplitPane centro = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scCarrito, scVentas);
        centro.setResizeWeight(0.45);
        add(centro, BorderLayout.CENTER);

        // --- Pie ---
        JButton btnRegistrar = new JButton("Registrar venta");
        btnRegistrar.addActionListener(e -> registrarVenta());
        lblTotal.setFont(lblTotal.getFont().deriveFont(Font.BOLD, 15f));
        JPanel pie = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pie.add(lblTotal);
        pie.add(btnRegistrar);
        add(pie, BorderLayout.SOUTH);

        cambiarSucursal();
        refrescarVentas();
    }

    // ---------------- Usuarios ----------------

    private void recargarUsuarios() {
        Object seleccionado = modeloUsuarios.getSelectedItem();
        modeloUsuarios.removeAllElements();
        modeloUsuarios.addAll(usuarios.listar());
        if (seleccionado != null) modeloUsuarios.setSelectedItem(seleccionado);
    }

    private void nuevoUsuario() {
        JTextField nombre = new JTextField(18), documento = new JTextField(18),
                telefono = new JTextField(18), correo = new JTextField(18);
        JPasswordField clave = new JPasswordField(18);
        JPanel form = new JPanel(new GridLayout(0, 2, 6, 6));
        form.add(new JLabel("Nombre *")); form.add(nombre);
        form.add(new JLabel("Documento *")); form.add(documento);
        form.add(new JLabel("Teléfono")); form.add(telefono);
        form.add(new JLabel("Correo")); form.add(correo);
        form.add(new JLabel("Clave")); form.add(clave);

        while (true) { // si hay un error, el formulario vuelve a abrir con lo escrito
            int r = JOptionPane.showConfirmDialog(this, form, "Registrar usuario nuevo",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (r != JOptionPane.OK_OPTION) return;
            try {
                Usuario u = usuarios.registrar(nombre.getText(), documento.getText(), telefono.getText(),
                        correo.getText(), new String(clave.getPassword()));
                recargarUsuarios();
                cmbUsuario.setSelectedItem(u);
                Util.info(this, "Usuario " + u.getNombre() + " registrado.");
                return;
            } catch (IllegalArgumentException ex) {
                Util.aviso(this, ex.getMessage());
            }
        }
    }

    // ---------------- Carrito ----------------

    private void cambiarSucursal() {
        Sucursal s = (Sucursal) cmbSucursal.getSelectedItem();
        cmbAsesor.removeAllItems();
        if (s != null) s.getAsesores().forEach(cmbAsesor::addItem);
        carrito.clear(); // el stock depende de la sucursal
        refrescarCarrito();
        cmbProducto.repaint();
    }

    private void agregarAlCarrito() {
        Producto p = (Producto) cmbProducto.getSelectedItem();
        Sucursal s = (Sucursal) cmbSucursal.getSelectedItem();
        if (p == null || s == null) return;
        int total = carrito.getOrDefault(p, 0) + (int) spCantidad.getValue();
        if (servicio.inventarioDe(s).getStock(p) < total) {
            Util.aviso(this, "Stock insuficiente de " + p.getNombreProducto() + " en " + s.getNombre() + ".");
            return;
        }
        carrito.put(p, total);
        refrescarCarrito();
    }

    private void quitarDelCarrito() {
        int fila = tblCarrito.getSelectedRow();
        if (fila < 0) { Util.aviso(this, "Seleccione un producto del carrito."); return; }
        carrito.remove(new ArrayList<>(carrito.keySet()).get(fila));
        refrescarCarrito();
    }

    private double totalCarrito() {
        return carrito.entrySet().stream().mapToDouble(e -> e.getKey().getPrecio() * e.getValue()).sum();
    }

    // ---------------- Bono y venta ----------------

    private void verificarBono() {
        String codigo = txtBono.getText().trim();
        if (codigo.isEmpty()) { lblBono.setText(" "); return; }
        try {
            double valor = servicio.valorBonoDisponible(codigo);
            double total = totalCarrito();
            String extra = total <= 0 ? "" : valor >= total ? " - cubre toda la compra"
                    : " - faltan " + Util.dinero(total - valor);
            lblBono.setForeground(new Color(0, 120, 0));
            lblBono.setText("Disponible por " + Util.dinero(valor) + extra);
        } catch (IllegalArgumentException ex) {
            lblBono.setForeground(Color.RED);
            lblBono.setText(ex.getMessage());
        }
    }

    private void registrarVenta() {
        try {
            String codigo = txtBono.getText().trim();
            if (!codigo.isEmpty()) {
                double valor = servicio.valorBonoDisponible(codigo); // verifica que exista y no este redimido
                double total = totalCarrito();
                if (total > 0 && valor > total) {
                    int r = JOptionPane.showConfirmDialog(this,
                            "El bono (" + Util.dinero(valor) + ") es mayor al total de la compra (" + Util.dinero(total)
                                    + ").\nEl bono se usa completo y el excedente no se devuelve. ¿Continuar?",
                            "Bono mayor al total", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                    if (r != JOptionPane.YES_OPTION) return;
                }
            }
            Venta v = servicio.registrarVenta(
                    (Usuario) cmbUsuario.getSelectedItem(),
                    (Asesor) cmbAsesor.getSelectedItem(),
                    (Sucursal) cmbSucursal.getSelectedItem(),
                    new LinkedHashMap<>(carrito),
                    (MetodoPago) cmbMetodo.getSelectedItem(),
                    codigo);
            StringBuilder pagos = new StringBuilder();
            v.getPagos().forEach(p -> pagos.append("\n  - ").append(p.getMetodo()).append(": ").append(Util.dinero(p.getMonto())));
            Util.info(this, "Venta #" + v.getIdVenta() + " registrada.\nFactura: " + v.getFactura().getNumero()
                    + "\nTotal: " + Util.dinero(v.getTotal()) + "\nPagos:" + pagos);
            carrito.clear();
            txtBono.setText("");
            lblBono.setText(" ");
            refrescarCarrito();
            refrescarVentas();
            cmbProducto.repaint();
        } catch (IllegalArgumentException ex) {
            Util.aviso(this, ex.getMessage());
        }
    }

    private void refrescarCarrito() {
        modeloCarrito.setRowCount(0);
        for (Map.Entry<Producto, Integer> e : carrito.entrySet()) {
            double subtotal = e.getKey().getPrecio() * e.getValue();
            modeloCarrito.addRow(new Object[]{e.getKey().getNombreProducto(), e.getValue(),
                    Util.dinero(e.getKey().getPrecio()), Util.dinero(subtotal)});
        }
        lblTotal.setText("Total: " + Util.dinero(totalCarrito()));
    }

    private void refrescarVentas() {
        modeloVentas.setRowCount(0);
        for (Venta v : servicio.listar()) {
            modeloVentas.addRow(new Object[]{v.getIdVenta(), v.getFecha(), v.getUsuario().getNombre(),
                    v.getAsesor().getNombre(), v.getSucursal().getNombre(), v.getMediosDePago(),
                    Util.dinero(v.getTotal()), v.getFactura().getNumero()});
        }
    }
}
