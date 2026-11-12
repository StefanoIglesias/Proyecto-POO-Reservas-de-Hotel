package modelo;

public class Reserva {
    private String idReserva;
    private String fechaInicio;
    private int diasEstadia;
    private String fechaSalida;
    private String dniCliente;
    private double montoTotal;
    private int metodoPago;

    public Reserva(String idReserva, String fechaInicio, int diasEstadia, String fechaSalida, String dniCliente, double montoTotal, int metodoPago) {
        this.idReserva = idReserva;
        this.fechaInicio = fechaInicio;
        this.diasEstadia = diasEstadia;
        this.fechaSalida = fechaSalida;
        this.dniCliente = dniCliente;
        this.montoTotal = montoTotal;
        this.metodoPago = metodoPago;
    }

    public String getIdReserva() { return idReserva; }
    public String getFechaInicio() { return fechaInicio; }
    public int getDiasEstadia() { return diasEstadia; }
    public String getFechaSalida() { return fechaSalida; }
    public String getDniCliente() { return dniCliente; }
    public double getMontoTotal() { return montoTotal; }
    public int getMetodoPago() { return metodoPago; }

    public String getMetodoPagoTexto() {
        switch (metodoPago) {
            case 1: return "Yape";
            case 2: return "Plin";
            case 3: return "Transferencia";
            case 4: return "Efectivo";
            default: return "Yape";
        }
    }

    public String toLineaTexto() {
        return idReserva + ";" + fechaInicio + ";" + diasEstadia + ";" + fechaSalida + ";" + dniCliente + ";" + montoTotal + ";" + metodoPago;
    }
}