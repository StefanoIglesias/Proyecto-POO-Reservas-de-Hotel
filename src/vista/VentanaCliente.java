package vista;

import modelo.*;
import servicio.*;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class VentanaCliente extends JFrame {
    private ServicioCliente servicioCliente;
    private ServicioHabitacion servicioHabitacion;
    private ServicioReserva servicioReserva;
    private ServicioDetalleReserva servicioDetalleReserva;

    // Componentes Clientes
    private JTextField txtCliDni, txtCliNombres, txtCliApellidos, txtCliCelular, txtCliEmail;
    private JComboBox<String> cmbCliTipo;
    private JTable tablaClientes;
    private DefaultTableModel modeloClientes;

    // Componentes Habitaciones
    private JTextField txtHabId, txtHabPiso, txtHabNumero;
    private JComboBox<String> cmbHabTipo;
    private JTable tablaHabitaciones;
    private DefaultTableModel modeloHabitaciones;
    private JPanel panelContenedorPisos;

    // Componentes Reservas
    private JTextField txtResId, txtResFechaInicio, txtResDias;
    private JComboBox<String> cmbResCliente, cmbResMetodoPago;
    private JList<String> listHabitacionesMultiples;
    private JTable tablaReservas;
    private DefaultTableModel modeloReservas;

    public VentanaCliente() {
        servicioCliente = new ServicioCliente();
        servicioHabitacion = new ServicioHabitacion();
        servicioReserva = new ServicioReserva();
        servicioDetalleReserva = new ServicioDetalleReserva();
        
        setTitle("Sistema de Gestión de Reservas de Hotel");
        setSize(1050, 780);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane pestanas = new JTabbedPane();
        pestanas.addTab("Gestión de Clientes", crearPanelClientes());
        pestanas.addTab("Gestión de Habitaciones", crearPanelHabitaciones());
        pestanas.addTab("Registro de Reservas", crearPanelReservas());

        add(pestanas);
        
        cargarDatosClientes();
        cargarDatosHabitaciones();
        cargarDatosReservas();
        actualizarCombosReserva();
    }

    private boolean esNumerico(String texto) {
        return texto != null && texto.matches("\\d+");
    }

    private JPanel crearPanelClientes() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel pForm = new JPanel(new GridLayout(6, 2, 5, 5));
        pForm.setBorder(BorderFactory.createTitledBorder("Datos del Cliente"));

        pForm.add(new JLabel("DNI (8 dígitos):"));
        txtCliDni = new JTextField();
        pForm.add(txtCliDni);
        pForm.add(new JLabel("Nombres:"));
        txtCliNombres = new JTextField();
        pForm.add(txtCliNombres);
        pForm.add(new JLabel("Apellidos:"));
        txtCliApellidos = new JTextField();
        pForm.add(txtCliApellidos);
        pForm.add(new JLabel("Celular:"));
        txtCliCelular = new JTextField();
        pForm.add(txtCliCelular);
        pForm.add(new JLabel("Email:"));
        txtCliEmail = new JTextField();
        pForm.add(txtCliEmail);
        pForm.add(new JLabel("Tipo de Cliente:"));
        cmbCliTipo = new JComboBox<>(new String[]{"Normal", "Premium"});
        pForm.add(cmbCliTipo);

        panel.add(pForm, BorderLayout.NORTH);

        modeloClientes = new DefaultTableModel(new String[]{"DNI", "Nombres", "Apellidos", "Celular", "Email", "Tipo"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaClientes = new JTable(modeloClientes);
        panel.add(new JScrollPane(tablaClientes), BorderLayout.CENTER);

        JPanel pBotones = new JPanel();
        JButton btnGuardar = new JButton("Guardar");
        JButton btnModificar = new JButton("Modificar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnLimpiar = new JButton("Limpiar");

        pBotones.add(btnGuardar);
        pBotones.add(btnModificar);
        pBotones.add(btnEliminar);
        pBotones.add(btnLimpiar);
        panel.add(pBotones, BorderLayout.SOUTH);

        tablaClientes.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int fila = tablaClientes.getSelectedRow();
                if (fila != -1) {
                    txtCliDni.setText(modeloClientes.getValueAt(fila, 0).toString());
                    txtCliDni.setEnabled(false);
                    txtCliNombres.setText(modeloClientes.getValueAt(fila, 1).toString());
                    txtCliApellidos.setText(modeloClientes.getValueAt(fila, 2).toString());
                    txtCliCelular.setText(modeloClientes.getValueAt(fila, 3).toString());
                    txtCliEmail.setText(modeloClientes.getValueAt(fila, 4).toString());
                    cmbCliTipo.setSelectedItem(modeloClientes.getValueAt(fila, 5).toString());
                }
            }
        });

        btnGuardar.addActionListener(e -> guardarCliente());
        btnModificar.addActionListener(e -> modificarCliente());
        btnEliminar.addActionListener(e -> eliminarCliente());
        btnLimpiar.addActionListener(e -> limpiarCamposCliente());

        return panel;
    }

    private JPanel crearPanelHabitaciones() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel pForm = new JPanel(new GridLayout(4, 2, 5, 5));
        pForm.setBorder(BorderFactory.createTitledBorder("Datos de la Habitación (Tarifas Fijas)"));

        pForm.add(new JLabel("ID Habitación (Numérico):"));
        txtHabId = new JTextField();
        pForm.add(txtHabId);
        pForm.add(new JLabel("ID Piso (Numérico):"));
        txtHabPiso = new JTextField();
        pForm.add(txtHabPiso);
        pForm.add(new JLabel("Número de Habitación (Numérico):"));
        txtHabNumero = new JTextField();
        pForm.add(txtHabNumero);
        pForm.add(new JLabel("Tipo y Tarifa:"));
        cmbHabTipo = new JComboBox<>(new String[]{
            "1.- Personal - 50 S/ noche", 
            "2.- Doble - 100 S/ noche", 
            "3.- Triple - 150 S/ noche", 
            "4.- Suite - 200 S/ noche", 
            "5.- Matrimonial - 250 S/ noche"
        });
        pForm.add(cmbHabTipo);

        panel.add(pForm, BorderLayout.NORTH);

        JTabbedPane subPestanasHab = new JTabbedPane();
        modeloHabitaciones = new DefaultTableModel(new String[]{"ID", "Piso", "Número", "Tipo / Tarifa"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaHabitaciones = new JTable(modeloHabitaciones);
        subPestanasHab.addTab("Lista en Tabla", new JScrollPane(tablaHabitaciones));

        panelContenedorPisos = new JPanel();
        panelContenedorPisos.setLayout(new BoxLayout(panelContenedorPisos, BoxLayout.Y_AXIS));
        JScrollPane scrollTarjetas = new JScrollPane(panelContenedorPisos);
        scrollTarjetas.getVerticalScrollBar().setUnitIncrement(16);
        subPestanasHab.addTab("Mapa de Estado por Pisos", scrollTarjetas);

        panel.add(subPestanasHab, BorderLayout.CENTER);

        JPanel pBotones = new JPanel();
        JButton btnGuardar = new JButton("Guardar");
        JButton btnModificar = new JButton("Modificar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnLimpiar = new JButton("Limpiar");
        JButton btnRefrescarMapa = new JButton("Actualizar Mapa");

        pBotones.add(btnGuardar);
        pBotones.add(btnModificar);
        pBotones.add(btnEliminar);
        pBotones.add(btnLimpiar);
        pBotones.add(btnRefrescarMapa);
        panel.add(pBotones, BorderLayout.SOUTH);

        tablaHabitaciones.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int fila = tablaHabitaciones.getSelectedRow();
                if (fila != -1) {
                    txtHabId.setText(modeloHabitaciones.getValueAt(fila, 0).toString());
                    txtHabId.setEnabled(false);
                    txtHabPiso.setText(modeloHabitaciones.getValueAt(fila, 1).toString());
                    txtHabNumero.setText(modeloHabitaciones.getValueAt(fila, 2).toString());
                    cmbHabTipo.setSelectedIndex(Integer.parseInt(modeloHabitaciones.getValueAt(fila, 3).toString().split("\\.-")[0]) - 1);
                }
            }
        });

        btnGuardar.addActionListener(e -> guardarHabitacion());
        btnModificar.addActionListener(e -> modificarHabitacion());
        btnEliminar.addActionListener(e -> eliminarHabitacion());
        btnLimpiar.addActionListener(e -> limpiarCamposHabitacion());
        btnRefrescarMapa.addActionListener(e -> actualizarMapaHabitaciones());

        return panel;
    }

    private JPanel crearPanelReservas() {
        JPanel panelPrincipal = new JPanel(new BorderLayout());

        // Panel superior con formulario compacto de altura fija
        JPanel pForm = new JPanel(new GridBagLayout());
        pForm.setBorder(BorderFactory.createTitledBorder("Gestión de Reservas Múltiples"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row; pForm.add(new JLabel("ID Reserva:"), gbc);
        gbc.gridx = 1; txtResId = new JTextField(15); pForm.add(txtResId, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; pForm.add(new JLabel("Fecha Inicio (DD/MM/AAAA):"), gbc);
        gbc.gridx = 1; txtResFechaInicio = new JTextField(15); pForm.add(txtResFechaInicio, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; pForm.add(new JLabel("Días de Estadía:"), gbc);
        gbc.gridx = 1; txtResDias = new JTextField(15); pForm.add(txtResDias, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; pForm.add(new JLabel("Cliente (DNI - Apellido):"), gbc);
        gbc.gridx = 1; cmbResCliente = new JComboBox<>(); pForm.add(cmbResCliente, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; pForm.add(new JLabel("Habitaciones (Ctrl+Click):"), gbc);
        listHabitacionesMultiples = new JList<>();
        listHabitacionesMultiples.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane scrollHabList = new JScrollPane(listHabitacionesMultiples);
        scrollHabList.setPreferredSize(new Dimension(200, 70));
        gbc.gridx = 1; pForm.add(scrollHabList, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; pForm.add(new JLabel("Método de Pago:"), gbc);
        gbc.gridx = 1; cmbResMetodoPago = new JComboBox<>(new String[]{"1.- Yape", "2.- Plin", "3.- Transferencia", "4.- Efectivo"}); pForm.add(cmbResMetodoPago, gbc);

        // Envolvemos el formulario en un JScrollPane para evitar problemas si la ventana es pequeña
        JScrollPane scrollForm = new JScrollPane(pForm);
        scrollForm.setPreferredSize(new Dimension(1000, 260));
        panelPrincipal.add(scrollForm, BorderLayout.NORTH);

        // Panel central: Tabla de Reservas visible y con scroll
        modeloReservas = new DefaultTableModel(new String[]{"ID Reserva", "F. Inicio", "Días", "F. Salida", "DNI Cliente", "Habitaciones", "Monto Total", "Pago"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaReservas = new JTable(modeloReservas);
        panelPrincipal.add(new JScrollPane(tablaReservas), BorderLayout.CENTER);

        // Panel inferior: Botones
        JPanel pBotones = new JPanel();
        JButton btnGuardar = new JButton("Guardar");
        JButton btnModificar = new JButton("Modificar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnLimpiar = new JButton("Limpiar");
        JButton btnRefrescar = new JButton("Refrescar Listas");

        pBotones.add(btnGuardar);
        pBotones.add(btnModificar);
        pBotones.add(btnEliminar);
        pBotones.add(btnLimpiar);
        pBotones.add(btnRefrescar);
        panelPrincipal.add(pBotones, BorderLayout.SOUTH);

        btnGuardar.addActionListener(e -> realizarReserva());
        btnModificar.addActionListener(e -> modificarReserva());
        btnEliminar.addActionListener(e -> eliminarReserva());
        btnLimpiar.addActionListener(e -> limpiarCamposReserva());
        btnRefrescar.addActionListener(e -> actualizarCombosReserva());

        return panelPrincipal;
    }

    // --- MAPA DINÁMICO DE HABITACIONES CON TEXTO DE TIPO CLARO ---
    private void actualizarMapaHabitaciones() {
        panelContenedorPisos.removeAll();
        List<Habitacion> habitaciones = servicioHabitacion.listarHabitaciones();
        List<DetalleReserva> detalles = servicioDetalleReserva.listarDetalles();
        List<Reserva> reservas = servicioReserva.listarReservas();
        List<Cliente> clientes = servicioCliente.listarClientes();

        Map<String, List<Habitacion>> habitacionesPorPiso = habitaciones.stream()
                .collect(Collectors.groupingBy(Habitacion::getIdPiso));

        if (habitacionesPorPiso.isEmpty()) {
            JLabel lblVacio = new JLabel("No hay habitaciones registradas.", JLabel.CENTER);
            panelContenedorPisos.add(lblVacio);
        } else {
            for (Map.Entry<String, List<Habitacion>> entry : habitacionesPorPiso.entrySet()) {
                String piso = entry.getKey();
                JPanel panelPiso = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
                panelPiso.setBorder(BorderFactory.createTitledBorder("--- Piso " + piso + " ---"));

                for (Habitacion h : entry.getValue()) {
                    String apellidoOcupante = "";
                    boolean ocupada = false;

                    for (DetalleReserva d : detalles) {
                        if (d.getIdHabitacion().equalsIgnoreCase(h.getIdHabitacion())) {
                            for (Reserva r : reservas) {
                                if (r.getIdReserva().equalsIgnoreCase(d.getIdReserva())) {
                                    ocupada = true;
                                    for (Cliente c : clientes) {
                                        if (c.getDni().equalsIgnoreCase(r.getDniCliente())) {
                                            apellidoOcupante = c.getApellidos();
                                            break;
                                        }
                                    }
                                    break;
                                }
                            }
                        }
                        if (ocupada) break;
                    }

                    JPanel tarjeta = new JPanel(new BorderLayout());
                    tarjeta.setPreferredSize(new Dimension(160, 105));
                    tarjeta.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));
                    tarjeta.setBackground(ocupada ? new Color(253, 237, 236) : new Color(234, 250, 234));

                    JLabel lblNum = new JLabel("Hab. " + h.getNumero(), JLabel.CENTER);
                    lblNum.setFont(new Font("Arial", Font.BOLD, 12));
                    
                    // Extraer solo el nombre del tipo (ej: "Personal", "Doble", etc., sin el número de índice)
                    String textoTipoLimpio = h.getTipoTexto().replaceAll("^\\d+\\.-\\s*", "").split("-")[0].trim();
                    JLabel lblTipo = new JLabel(textoTipoLimpio, JLabel.CENTER);
                    lblTipo.setFont(new Font("Arial", Font.PLAIN, 11));

                    JLabel lblEstado = new JLabel(ocupada ? "OCUPADA (" + apellidoOcupante + ")" : "DISPONIBLE", JLabel.CENTER);
                    lblEstado.setFont(new Font("Arial", Font.BOLD, 10));
                    lblEstado.setForeground(ocupada ? new Color(192, 57, 43) : new Color(39, 174, 96));

                    JPanel pInfo = new JPanel(new GridLayout(3, 1));
                    pInfo.setOpaque(false);
                    pInfo.add(lblNum);
                    pInfo.add(lblTipo);
                    pInfo.add(lblEstado);

                    tarjeta.add(pInfo, BorderLayout.CENTER);
                    panelPiso.add(tarjeta);
                }
                panelContenedorPisos.add(panelPiso);
            }
        }
        panelContenedorPisos.revalidate();
        panelContenedorPisos.repaint();
    }

    // --- MÉTODOS CLIENTES Y HABITACIONES ---
    private void cargarDatosClientes() {
        modeloClientes.setRowCount(0);
        for (Cliente c : servicioCliente.listarClientes()) {
            modeloClientes.addRow(new Object[]{c.getDni(), c.getNombres(), c.getApellidos(), c.getCelular(), c.getEmail(), c.getTipo() == 2 ? "Premium" : "Normal"});
        }
    }

    private void guardarCliente() {
        String dni = txtCliDni.getText().trim();
        String cel = txtCliCelular.getText().trim();
        if (dni.isEmpty() || txtCliNombres.getText().trim().isEmpty() || txtCliApellidos.getText().trim().isEmpty() || cel.isEmpty() || txtCliEmail.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Complete todos los campos.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!dni.matches("\\d{8}") || !cel.matches("9\\d{8}")) {
            JOptionPane.showMessageDialog(this, "DNI debe tener 8 dígitos y Celular 9 dígitos empezando con 9.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Cliente c = new Cliente(dni, txtCliNombres.getText().trim(), txtCliApellidos.getText().trim(), cel, txtCliEmail.getText().trim(), cmbCliTipo.getSelectedIndex() + 1);
        if (servicioCliente.guardarCliente(c)) {
            JOptionPane.showMessageDialog(this, "Cliente guardado.");
            cargarDatosClientes();
            actualizarCombosReserva();
            limpiarCamposCliente();
        } else {
            JOptionPane.showMessageDialog(this, "El DNI ya existe.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void modificarCliente() {
        if (txtCliDni.isEnabled()) {
            JOptionPane.showMessageDialog(this, "Seleccione un cliente de la tabla.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Cliente c = new Cliente(txtCliDni.getText().trim(), txtCliNombres.getText().trim(), txtCliApellidos.getText().trim(), txtCliCelular.getText().trim(), txtCliEmail.getText().trim(), cmbCliTipo.getSelectedIndex() + 1);
        if (servicioCliente.modificarCliente(c)) {
            JOptionPane.showMessageDialog(this, "Cliente modificado.");
            cargarDatosClientes();
            actualizarCombosReserva();
            limpiarCamposCliente();
        }
    }

    private void eliminarCliente() {
        int fila = tablaClientes.getSelectedRow();
        if (fila == -1) return;
        if (servicioCliente.eliminarCliente(modeloClientes.getValueAt(fila, 0).toString())) {
            cargarDatosClientes();
            actualizarCombosReserva();
            limpiarCamposCliente();
        }
    }

    private void limpiarCamposCliente() {
        txtCliDni.setText(""); txtCliDni.setEnabled(true);
        txtCliNombres.setText(""); txtCliApellidos.setText("");
        txtCliCelular.setText(""); txtCliEmail.setText("");
        cmbCliTipo.setSelectedIndex(0); tablaClientes.clearSelection();
    }

    private void cargarDatosHabitaciones() {
        modeloHabitaciones.setRowCount(0);
        for (Habitacion h : servicioHabitacion.listarHabitaciones()) {
            modeloHabitaciones.addRow(new Object[]{h.getIdHabitacion(), h.getIdPiso(), h.getNumero(), h.getTipoTexto()});
        }
        actualizarMapaHabitaciones();
    }

    private void guardarHabitacion() {
        String id = txtHabId.getText().trim(), piso = txtHabPiso.getText().trim(), num = txtHabNumero.getText().trim();
        if (id.isEmpty() || piso.isEmpty() || num.isEmpty() || !esNumerico(id) || !esNumerico(piso) || !esNumerico(num)) {
            JOptionPane.showMessageDialog(this, "Complete correctamente los campos numéricos.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Habitacion h = new Habitacion(id, piso, num, cmbHabTipo.getSelectedIndex() + 1);
        if (servicioHabitacion.guardarHabitacion(h)) {
            cargarDatosHabitaciones();
            actualizarCombosReserva();
            limpiarCamposHabitacion();
        }
    }

    private void modificarHabitacion() {
        if (txtHabId.isEnabled()) return;
        Habitacion h = new Habitacion(txtHabId.getText().trim(), txtHabPiso.getText().trim(), txtHabNumero.getText().trim(), cmbHabTipo.getSelectedIndex() + 1);
        if (servicioHabitacion.modificarHabitacion(h)) {
            cargarDatosHabitaciones();
            actualizarCombosReserva();
            limpiarCamposHabitacion();
        }
    }

    private void eliminarHabitacion() {
        int fila = tablaHabitaciones.getSelectedRow();
        if (fila == -1) return;
        if (servicioHabitacion.eliminarHabitacion(modeloHabitaciones.getValueAt(fila, 0).toString())) {
            cargarDatosHabitaciones();
            actualizarCombosReserva();
            limpiarCamposHabitacion();
        }
    }

    private void limpiarCamposHabitacion() {
        txtHabId.setText(""); txtHabId.setEnabled(true);
        txtHabPiso.setText(""); txtHabNumero.setText("");
        cmbHabTipo.setSelectedIndex(0); tablaHabitaciones.clearSelection();
    }

    // --- MÉTODOS DE RESERVAS MÚLTIPLES ---
    private void cargarDatosReservas() {
        modeloReservas.setRowCount(0);
        List<Reserva> reservas = servicioReserva.listarReservas();
        List<DetalleReserva> detalles = servicioDetalleReserva.listarDetalles();

        for (Reserva r : reservas) {
            StringBuilder habsStr = new StringBuilder();
            for (DetalleReserva d : detalles) {
                if (d.getIdReserva().equalsIgnoreCase(r.getIdReserva())) {
                    habsStr.append(d.getIdHabitacion()).append(" ");
                }
            }
            modeloReservas.addRow(new Object[]{
                r.getIdReserva(), r.getFechaInicio(), r.getDiasEstadia(), r.getFechaSalida(), r.getDniCliente(), habsStr.toString().trim(), r.getMontoTotal(), r.getMetodoPagoTexto()
            });
        }
        actualizarMapaHabitaciones();
    }

    private void actualizarCombosReserva() {
        cmbResCliente.removeAllItems();
        for (Cliente c : servicioCliente.listarClientes()) {
            cmbResCliente.addItem(c.getDni() + " - " + c.getApellidos() + ", " + c.getNombres());
        }

        DefaultListModel<String> listModel = new DefaultListModel<>();
        for (Habitacion h : servicioHabitacion.listarHabitaciones()) {
            listModel.addElement(h.getIdHabitacion() + " (Hab. " + h.getNumero() + " - " + h.getTipoTexto() + ")");
        }
        listHabitacionesMultiples.setModel(listModel);
    }

    private void realizarReserva() {
        String idRes = txtResId.getText().trim();
        String fechaInicioStr = txtResFechaInicio.getText().trim();
        String diasStr = txtResDias.getText().trim();
        List<String> seleccionadas = listHabitacionesMultiples.getSelectedValuesList();

        if (idRes.isEmpty() || fechaInicioStr.isEmpty() || diasStr.isEmpty() || seleccionadas.isEmpty() || !esNumerico(idRes) || !esNumerico(diasStr)) {
            JOptionPane.showMessageDialog(this, "Complete todos los campos y seleccione al menos una habitación.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate fInicio = LocalDate.parse(fechaInicioStr, formatter);
            int dias = Integer.parseInt(diasStr);
            LocalDate fSalida = fInicio.plusDays(dias);
            String fechaSalidaStr = fSalida.format(formatter);

            double montoTotal = 0.0;
            List<Habitacion> todasHabitaciones = servicioHabitacion.listarHabitaciones();

            for (String item : seleccionadas) {
                String idHab = item.split(" ")[0];
                for (Habitacion h : todasHabitaciones) {
                    if (h.getIdHabitacion().equalsIgnoreCase(idHab)) {
                        montoTotal += h.getPrecioNoche() * dias;
                        break;
                    }
                }
            }

            String dniCliente = cmbResCliente.getSelectedItem().toString().split(" - ")[0];
            int metodoPago = cmbResMetodoPago.getSelectedIndex() + 1;

            Reserva r = new Reserva(idRes, fechaInicioStr, dias, fechaSalidaStr, dniCliente, montoTotal, metodoPago);

            if (servicioReserva.guardarReserva(r)) {
                int contador = 1;
                for (String item : seleccionadas) {
                    String idHab = item.split(" ")[0];
                    DetalleReserva d = new DetalleReserva(idRes + "-" + contador++, idRes, idHab);
                    servicioDetalleReserva.guardarDetalle(d);
                }
                JOptionPane.showMessageDialog(this, "Reserva múltiple guardada con éxito.\nMonto Total: S/ " + montoTotal + "\nFecha Salida: " + fechaSalidaStr);
                cargarDatosReservas();
                limpiarCamposReserva();
            } else {
                JOptionPane.showMessageDialog(this, "El ID de la reserva ya existe.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Formato de fecha inválido. Use DD/MM/AAAA", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void modificarReserva() {
        JOptionPane.showMessageDialog(this, "Para modificar una reserva, elimínela y vuelva a registrarla para mantener la integridad de las habitaciones.");
    }

    private void eliminarReserva() {
        int fila = tablaReservas.getSelectedRow();
        if (fila == -1) return;
        String idRes = modeloReservas.getValueAt(fila, 0).toString();
        if (servicioReserva.eliminarReserva(idRes)) {
            servicioDetalleReserva.eliminarDetallePorReserva(idRes);
            JOptionPane.showMessageDialog(this, "Reserva eliminada.");
            cargarDatosReservas();
            limpiarCamposReserva();
        }
    }

    private void limpiarCamposReserva() {
        txtResId.setText(""); txtResFechaInicio.setText(""); txtResDias.setText("");
        listHabitacionesMultiples.clearSelection();
        tablaReservas.clearSelection();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new VentanaCliente().setVisible(true));
    }
}

