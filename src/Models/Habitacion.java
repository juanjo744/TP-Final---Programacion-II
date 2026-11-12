package Models;

import Enums.EstadoHabitacion;

public class Habitacion {
    private static int count = 0;
    private int numero;
    private int capacidad;
    private double precioPorNoche;
    private EstadoHabitacion estado;

    public Habitacion(int capacidad, double precioPorNoche, EstadoHabitacion estado) {
        count++;
        this.numero = count;
        this.capacidad = capacidad;
        this.precioPorNoche = precioPorNoche;
        this.estado = estado;
    }

    public int getNumero() {
        return numero;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(int capacidad) {
        this.capacidad = capacidad;
    }

    public double getPrecioPorNoche() {
        return precioPorNoche;
    }

    public void setPrecioPorNoche(double precioPorNoche) {
        this.precioPorNoche = precioPorNoche;
    }

    public EstadoHabitacion getEstado() {
        return estado;
    }

    public void setEstado(EstadoHabitacion estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "Habitacion Nro" + numero + ":\n" +
                "Capacidad: " + capacidad + ":\n" +
                "PrecioPorNoche: " + precioPorNoche + ":\n" +
                "Estado: " + estado;
    }
}