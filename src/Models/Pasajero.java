package Models;

public class Pasajero {
    private String nombre;
    private String DNI;
    private String origen; // País de prosedencia
    private String domicilio;
    private Cliente cliente; //

    public Pasajero(String nombre, String DNI, String origen, String domicilio, Cliente cliente) {
        this.nombre = nombre;
        this.DNI = DNI;
        this.origen = origen;
        this.domicilio = domicilio;
        this.cliente = cliente;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDNI() {
        return DNI;
    }

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public String getDomicilio() {
        return domicilio;
    }

    public void setDomicilio(String domicilio) {
        this.domicilio = domicilio;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    @Override
    public String toString() {
        return "Pasajero: " + nombre + ":\n" +
                "Documento: " + DNI + ":\n" +
                "Origen: " + origen + ":\n" +
                "Domicilio: " + domicilio + ":\n" +
                "Cuenta asociada: " + null;
    }

    public boolean equals (Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof Pasajero))
            return false;

        return ((Pasajero) o).getDNI().equals(this.getDNI());
    }

    public int hashCode ()
    {
        return getDNI().hashCode()*35;
    }
}
