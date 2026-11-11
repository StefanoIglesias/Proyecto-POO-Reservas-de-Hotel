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
    public void setIdDetalle(String idDetalle) { this.idDetalle = idDetalle; }

    public String getIdReserva() { return idReserva; }
    public void setIdReserva(String idReserva) { this.idReserva = idReserva; }

    public String getIdHabitacion() { return idHabitacion; }
    public void setIdHabitacion(String idHabitacion) { this.idHabitacion = idHabitacion; }

    public String toLineaTexto() {
        return idDetalle + ";" + idReserva + ";" + idHabitacion;
    }
}
