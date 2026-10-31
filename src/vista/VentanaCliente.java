package vista;

import modelo.Cliente;
import servicio.ServicioCliente;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class VentanaCliente extends JFrame {
    private JTextField txtDni, txtNombres, txtApellidos, txtCelular, txtEmail;
    private JComboBox<String> cmbTipo;
    private JTable tablaClientes;
    private DefaultTableModel modeloTabla;
    private ServicioCliente servicio;

    public VentanaCliente() {
        servicio = new ServicioCliente();
        inicializarComponentes();
        cargarDatosTabla();
        mostrarResumenConsola();
    }

    private void inicializarComponentes() {
        setTitle("Gestión de Clientes");
        setSize(750, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Panel Superior (Formulario)
        JPanel panelFormulario = new JPanel(new GridLayout(6, 2, 5, 5));
        panelFormulario.setBorder(BorderFactory.createTitledBorder("Datos del Cliente"));

        panelFormulario.add(new JLabel("DNI:"));
        txtDni = new JTextField();
        panelFormulario.add(txtDni);

        panelFormulario.add(new JLabel("Nombres:"));
        txtNombres = new JTextField();
        panelFormulario.add(txtNombres);

        panelFormulario.add(new JLabel("Apellidos:"));
        txtApellidos = new JTextField();
        panelFormulario.add(txtApellidos);

        panelFormulario.add(new JLabel("Celular:"));
        txtCelular = new JTextField();
        panelFormulario.add(txtCelular);

        panelFormulario.add(new JLabel("Email:"));
        txtEmail = new JTextField();
        panelFormulario.add(txtEmail);

        panelFormulario.add(new JLabel("Tipo de Cliente:"));
        cmbTipo = new JComboBox<>(new String[]{"Normal", "Premium"});
        panelFormulario.add(cmbTipo);

        add(panelFormulario, BorderLayout.NORTH);

        // Panel Central (Tabla)
        modeloTabla = new DefaultTableModel(new String[]{"DNI", "Nombres", "Apellidos", "Celular", "Email", "Tipo"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Hacer celdas no editables directamente
            }
        };
        tablaClientes = new JTable(modeloTabla);
        add(new JScrollPane(tablaClientes), BorderLayout.CENTER);

        // Panel Inferior (Botones)
        JPanel panelBotones = new JPanel();
        JButton btnGuardar = new JButton("Guardar");
        JButton btnModificar = new JButton("Modificar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnLimpiar = new JButton("Limpiar");

        panelBotones.add(btnGuardar);
        panelBotones.add(btnModificar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnLimpiar);

        add(panelBotones, BorderLayout.SOUTH);

        // Eventos de la Tabla para rellenar campos al hacer clic
        tablaClientes.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int fila = tablaClientes.getSelectedRow();
                if (fila != -1) {
                    txtDni.setText(modeloTabla.getValueAt(fila, 0).toString());
                    txtDni.setEnabled(false); // DNI único no editable al modificar
                    txtNombres.setText(modeloTabla.getValueAt(fila, 1).toString());
                    txtApellidos.setText(modeloTabla.getValueAt(fila, 2).toString());
                    txtCelular.setText(modeloTabla.getValueAt(fila, 3).toString());
                    txtEmail.setText(modeloTabla.getValueAt(fila, 4).toString());
                    
                    String tipoStr = modeloTabla.getValueAt(fila, 5).toString();
                    cmbTipo.setSelectedItem(tipoStr);
                }
            }
        });

        // Acciones de Botones
        btnGuardar.addActionListener(e -> guardarCliente());
        btnModificar.addActionListener(e -> modificarCliente());
        btnEliminar.addActionListener(e -> eliminarCliente());
        btnLimpiar.addActionListener(e -> limpiarCampos());
    }

    private void cargarDatosTabla() {
        modeloTabla.setRowCount(0); // Limpiar tabla
        List<Cliente> lista = servicio.listar();
        for (Cliente c : lista) {
            String tipoTexto = (c.getTipo() == 2) ? "Premium" : "Normal";
            modeloTabla.addRow(new Object[]{
                c.getDni(),
                c.getNombres(),
                c.getApellidos(),
                c.getCelular(),
                c.getEmail(),
                tipoTexto
            });
        }
    }

    private void guardarCliente() {
        if (validarCamposVacios()) return;

        String dni = txtDni.getText().trim();
        String nombres = txtNombres.getText().trim();
        String apellidos = txtApellidos.getText().trim();
        String celular = txtCelular.getText().trim();
        String email = txtEmail.getText().trim();
        int tipo = cmbTipo.getSelectedIndex() == 1 ? 2 : 1; // "Normal" = 1, "Premium" = 2

        Cliente cliente = new Cliente(dni, nombres, apellidos, celular, email, tipo);
        boolean exito = servicio.guardar(cliente);

        if (exito) {
            JOptionPane.showMessageDialog(this, "Cliente guardado exitosamente.");
            cargarDatosTabla();
            limpiarCampos();
        } else {
            JOptionPane.showMessageDialog(this, "Error: Ya existe un cliente registrado con el DNI " + dni, "Duplicado", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void modificarCliente() {
        if (txtDni.isEnabled()) {
            JOptionPane.showMessageDialog(this, "Seleccione un cliente de la tabla para modificar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String dni = txtDni.getText().trim();
        String nombres = txtNombres.getText().trim();
        String apellidos = txtApellidos.getText().trim();
        String celular = txtCelular.getText().trim();
        String email = txtEmail.getText().trim();
        int tipo = cmbTipo.getSelectedIndex() == 1 ? 2 : 1;

        Cliente cliente = new Cliente(dni, nombres, apellidos, celular, email, tipo);
        boolean exito = servicio.modificar(cliente);

        if (exito) {
            JOptionPane.showMessageDialog(this, "Cliente modificado correctamente.");
            cargarDatosTabla();
            limpiarCampos();
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo modificar el cliente.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarCliente() {
        int fila = tablaClientes.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un cliente de la tabla para eliminar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String dni = modeloTabla.getValueAt(fila, 0).toString();
        int confirm = JOptionPane.showConfirmDialog(this, "¿Está seguro de eliminar al cliente con DNI " + dni + "?", "Confirmar", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean exito = servicio.eliminar(dni);
            if (exito) {
                JOptionPane.showMessageDialog(this, "Cliente eliminado.");
                cargarDatosTabla();
                limpiarCampos();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo eliminar el registro.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void limpiarCampos() {
        txtDni.setText("");
        txtDni.setEnabled(true); // Habilitar nuevamente para nuevos registros
        txtNombres.setText("");
        txtApellidos.setText("");
        txtCelular.setText("");
        txtEmail.setText("");
        cmbTipo.setSelectedIndex(0);
        tablaClientes.clearSelection();
    }

    private boolean validarCamposVacios() {
        if (txtDni.getText().trim().isEmpty() || txtNombres.getText().trim().isEmpty() ||
            txtApellidos.getText().trim().isEmpty() || txtCelular.getText().trim().isEmpty() ||
            txtEmail.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return true;
        }
        return false;
    }

    private void mostrarResumenConsola() {
        System.out.println("==================================================");
        System.out.println("  RESUMEN DE COMPONENTES SWING UTILIZADOS EN VISTA  ");
        System.out.println("==================================================");
        System.out.println("- JFrame: Ventana principal de la aplicación.");
        System.out.println("- JPanel: Contenedores para organizar componentes (GridLayout y BorderLayout).");
        System.out.println("- JLabel: Etiquetas de texto descriptivas para el formulario.");
        System.out.println("- JTextField: Campos de texto para ingreso de DNI, Nombres, Apellidos, Celular y Email.");
        System.out.println("- JComboBox: Selector desplegable para el tipo de cliente ('Normal' / 'Premium').");
        System.out.println("- JTable & DefaultTableModel: Grilla para listar y visualizar los clientes registrados.");
        System.out.println("- JScrollPane: Panel con barras de desplazamiento para la tabla.");
        System.out.println("- JButton: Botones de acción (Guardar, Modificar, Eliminar, Limpiar).");
        System.out.println("- JOptionPane: Cuadros de diálogo para mensajes informativos y de advertencia.");
        System.out.println("==================================================");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new VentanaCliente().setVisible(true);
        });
    }
}