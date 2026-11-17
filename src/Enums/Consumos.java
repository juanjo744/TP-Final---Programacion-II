package Enums;

public enum Consumos {
    BEBIDA(2000), SNACKS(3500), COMIDA(15000);

    double precio;

    Consumos(double precio) {
        this.precio = precio;
    }

    public double getPrecio() {
        return precio;
    }
}
