package servicio;

import modelo.DetalleReserva;
import modelo.Reserva;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ServicioReservaDetalle {
    private final String ARCHIVO_RESERVAS = "reservas.txt";
    private final String ARCHIVO_DETALLES = "detalles_reserva.txt";

    public List<Reserva> listarReservas() {
        List<Reserva> lista = new ArrayList<>();
        File f = new File(ARCHIVO_RESERVAS);
        if (!f.exists()) return lista;

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] p = linea.split(";");
                if (p.length == 5) {
                    lista.add(new Reserva(p[0], p[1], p[2], Double.parseDouble(p[3]), Integer.parseInt(p[4])));
                }
            }
        } catch (IOException e) {
            System.err.println("Error al leer reservas: " + e.getMessage());
        }
        return lista;
    }
    
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

    public boolean registrarReservaCompleta(Reserva r, DetalleReserva d) {
        List<Reserva> reservas = listarReservas();
        for (Reserva res : reservas) {
            if (res.getIdReserva().equalsIgnoreCase(r.getIdReserva())) return false;
        }

        try (PrintWriter pwR = new PrintWriter(new FileWriter(ARCHIVO_RESERVAS, true));
             PrintWriter pwD = new PrintWriter(new FileWriter(ARCHIVO_DETALLES, true))) {
            
            pwR.println(r.toLineaTexto());
            pwD.println(d.toLineaTexto());
            return true;
        } catch (IOException e) {
            System.err.println("Error al guardar reserva/detalle: " + e.getMessage());
            return false;
        }
    }


    public boolean modificarReservaCompleta(Reserva r, DetalleReserva d) {
        List<Reserva> reservas = listarReservas();
        List<DetalleReserva> detalles = listarDetalles();
        boolean encontrado = false;

        for (int i = 0; i < reservas.size(); i++) {
            if (reservas.get(i).getIdReserva().equalsIgnoreCase(r.getIdReserva())) {
                reservas.set(i, r);
                encontrado = true;
                break;
            }
        }

        if (encontrado) {
            for (int i = 0; i < detalles.size(); i++) {
                if (detalles.get(i).getIdReserva().equalsIgnoreCase(r.getIdReserva())) {
                    detalles.set(i, d);
                    break;
                }
            }
            reescribirArchivos(reservas, detalles);
            return true;
        }
        return false;
    }


    public boolean eliminarReserva(String idReserva) {
        List<Reserva> reservas = listarReservas();
        List<DetalleReserva> detalles = listarDetalles();

        boolean eliminadoRes = reservas.removeIf(r -> r.getIdReserva().equalsIgnoreCase(idReserva));
        detalles.removeIf(d -> d.getIdReserva().equalsIgnoreCase(idReserva));

        if (eliminadoRes) {
            reescribirArchivos(reservas, detalles);
            return true;
        }
        return false;
    }


    private void reescribirArchivos(List<Reserva> reservas, List<DetalleReserva> detalles) {
        try (PrintWriter pwR = new PrintWriter(new FileWriter(ARCHIVO_RESERVAS, false))) {
            for (Reserva r : reservas) {
                pwR.println(r.toLineaTexto());
            }
        } catch (IOException e) {
            System.err.println("Error al actualizar reservas: " + e.getMessage());
        }

        try (PrintWriter pwD = new PrintWriter(new FileWriter(ARCHIVO_DETALLES, false))) {
            for (DetalleReserva d : detalles) {
                pwD.println(d.toLineaTexto());
            }
        } catch (IOException e) {
            System.err.println("Error al actualizar detalles: " + e.getMessage());
        }
    }
}
