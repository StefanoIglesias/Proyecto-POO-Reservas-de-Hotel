package vista;

import modelo.*;
import servicio.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class VentanaCliente extends JFrame {
    private ServicioPersonaHabitacion servicioPersonaHabitacion;
    private ServicioReservaDetalle servicioReservaDetalle;

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
    private JTextField txtResId, txtResFecha, txtResMonto;
    private JComboBox<String> cmbResCliente, cmbResHabitacion, cmbResMetodoPago;
    private JTable tablaReservas;
    private DefaultTableModel modeloReservas;

    public VentanaCliente() {
        servicioPersonaHabitacion = new ServicioPersonaHabitacion();
        servicioReservaDetalle = new ServicioReservaDetalle();
        
        setTitle("Sistema de Gestión de Reservas de Hotel");
        setSize(900, 680);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane pestanas = new JTabbedPane();
        pestanas.addTab("Gestión de Clientes", crearPanelClientes());
        
        add(pestanas);
        
        cargarDatosClientes();
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

        pForm.add(new JLabel("Celular (9 dígitos):"));
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

    private void cargarDatosClientes() {
        modeloClientes.setRowCount(0);
        List<Cliente> lista = servicioPersonaHabitacion.listarClientes();
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
        
        if (servicioPersonaHabitacion.guardarCliente(c)) {
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
        
        if (servicioPersonaHabitacion.modificarCliente(c)) {
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
        if (servicioPersonaHabitacion.eliminarCliente(dni)) {
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
    
    private void actualizarCombosReserva() {
        cmbResCliente.removeAllItems();
        List<Cliente> clientes = servicioPersonaHabitacion.listarClientes();
        for (Cliente c : clientes) {
            cmbResCliente.addItem(c.getDni() + " - " + c.getNombres() + " " + c.getApellidos());
    }

    cmbResHabitacion.removeAllItems();
    List<Habitacion> habitaciones = servicioPersonaHabitacion.listarHabitaciones();
    for (Habitacion h : habitaciones) {
        cmbResHabitacion.addItem(h.getIdHabitacion() + " (Hab. " + h.getNumero() + ")");
    }
}
    

    

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new VentanaCliente().setVisible(true));
    }
}