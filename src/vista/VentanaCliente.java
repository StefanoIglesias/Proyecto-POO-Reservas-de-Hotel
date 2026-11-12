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
import java.time.format.ResolverStyle;
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
        setSize(900, 680);
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
            public boolean isCellEditable(int row, int column) { return false; }
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
        pForm.setBorder(BorderFactory.createTitledBorder("Datos de la Habitación"));

        pForm.add(new JLabel("ID Habitación (Numérico):"));
        txtHabId = new JTextField();
        pForm.add(txtHabId);

        pForm.add(new JLabel("ID Piso (Numérico):"));
        txtHabPiso = new JTextField();
        pForm.add(txtHabPiso);

        pForm.add(new JLabel("Número de Habitación (Numérico):"));
        txtHabNumero = new JTextField();
        pForm.add(txtHabNumero);

        pForm.add(new JLabel("Tipo de Habitación:"));
        cmbHabTipo = new JComboBox<>(new String[]{"1.- Personal", "2.- Doble", "3.- Matrimonial", "4.- Suite", "5.- Triple"});
        pForm.add(cmbHabTipo);

        panel.add(pForm, BorderLayout.NORTH);

        modeloHabitaciones = new DefaultTableModel(new String[]{"ID", "Piso", "Número", "Tipo"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaHabitaciones = new JTable(modeloHabitaciones);
        panel.add(new JScrollPane(tablaHabitaciones), BorderLayout.CENTER);

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

        tablaHabitaciones.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int fila = tablaHabitaciones.getSelectedRow();
                if (fila != -1) {
                    txtHabId.setText(modeloHabitaciones.getValueAt(fila, 0).toString());
                    txtHabId.setEnabled(false);
                    txtHabPiso.setText(modeloHabitaciones.getValueAt(fila, 1).toString());
                    txtHabNumero.setText(modeloHabitaciones.getValueAt(fila, 2).toString());
                    
                    String tipoStr = modeloHabitaciones.getValueAt(fila, 3).toString();
                    for (int i = 0; i < cmbHabTipo.getItemCount(); i++) {
                        if (cmbHabTipo.getItemAt(i).contains(tipoStr)) {
                            cmbHabTipo.setSelectedIndex(i);
                            break;
                        }
                    }
                }
            }
        });

        btnGuardar.addActionListener(e -> guardarHabitacion());
        btnModificar.addActionListener(e -> modificarHabitacion());
        btnEliminar.addActionListener(e -> eliminarHabitacion());
        btnLimpiar.addActionListener(e -> limpiarCamposHabitacion());

        return panel;
    }


    private JPanel crearPanelReservas() {
        JPanel panelPrincipal = new JPanel(new BorderLayout());

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

        JScrollPane scrollForm = new JScrollPane(pForm);
        scrollForm.setPreferredSize(new Dimension(1000, 260));
        panelPrincipal.add(scrollForm, BorderLayout.NORTH);

        modeloReservas = new DefaultTableModel(new String[]{"ID Reserva", "F. Inicio", "Días", "F. Salida", "DNI Cliente", "Habitaciones", "Monto Total", "Pago"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaReservas = new JTable(modeloReservas);
        panelPrincipal.add(new JScrollPane(tablaReservas), BorderLayout.CENTER);

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

        tablaReservas.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int fila = tablaReservas.getSelectedRow();
                if (fila != -1) {
                    txtResId.setText(modeloReservas.getValueAt(fila, 0).toString());
                    txtResId.setEnabled(false);
                    txtResFechaInicio.setText(modeloReservas.getValueAt(fila, 1).toString());
                    txtResDias.setText(modeloReservas.getValueAt(fila, 2).toString());

                    String dniTabla = modeloReservas.getValueAt(fila, 4).toString();
                    for (int i = 0; i < cmbResCliente.getItemCount(); i++) {
                        if (cmbResCliente.getItemAt(i).startsWith(dniTabla)) {
                            cmbResCliente.setSelectedIndex(i);
                            break;
                        }
                    }

                    String habsStr = modeloReservas.getValueAt(fila, 5).toString();
                    String[] habsIds = habsStr.split("\\s+");
                    listHabitacionesMultiples.clearSelection();
                    ListModel<String> model = listHabitacionesMultiples.getModel();
                    for (int i = 0; i < model.getSize(); i++) {
                        String elemento = model.getElementAt(i);
                        String idHabElem = elemento.split(" ")[0];
                        for (String idH : habsIds) {
                            if (idHabElem.equalsIgnoreCase(idH)) {
                                listHabitacionesMultiples.addSelectionInterval(i, i);
                                break;
                            }
                        }
                    }

                    String pagoTabla = modeloReservas.getValueAt(fila, 7).toString();
                    for (int i = 0; i < cmbResMetodoPago.getItemCount(); i++) {
                        if (cmbResMetodoPago.getItemAt(i).contains(pagoTabla)) {
                            cmbResMetodoPago.setSelectedIndex(i);
                            break;
                        }
                    }
                }
            }
        });

        btnGuardar.addActionListener(e -> realizarReserva());
        btnModificar.addActionListener(e -> modificarReserva());
        btnEliminar.addActionListener(e -> eliminarReserva());
        btnLimpiar.addActionListener(e -> limpiarCamposReserva());
        btnRefrescar.addActionListener(e -> actualizarCombosReserva());

        return panelPrincipal;
    }

    // --- MÉTODOS CLIENTES ---
    private void cargarDatosClientes() {
        modeloClientes.setRowCount(0);
        List<Cliente> lista = servicioCliente.listarClientes();
        for (Cliente c : lista) {
            String tipoTxt = (c.getTipo() == 2) ? "Premium" : "Normal";
            modeloClientes.addRow(new Object[]{c.getDni(), c.getNombres(), c.getApellidos(), c.getCelular(), c.getEmail(), tipoTxt});
        }
    }

    private void guardarCliente() {
        String dni = txtCliDni.getText().trim();
        String celular = txtCliCelular.getText().trim();
        
        if (dni.isEmpty() || txtCliNombres.getText().trim().isEmpty() || 
            txtCliApellidos.getText().trim().isEmpty() || celular.isEmpty() || 
            txtCliEmail.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ningún campo puede estar en blanco.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!dni.matches("\\d{8}")) {
            JOptionPane.showMessageDialog(this, "El DNI debe tener exactamente 8 dígitos numéricos.", "Error de Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!celular.matches("9\\d{8}")) {
            JOptionPane.showMessageDialog(this, "El celular debe tener 9 dígitos y empezar obligatoriamente con 9.", "Error de Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int tipo = cmbCliTipo.getSelectedIndex() == 1 ? 2 : 1;
        Cliente c = new Cliente(dni, txtCliNombres.getText().trim(), txtCliApellidos.getText().trim(), celular, txtCliEmail.getText().trim(), tipo);
        
        if (servicioCliente.guardarCliente(c)) {
            JOptionPane.showMessageDialog(this, "Cliente guardado exitosamente.");
            cargarDatosClientes();
            actualizarCombosReserva();
            limpiarCamposCliente();
        } else {
            JOptionPane.showMessageDialog(this, "El DNI ya se encuentra registrado.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void modificarCliente() {
        if (txtCliDni.isEnabled()) {
            JOptionPane.showMessageDialog(this, "Seleccione un cliente de la tabla.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String celular = txtCliCelular.getText().trim();
        if (txtCliNombres.getText().trim().isEmpty() || txtCliApellidos.getText().trim().isEmpty() || 
            celular.isEmpty() || txtCliEmail.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ningún campo puede quedar en blanco al modificar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!celular.matches("9\\d{8}")) {
            JOptionPane.showMessageDialog(this, "El celular debe tener 9 dígitos y empezar obligatoriamente con 9.", "Error de Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int tipo = cmbCliTipo.getSelectedIndex() == 1 ? 2 : 1;
        Cliente c = new Cliente(txtCliDni.getText().trim(), txtCliNombres.getText().trim(), txtCliApellidos.getText().trim(), celular, txtCliEmail.getText().trim(), tipo);
        
        if (servicioCliente.modificarCliente(c)) {
            JOptionPane.showMessageDialog(this, "Cliente modificado correctamente.");
            cargarDatosClientes();
            actualizarCombosReserva();
            limpiarCamposCliente();
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo modificar.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarCliente() {
        int fila = tablaClientes.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un cliente de la tabla.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String dni = modeloClientes.getValueAt(fila, 0).toString();
        if (servicioCliente.eliminarCliente(dni)) {
            JOptionPane.showMessageDialog(this, "Cliente eliminado.");
            cargarDatosClientes();
            actualizarCombosReserva();
            limpiarCamposCliente();
        }
    }

    private void limpiarCamposCliente() {
        txtCliDni.setText("");
        txtCliDni.setEnabled(true);
        txtCliNombres.setText("");
        txtCliApellidos.setText("");
        txtCliCelular.setText("");
        txtCliEmail.setText("");
        cmbCliTipo.setSelectedIndex(0);
        tablaClientes.clearSelection();
    }

    // --- MÉTODOS HABITACIONES ---
    private void cargarDatosHabitaciones() {
        modeloHabitaciones.setRowCount(0);
        List<Habitacion> lista = servicioHabitacion.listarHabitaciones();
        for (Habitacion h : lista) {
            modeloHabitaciones.addRow(new Object[]{h.getIdHabitacion(), h.getIdPiso(), h.getNumero(), h.getTipoTexto()});
        }
    }
    
    private void guardarHabitacion() {
        String id = txtHabId.getText().trim();
        String piso = txtHabPiso.getText().trim();
        String numero = txtHabNumero.getText().trim();

        if (id.isEmpty() || piso.isEmpty() || numero.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ningún campo puede estar en blanco.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!esNumerico(id) || !esNumerico(piso) || !esNumerico(numero)) {
            JOptionPane.showMessageDialog(this, "El ID, ID Piso y Número de habitación deben ser únicamente números.", "Error de Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int tipo = cmbHabTipo.getSelectedIndex() + 1;
        Habitacion h = new Habitacion(id, piso, numero, tipo);

        if (servicioHabitacion.guardarHabitacion(h)) {
            JOptionPane.showMessageDialog(this, "Habitación registrada.");
            cargarDatosHabitaciones();
            actualizarCombosReserva();
            limpiarCamposHabitacion();
        } else {
            JOptionPane.showMessageDialog(this, "El ID de la habitación ya existe.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void modificarHabitacion() {
        if (txtHabId.isEnabled()) {
            JOptionPane.showMessageDialog(this, "Seleccione una habitación de la tabla.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String piso = txtHabPiso.getText().trim();
        String numero = txtHabNumero.getText().trim();

        if (piso.isEmpty() || numero.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ningún campo puede quedar en blanco al modificar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!esNumerico(piso) || !esNumerico(numero)) {
            JOptionPane.showMessageDialog(this, "El ID Piso y Número de habitación deben ser únicamente números.", "Error de Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int tipo = cmbHabTipo.getSelectedIndex() + 1;
        Habitacion h = new Habitacion(txtHabId.getText().trim(), piso, numero, tipo);

        if (servicioHabitacion.modificarHabitacion(h)) {
            JOptionPane.showMessageDialog(this, "Habitación modificada correctamente.");
            cargarDatosHabitaciones();
            actualizarCombosReserva();
            limpiarCamposHabitacion();
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo modificar la habitación.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarHabitacion() {
        int fila = tablaHabitaciones.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una habitación de la tabla.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String id = modeloHabitaciones.getValueAt(fila, 0).toString();
        if (servicioHabitacion.eliminarHabitacion(id)) {
            JOptionPane.showMessageDialog(this, "Habitación eliminada.");
            cargarDatosHabitaciones();
            actualizarCombosReserva();
            limpiarCamposHabitacion();
        }
    }

    private void limpiarCamposHabitacion() {
        txtHabId.setText("");
        txtHabId.setEnabled(true);
        txtHabPiso.setText("");
        txtHabNumero.setText("");
        cmbHabTipo.setSelectedIndex(0);
        tablaHabitaciones.clearSelection();
    }

    // --- MÉTODOS RESERVAS ---

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
            listModel.addElement(h.getIdHabitacion() + " (Hab. " + h.getNumero() + " - " + obtenerTextoTipoHabitacion(h.getTipo()) + ")");
        }
        listHabitacionesMultiples.setModel(listModel);
    }

    
    
    private boolean validarDisponibilidadHabitaciones(List<String> idsHabitaciones, LocalDate nuevaInicio, LocalDate nuevaSalida, String idReservaExcluida) {
        List<Reserva> reservas = servicioReserva.listarReservas();
        List<DetalleReserva> detalles = servicioDetalleReserva.listarDetalles();
        List<Habitacion> habitaciones = servicioHabitacion.listarHabitaciones();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (Reserva r : reservas) {
            if (idReservaExcluida != null && r.getIdReserva().equalsIgnoreCase(idReservaExcluida)) {
                continue;
            }

            try {
                LocalDate rInicio = LocalDate.parse(r.getFechaInicio(), formatter);
                LocalDate rSalida = LocalDate.parse(r.getFechaSalida(), formatter);

                boolean hayCruceFechas = nuevaInicio.isBefore(rSalida) && nuevaSalida.isAfter(rInicio);

                if (hayCruceFechas) {
                    for (DetalleReserva d : detalles) {
                        if (d.getIdReserva().equalsIgnoreCase(r.getIdReserva())) {
                            for (String idHabSel : idsHabitaciones) {
                                if (d.getIdHabitacion().equalsIgnoreCase(idHabSel)) {
                                    String numeroHabitacion = idHabSel;
                                    for (Habitacion hab : habitaciones) {
                                        if (hab.getIdHabitacion().equalsIgnoreCase(idHabSel)) {
                                            numeroHabitacion = hab.getNumero();
                                            break;
                                        }
                                    }

                                    JOptionPane.showMessageDialog(this,
                                        "La habitación " + numeroHabitacion + " ya se encuentra reservada del " + r.getFechaInicio() + " al " + r.getFechaSalida() + ".",
                                        "Conflicto de Disponibilidad", JOptionPane.WARNING_MESSAGE);
                                    return false;
                                }
                            }
                        }
                    }
                }
            } catch (Exception ignored) {}
        }
        return true;
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
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);
            LocalDate fInicio = LocalDate.parse(fechaInicioStr, formatter);

            LocalDate hoy = LocalDate.now();
            if (fInicio.isBefore(hoy)) {
                JOptionPane.showMessageDialog(this, "No se pueden registrar reservas en fechas anteriores al día de hoy (" + hoy.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + ").", "Error de Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int dias = Integer.parseInt(diasStr);
            if (dias <= 0) {
                JOptionPane.showMessageDialog(this, "Los días de estadía deben ser mayores a 0.", "Error de Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }
            LocalDate fSalida = fInicio.plusDays(dias);
            String fechaSalidaStr = fSalida.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

            List<String> idsHabitaciones = seleccionadas.stream().map(s -> s.split(" ")[0]).collect(Collectors.toList());

            if (!validarDisponibilidadHabitaciones(idsHabitaciones, fInicio, fSalida, null)) {
                return;
            }

            double montoTotal = 0.0;
            List<Habitacion> todasHabitaciones = servicioHabitacion.listarHabitaciones();

            for (String idHab : idsHabitaciones) {
                for (Habitacion h : todasHabitaciones) {
                    if (h.getIdHabitacion().equalsIgnoreCase(idHab)) {
                        montoTotal += h.getPrecioNoche() * dias;
                        break;
                    }
                }
            }

            String dniCliente = cmbResCliente.getSelectedItem().toString().split(" - ")[0];

            boolean esPremium = false;
            for (Cliente c : servicioCliente.listarClientes()) {
                if (c.getDni().equalsIgnoreCase(dniCliente) && c.getTipo() == 2) {
                    esPremium = true;
                    break;
                }
            }

            if (esPremium) {
                montoTotal = montoTotal * 0.90;
            }

            int metodoPago = cmbResMetodoPago.getSelectedIndex() + 1;

            Reserva r = new Reserva(idRes, fechaInicioStr, dias, fechaSalidaStr, dniCliente, montoTotal, metodoPago);

            if (servicioReserva.guardarReserva(r)) {
                int contador = 1;
                for (String idHab : idsHabitaciones) {
                    DetalleReserva d = new DetalleReserva(idRes + "-" + contador++, idRes, idHab);
                    servicioDetalleReserva.guardarDetalle(d);
                }

                String mensajeExito = "Reserva guardada con éxito.\nMonto Total: S/ " + String.format("%.2f", montoTotal) + "\nFecha Salida: " + fechaSalidaStr;
                if (esPremium) {
                    mensajeExito += "\n(¡Se aplicó 10% de descuento por Cliente Premium!)";
                }

                JOptionPane.showMessageDialog(this, mensajeExito);
                cargarDatosReservas();
                limpiarCamposReserva();
            } else {
                JOptionPane.showMessageDialog(this, "El ID de la reserva ya existe.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Fecha inválida (verifique que el día exista en el mes). Use formato DD/MM/AAAA", "Error de Validación", JOptionPane.WARNING_MESSAGE);
        }
    }



    private void modificarReserva() {
        if (txtResId.isEnabled()) {
            JOptionPane.showMessageDialog(this, "Seleccione una reserva de la tabla para modificar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String idRes = txtResId.getText().trim();
        String fechaInicioStr = txtResFechaInicio.getText().trim();
        String diasStr = txtResDias.getText().trim();
        List<String> seleccionadas = listHabitacionesMultiples.getSelectedValuesList();

        if (fechaInicioStr.isEmpty() || diasStr.isEmpty() || seleccionadas.isEmpty() || !esNumerico(diasStr)) {
            JOptionPane.showMessageDialog(this, "Complete todos los campos y seleccione al menos una habitación.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);
            LocalDate fInicio = LocalDate.parse(fechaInicioStr, formatter);

            LocalDate hoy = LocalDate.now();
            if (fInicio.isBefore(hoy)) {
                JOptionPane.showMessageDialog(this, "No se pueden modificar reservas a fechas anteriores al día de hoy.", "Error de Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int dias = Integer.parseInt(diasStr);
            if (dias <= 0) {
                JOptionPane.showMessageDialog(this, "Los días de estadía deben ser mayores a 0.", "Error de Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }
            LocalDate fSalida = fInicio.plusDays(dias);
            String fechaSalidaStr = fSalida.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

            List<String> idsHabitaciones = seleccionadas.stream().map(s -> s.split(" ")[0]).collect(Collectors.toList());

            if (!validarDisponibilidadHabitaciones(idsHabitaciones, fInicio, fSalida, idRes)) {
                return;
            }

            double montoTotal = 0.0;
            List<Habitacion> todasHabitaciones = servicioHabitacion.listarHabitaciones();

            for (String idHab : idsHabitaciones) {
                for (Habitacion h : todasHabitaciones) {
                    if (h.getIdHabitacion().equalsIgnoreCase(idHab)) {
                        montoTotal += h.getPrecioNoche() * dias;
                        break;
                    }
                }
            }

            String dniCliente = cmbResCliente.getSelectedItem().toString().split(" - ")[0];

            boolean esPremium = false;
            for (Cliente c : servicioCliente.listarClientes()) {
                if (c.getDni().equalsIgnoreCase(dniCliente) && c.getTipo() == 2) {
                    esPremium = true;
                    break;
                }
            }

            if (esPremium) {
                montoTotal = montoTotal * 0.90;
            }

            int metodoPago = cmbResMetodoPago.getSelectedIndex() + 1;

            Reserva r = new Reserva(idRes, fechaInicioStr, dias, fechaSalidaStr, dniCliente, montoTotal, metodoPago);

            if (servicioReserva.modificarReserva(r)) {
                servicioDetalleReserva.eliminarDetallePorReserva(idRes);
                int contador = 1;
                for (String idHab : idsHabitaciones) {
                    DetalleReserva d = new DetalleReserva(idRes + "-" + contador++, idRes, idHab);
                    servicioDetalleReserva.guardarDetalle(d);
                }

                String mensajeExito = "Reserva modificada con éxito.\nMonto Total: S/ " + String.format("%.2f", montoTotal) + "\nFecha Salida: " + fechaSalidaStr;
                if (esPremium) {
                    mensajeExito += "\n(¡Se aplicó 10% de descuento por Cliente Premium!)";
                }

                JOptionPane.showMessageDialog(this, mensajeExito);
                cargarDatosReservas();
                limpiarCamposReserva();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo modificar la reserva.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Fecha inválida (verifique que el día exista en el mes). Use formato DD/MM/AAAA", "Error de Validación", JOptionPane.WARNING_MESSAGE);
        }
    }



    private void eliminarReserva() {
        int fila = tablaReservas.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una reserva de la tabla para eliminar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String idRes = modeloReservas.getValueAt(fila, 0).toString();
        if (servicioReserva.eliminarReserva(idRes)) {
            servicioDetalleReserva.eliminarDetallePorReserva(idRes);
            JOptionPane.showMessageDialog(this, "Reserva eliminada.");
            cargarDatosReservas();
            limpiarCamposReserva();
        }
    }


    private void limpiarCamposReserva() {
        txtResId.setText("");
        txtResId.setEnabled(true);
        txtResFechaInicio.setText("");
        txtResDias.setText("");
        listHabitacionesMultiples.clearSelection();
        if (cmbResCliente.getItemCount() > 0) cmbResCliente.setSelectedIndex(0);
        cmbResMetodoPago.setSelectedIndex(0);
        tablaReservas.clearSelection();
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new VentanaCliente().setVisible(true));
    }
}