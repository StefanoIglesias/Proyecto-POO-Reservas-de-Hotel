package servicio;

import modelo.DetalleReserva;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ServicioDetalleReserva {
    private final String ARCHIVO_DETALLES = "detalles_reserva.txt";

    public List<DetalleReserva> listarDetalles() {
        List<DetalleReserva> lista = new ArrayList<>();
        File f = new File(ARCHIVO_DETALLES);
        if (!f.exists()) return lista;

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] p = linea.split(";");
                if (p.length == 3) {
                    lista.add(new DetalleReserva(p[0], p[1], p[2]));
                }
            }
        } catch (IOException e) {
            System.err.println("Error al leer detalles de reserva: " + e.getMessage());
        }
        return lista;
    }

    public boolean guardarDetalle(DetalleReserva d) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ARCHIVO_DETALLES, true))) {
            pw.println(d.toLineaTexto());
            return true;
        } catch (IOException e) {
            System.err.println("Error al guardar detalle: " + e.getMessage());
            return false;
        }
    }

    public boolean modificarDetallePorReserva(String idReserva, String nuevoIdHabitacion) {
        List<DetalleReserva> lista = listarDetalles();
        boolean modificado = false;
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getIdReserva().equalsIgnoreCase(idReserva)) {
                DetalleReserva dActualizado = new DetalleReserva(lista.get(i).getIdDetalle(), idReserva, nuevoIdHabitacion);
                lista.set(i, dActualizado);
                modificado = true;
                break;
            }
        }
        if (modificado) {
            reescribirArchivo(lista);
        }
        return modificado;
    }

    public boolean eliminarDetallePorReserva(String idReserva) {
        List<DetalleReserva> lista = listarDetalles();
        boolean eliminado = lista.removeIf(d -> d.getIdReserva().equalsIgnoreCase(idReserva));
        if (eliminado) {
            reescribirArchivo(lista);
        }
        return eliminado;
    }

    private void reescribirArchivo(List<DetalleReserva> lista) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ARCHIVO_DETALLES, false))) {
            for (DetalleReserva d : lista) {
                pw.println(d.toLineaTexto());
            }
        } catch (IOException e) {
            System.err.println("Error al actualizar archivo de detalles: " + e.getMessage());
        }
    }
}