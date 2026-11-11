package modelo;

public class Habitacion {
private String idHabitacion;
    private String idPiso;
    private String numero;
    private int tipo; 

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

    public String getTipoTexto() {
        switch (tipo) {
            case 1: return "Personal";
            case 2: return "Doble";
            case 3: return "Matrimonial";
            case 4: return "Suite";
            case 5: return "Triple";
            default: return "Desconocido";
        }
    }

    public String toLineaTexto() {
        return idHabitacion + ";" + idPiso + ";" + numero + ";" + tipo;
    }
}
