package modelo;

public class Reserva {
    private String idReserva;
    private String fecha;
    private String dniCliente;
    private double monto;
    private int metodoPago; // 1: Yape, 2: Plin, 3: Transferencia, 4: Efectivo

    public Reserva(String idReserva, String fecha, String dniCliente, double monto, int metodoPago) {
        this.idReserva = idReserva;
        this.fecha = fecha;
        this.dniCliente = dniCliente;
        this.monto = monto;
        this.metodoPago = metodoPago;
    }

    public String getIdReserva() { return idReserva; }
    public void setIdReserva(String idReserva) { this.idReserva = idReserva; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public String getDniCliente() { return dniCliente; }
    public void setDniCliente(String dniCliente) { this.dniCliente = dniCliente; }

    public double getMonto() { return monto; }
    public void setMonto(double monto) { this.monto = monto; }

    public int getMetodoPago() { return metodoPago; }
    public void setMetodoPago(int metodoPago) { this.metodoPago = metodoPago; }

    public String getMetodoPagoTexto() {
        switch (metodoPago) {
            case 1: return "Yape";
            case 2: return "Plin";
            case 3: return "Transferencia";
            case 4: return "Efectivo";
            default: return "Desconocido";
        }
    }

    public String toLineaTexto() {
        return idReserva + ";" + fecha + ";" + dniCliente + ";" + monto + ";" + metodoPago;
    }
}
