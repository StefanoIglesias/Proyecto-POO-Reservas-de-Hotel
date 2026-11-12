package servicio;

import modelo.Reserva;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ServicioReserva {
    private final String ARCHIVO_RESERVAS = "reservas.txt";

    public List<Reserva> listarReservas() {
        List<Reserva> lista = new ArrayList<>();
        File f = new File(ARCHIVO_RESERVAS);
        if (!f.exists()) return lista;

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] p = linea.split(";");
                if (p.length == 7) {
                    lista.add(new Reserva(p[0], p[1], Integer.parseInt(p[2]), p[3], p[4], Double.parseDouble(p[5]), Integer.parseInt(p[6])));
                }
            }
        } catch (IOException e) {
            System.err.println("Error al leer reservas: " + e.getMessage());
        }
        return lista;
    }

    public boolean guardarReserva(Reserva r) {
        List<Reserva> lista = listarReservas();
        for (Reserva res : lista) {
            if (res.getIdReserva().equalsIgnoreCase(r.getIdReserva())) return false;
        }
        try (PrintWriter pw = new PrintWriter(new FileWriter(ARCHIVO_RESERVAS, true))) {
            pw.println(r.toLineaTexto());
            return true;
        } catch (IOException e) {
            System.err.println("Error al guardar reserva: " + e.getMessage());
            return false;
        }
    }

    public boolean modificarReserva(Reserva r) {
        List<Reserva> lista = listarReservas();
        boolean modificado = false;
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getIdReserva().equalsIgnoreCase(r.getIdReserva())) {
                lista.set(i, r);
                modificado = true;
                break;
            }
        }
        if (modificado) reescribirArchivo(lista);
        return modificado;
    }

    public boolean eliminarReserva(String idReserva) {
        List<Reserva> lista = listarReservas();
        boolean eliminado = lista.removeIf(r -> r.getIdReserva().equalsIgnoreCase(idReserva));
        if (eliminado) reescribirArchivo(lista);
        return eliminado;
    }

    private void reescribirArchivo(List<Reserva> lista) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ARCHIVO_RESERVAS, false))) {
            for (Reserva r : lista) {
                pw.println(r.toLineaTexto());
            }
        } catch (IOException e) {
            System.err.println("Error al actualizar reservas: " + e.getMessage());
        }
    }
}