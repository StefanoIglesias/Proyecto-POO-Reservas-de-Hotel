package servicio;

import modelo.Cliente;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ServicioPersonaHabitacion {
    private final String RUTA_ARCHIVO = "clientes.txt";

    // Listar todos los clientes del archivo
    public List<Cliente> listar() {
        List<Cliente> lista = new ArrayList<>();
        File archivo = new File(RUTA_ARCHIVO);
        
        if (!archivo.exists()) {
            return lista; // Si no existe aún, retorna lista vacía
        }

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] partes = linea.split(";");
                if (partes.length == 6) {
                    Cliente cliente = new Cliente(
                        partes[0], 
                        partes[1], 
                        partes[2], 
                        partes[3], 
                        partes[4], 
                        Integer.parseInt(partes[5])
                    );
                    lista.add(cliente);
                }
            }
        } catch (IOException e) {
            System.err.println("Error al leer el archivo de clientes: " + e.getMessage());
        }
        return lista;
    }

    // Guardar un nuevo cliente (Valida DNI único)
    public boolean guardar(Cliente nuevoCliente) {
        List<Cliente> clientes = listar();
        
        // Validar si el DNI ya existe
        for (Cliente c : clientes) {
            if (c.getDni().equalsIgnoreCase(nuevoCliente.getDni())) {
                return false; // DNI duplicado
            }
        }

        try (PrintWriter pw = new PrintWriter(new FileWriter(RUTA_ARCHIVO, true))) {
            pw.println(nuevoCliente.toLineaTexto());
            return true;
        } catch (IOException e) {
            System.err.println("Error al escribir en el archivo: " + e.getMessage());
            return false;
        }
    }

    // Buscar cliente por DNI
    public Cliente buscar(String dni) {
        List<Cliente> clientes = listar();
        for (Cliente c : clientes) {
            if (c.getDni().equalsIgnoreCase(dni)) {
                return c;
            }
        }
        return null;
    }

    // Modificar cliente existente según su DNI
    public boolean modificar(Cliente clienteModificado) {
        List<Cliente> clientes = listar();
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
        List<Cliente> clientes = listar();
        boolean eliminado = clientes.removeIf(c -> c.getDni().equalsIgnoreCase(dni));

        if (eliminado) {
            reescribirArchivo(clientes);
            return true;
        }
        return false;
    }

    // Método auxiliar para sobreescribir el archivo completo (usado en modificar y eliminar)
    private void reescribirArchivo(List<Cliente> clientes) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(RUTA_ARCHIVO, false))) {
            for (Cliente c : clientes) {
                pw.println(c.toLineaTexto());
            }
        } catch (IOException e) {
            System.err.println("Error al actualizar el archivo de clientes: " + e.getMessage());
        }
    }
}