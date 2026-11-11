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
}
