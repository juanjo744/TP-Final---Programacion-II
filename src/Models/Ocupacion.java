package Models;

import java.time.LocalDate;
import java.util.ArrayList;

public class Ocupacion {
    private int id;
    private Habitacion habitacionReservada;
    private ArrayList<Pasajero> pasajeros;
    private LocalDate fechaCheckIn;
    private LocalDate fechaCheckOut;
    private boolean activa;

    public Ocupacion(Habitacion habitacionReservada, ArrayList<Pasajero> pasajeros, LocalDate fechaCheckIn, LocalDate fechaCheckOut, boolean activa, int id) {
        this.id = id;
        this.habitacionReservada = habitacionReservada;
        this.pasajeros = pasajeros;
        this.fechaCheckIn = fechaCheckIn;
        this.fechaCheckOut = fechaCheckOut;
        this.activa = activa;
    }
    public Ocupacion(Habitacion habitacionReservada, ArrayList<Pasajero> pasajeros, LocalDate fechaCheckIn, LocalDate fechaCheckOut, boolean activa) {
        this.habitacionReservada = habitacionReservada;
        this.pasajeros = pasajeros;
        this.fechaCheckIn = fechaCheckIn;
        this.fechaCheckOut = fechaCheckOut;
        this.activa = activa;
    }

    public int getId() {
        return id;
    }
    public Habitacion getHabitacionReservada() {
        return habitacionReservada;
    }
    public void setHabitacionReservada(Habitacion habitacionReservada) {
        this.habitacionReservada = habitacionReservada;
    }
    public ArrayList<Pasajero> getPasajeros() {
        return pasajeros;
    }
    public void setPasajeros(ArrayList<Pasajero> pasajeros) {
        this.pasajeros = pasajeros;
    }
    public LocalDate getFechaCheckIn() {
        return fechaCheckIn;
    }
    public void setFechaCheckIn(LocalDate fechaCheckIn) {
        this.fechaCheckIn = fechaCheckIn;
    }
    public LocalDate getFechaCheckOut() {
        return fechaCheckOut;
    }
    public void setFechaCheckOut(LocalDate fechaCheckOut) {
        this.fechaCheckOut = fechaCheckOut;
    }
    public boolean isActiva() {
        return activa;
    }
    public void setActiva(boolean activa) {
        this.activa = activa;
    }


    @Override
    public String toString() {
        return "Ocupación Nro: " + id + ":\n" +
                "Habitación: " + habitacionReservada.getNumero() + ":\n" +
                "Cantidad de pasajeros: " + pasajeros.size() + ":\n" +
                "Check-In: " + fechaCheckIn + ":\n" +
                "Check-Out: " + fechaCheckOut + ":\n" +
                "Estado: " + activa + ":\n";
    }

    public void asignarId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException(
                    "El ID debe ser mayor que cero.");
        }

        if (this.id != 0) {
            throw new IllegalStateException(
                    "La ocupación ya tiene un ID asignado.");
        }

        this.id = id;
    }


}
