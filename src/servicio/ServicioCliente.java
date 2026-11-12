package servicio;

import modelo.Cliente;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ServicioCliente {
    private final String ARCHIVO_CLIENTES = "clientes.txt";

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

    public boolean modificarCliente(Cliente c) {
        List<Cliente> lista = listarClientes();
        boolean modificado = false;
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getDni().equalsIgnoreCase(c.getDni())) {
                lista.set(i, c);
                modificado = true;
                break;
            }
        }
        if (modificado) {
            reescribirArchivo(lista);
        }
        return modificado;
    }

    public boolean eliminarCliente(String dni) {
        List<Cliente> lista = listarClientes();
        boolean eliminado = lista.removeIf(c -> c.getDni().equalsIgnoreCase(dni));
        if (eliminado) {
            reescribirArchivo(lista);
        }
        return eliminado;
    }

    private void reescribirArchivo(List<Cliente> lista) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ARCHIVO_CLIENTES, false))) {
            for (Cliente c : lista) {
                pw.println(c.toLineaTexto());
            }
        } catch (IOException e) {
            System.err.println("Error al actualizar archivo de clientes: " + e.getMessage());
        }
    }
}