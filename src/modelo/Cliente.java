package modelo;

public class Cliente {
    private String dni;
    private String nombres;
    private String apellidos;
    private String celular;
    private String email;
    private int tipo; // 1: Normal, 2: Premium

    public Cliente(String dni, String nombres, String apellidos, String celular, String email, int tipo) {
        this.dni = dni;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.celular = celular;
        this.email = email;
        this.tipo = tipo;
    }

    // Getters y Setters
    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }

    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public String getCelular() { return celular; }
    public void setCelular(String celular) { this.celular = celular; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public int getTipo() { return tipo; }
    public void setTipo(int tipo) { this.tipo = tipo; }

    // Método para convertir el objeto a línea de texto para el archivo .txt
    public String toLineaTexto() {
        return dni + ";" + nombres + ";" + apellidos + ";" + celular + ";" + email + ";" + tipo;
    }
}