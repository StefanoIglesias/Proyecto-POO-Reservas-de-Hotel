package servicio;

import modelo.Cliente;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ServicioPersonaHabitacion {
    private final String ARCHIVO_CLIENTES = "clientes.txt";

    // --- MÓDULO CLIENTES ---
    public List<Cliente> listarClientes() {
        List<Cliente> lista = new ArrayList<>();
        File f = new File(ARCHIVO_CLIENTES);
        if (!f.exists()) return lista;

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] p = linea.split(";");
                if (p.length == 6) {
                    lista.add(new Cliente(p[0], p[1], p[2], p[3], p[4], Integer.parseInt(p[5])));
                }
            }
        } catch (IOException e) {
            System.err.println("Error al leer clientes: " + e.getMessage());
        }
        return lista;
    }

    public boolean guardarCliente(Cliente c) {
        List<Cliente> lista = listarClientes();
        for (Cliente cli : lista) {
            if (cli.getDni().equalsIgnoreCase(c.getDni())) return false;
        }
        try (PrintWriter pw = new PrintWriter(new FileWriter(ARCHIVO_CLIENTES, true))) {
            pw.println(c.toLineaTexto());
            return true;
        } catch (IOException e) {
            System.err.println("Error al guardar cliente: " + e.getMessage());
            return false;
        }
    }

    // Buscar cliente por DNI
    public Cliente buscarCliente(String dni) {
        List<Cliente> clientes = listarClientes();
        for (Cliente c : clientes) {
            if (c.getDni().equalsIgnoreCase(dni)) {
                return c;
            }
        }
        return null;
    }

    // Modificar cliente existente según su DNI
    public boolean modificarCliente(Cliente clienteModificado) {
        List<Cliente> clientes = listarClientes();
        boolean encontrado = false;

        for (int i = 0; i < clientes.size(); i++) {
            if (clientes.get(i).getDni().equalsIgnoreCase(clienteModificado.getDni())) {
                clientes.set(i, clienteModificado);
                encontrado = true;
                break;
            }
        }

        if (encontrado) {
            reescribirArchivo(clientes);
            return true;
        }
        return false;
    }

    // Eliminar cliente por DNI
    public boolean eliminar(String dni) {
        List<Cliente> clientes = listarClientes();
        boolean eliminado = clientes.removeIf(c -> c.getDni().equalsIgnoreCase(dni));

        if (eliminado) {
            reescribirArchivo(clientes);
            return true;
        }
        return false;
    }

    // Método auxiliar para sobreescribir el archivo completo (usado en modificar y eliminar)
    private void reescribirArchivo(List<Cliente> clientes) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ARCHIVO_CLIENTES, false))) {
            for (Cliente c : clientes) {
                pw.println(c.toLineaTexto());
            }
        } catch (IOException e) {
            System.err.println("Error al actualizar el archivo de clientes: " + e.getMessage());
        }
    }
}