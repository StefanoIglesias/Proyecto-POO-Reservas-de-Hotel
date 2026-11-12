package servicio;

import modelo.Habitacion;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ServicioHabitacion {
    private final String ARCHIVO_HABITACIONES = "habitaciones.txt";

    public List<Habitacion> listarHabitaciones() {
        List<Habitacion> lista = new ArrayList<>();
        File f = new File(ARCHIVO_HABITACIONES);
        if (!f.exists()) return lista;

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] p = linea.split(";");
                if (p.length == 4) {
                    lista.add(new Habitacion(p[0], p[1], p[2], Integer.parseInt(p[3])));
                }
            }
        } catch (IOException e) {
            System.err.println("Error al leer habitaciones: " + e.getMessage());
        }
        return lista;
    }

    public boolean guardarHabitacion(Habitacion h) {
        List<Habitacion> lista = listarHabitaciones();
        for (Habitacion hab : lista) {
            if (hab.getIdHabitacion().equalsIgnoreCase(h.getIdHabitacion())) return false;
        }
        try (PrintWriter pw = new PrintWriter(new FileWriter(ARCHIVO_HABITACIONES, true))) {
            pw.println(h.toLineaTexto());
            return true;
        } catch (IOException e) {
            System.err.println("Error al guardar habitación: " + e.getMessage());
            return false;
        }
    }

    public boolean modificarHabitacion(Habitacion h) {
        List<Habitacion> lista = listarHabitaciones();
        boolean modificado = false;
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getIdHabitacion().equalsIgnoreCase(h.getIdHabitacion())) {
                lista.set(i, h);
                modificado = true;
                break;
            }
        }
        if (modificado) {
            reescribirArchivo(lista);
        }
        return modificado;
    }

    public boolean eliminarHabitacion(String idHabitacion) {
        List<Habitacion> lista = listarHabitaciones();
        boolean eliminado = lista.removeIf(h -> h.getIdHabitacion().equalsIgnoreCase(idHabitacion));
        if (eliminado) {
            reescribirArchivo(lista);
        }
        return eliminado;
    }

    private void reescribirArchivo(List<Habitacion> lista) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ARCHIVO_HABITACIONES, false))) {
            for (Habitacion h : lista) {
                pw.println(h.toLineaTexto());
            }
        } catch (IOException e) {
            System.err.println("Error al actualizar archivo de habitaciones: " + e.getMessage());
        }
    }
}