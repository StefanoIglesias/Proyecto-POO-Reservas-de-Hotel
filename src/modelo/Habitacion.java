package modelo;

public class Habitacion {
    private String idHabitacion;
    private String idPiso;
    private String numero;
    private int tipo; // 1: Personal, 2: Doble, 3: Triple, 4: Suite, 5: Matrimonial

    public Habitacion(String idHabitacion, String idPiso, String numero, int tipo) {
        this.idHabitacion = idHabitacion;
        this.idPiso = idPiso;
        this.numero = numero;
        this.tipo = tipo;
    }

    public String getIdHabitacion() { return idHabitacion; }
    public void setIdHabitacion(String idHabitacion) { this.idHabitacion = idHabitacion; }

    public String getIdPiso() { return idPiso; }
    public void setIdPiso(String idPiso) { this.idPiso = idPiso; }

    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }

    public int getTipo() { return tipo; }
    public void setTipo(int tipo) { this.tipo = tipo; }

    public double getPrecioNoche() {
        switch (tipo) {
            case 1: return 50.0;   // Personal
            case 2: return 100.0;  // Doble
            case 3: return 150.0;  // Triple
            case 4: return 200.0;  // Suite
            case 5: return 250.0;  // Matrimonial
            default: return 50.0;
        }
    }

    public String getTipoTexto() {
        switch (tipo) {
            case 1: return "1.- Personal (50 S/)";
            case 2: return "2.- Doble (100 S/)";
            case 3: return "3.- Triple (150 S/)";
            case 4: return "4.- Suite (200 S/)";
            case 5: return "5.- Matrimonial (250 S/)";
            default: return "Personal";
        }
    }

    public String toLineaTexto() {
        return idHabitacion + ";" + idPiso + ";" + numero + ";" + tipo;
    }
}