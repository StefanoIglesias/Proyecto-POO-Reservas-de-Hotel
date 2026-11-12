package modelo;

public class DetalleReserva {
    private String idDetalle;
    private String idReserva;
    private String idHabitacion;

    public DetalleReserva(String idDetalle, String idReserva, String idHabitacion) {
        this.idDetalle = idDetalle;
        this.idReserva = idReserva;
        this.idHabitacion = idHabitacion;
    }

    public String getIdDetalle() { return idDetalle; }
    public String getIdReserva() { return idReserva; }
    public String getIdHabitacion() { return idHabitacion; }

    public String toLineaTexto() {
        return idDetalle + ";" + idReserva + ";" + idHabitacion;
    }
}