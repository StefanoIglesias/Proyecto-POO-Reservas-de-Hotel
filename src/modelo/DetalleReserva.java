
package modelo;

public class DetalleReserva {
    // Atributos
    private String idReserva;        // código de la reserva a la que pertenece
    private String numeroHabitacion; // habitación reservada
    private int noches;              // cantidad de noches
    private double precioPorNoche;   // precio de la habitación por noche

    // Constructor
    public DetalleReserva(String idReserva, String numeroHabitacion, int noches, double precioPorNoche) {
        this.idReserva = idReserva;
        this.numeroHabitacion = numeroHabitacion;
        this.noches = noches;
        this.precioPorNoche = precioPorNoche;
    }

    // Getters y Setters
    public String getIdReserva() { return idReserva; }
    public void setIdReserva(String idReserva) { this.idReserva = idReserva; }

    public String getNumeroHabitacion() { return numeroHabitacion; }
    public void setNumeroHabitacion(String numeroHabitacion) { this.numeroHabitacion = numeroHabitacion; }

    public int getNoches() { return noches; }
    public void setNoches(int noches) { this.noches = noches; }

    public double getPrecioPorNoche() { return precioPorNoche; }
    public void setPrecioPorNoche(double precioPorNoche) { this.precioPorNoche = precioPorNoche; }

    // Método para calcular el subtotal de esta línea (noches x precio por noche)
    public double getSubtotal() {
        return noches * precioPorNoche;
    }

    // Método para convertir el objeto a línea de texto para el archivo .txt
    public String toLineaTexto() {
        return idReserva + ";" + numeroHabitacion + ";" + noches + ";" + precioPorNoche;
    }
}