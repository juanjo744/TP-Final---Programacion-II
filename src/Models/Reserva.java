package Models;

import Enums.EstadoReserva;

import java.time.LocalDate;
import java.util.ArrayList;

public class Reserva {
    private int id;
    private Habitacion habitacionSolicitada;
    private Cliente titular;
    private ArrayList<Pasajero> pasajeros;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private EstadoReserva estado;

    public Reserva(int id, Habitacion habitacionSolicitada, Cliente titular, ArrayList<Pasajero> pasajeros, LocalDate fechaInicio, LocalDate fechaFin, EstadoReserva estado) {
        this.id = id;
        this.habitacionSolicitada = habitacionSolicitada;
        this.titular = titular;
        this.pasajeros = pasajeros;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.estado = estado;
    }
    public Reserva(Habitacion habitacionSolicitada, Cliente titular, ArrayList<Pasajero> pasajeros, LocalDate fechaInicio, LocalDate fechaFin) {
        this.habitacionSolicitada = habitacionSolicitada;
        this.titular = titular;
        this.pasajeros = pasajeros;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.estado = EstadoReserva.PENDIENTE;
    }

    public int getId() {
        return id;
    }
    public Habitacion getHabitacionSolicitada() {
        return habitacionSolicitada;
    }
    public void setHabitacionSolicitada(Habitacion habitacionSolicitada) {
        this.habitacionSolicitada = habitacionSolicitada;
    }
    public Cliente getTitular() {
        return titular;
    }
    public void setTitular(Cliente titular) {
        this.titular = titular;
    }
    public ArrayList<Pasajero> getPasajeros() {
        return pasajeros;
    }
    public void setPasajeros(ArrayList<Pasajero> pasajeros) {
        this.pasajeros = pasajeros;
    }
    public LocalDate getFechaInicio() {
        return fechaInicio;
    }
    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }
    public LocalDate getFechaFin() {
        return fechaFin;
    }
    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }
    public EstadoReserva getEstado() {
        return estado;
    }
    public void setEstado(EstadoReserva estado) {
        this.estado = estado;
    }


    @Override
    public String toString() {
        return "Reserva Nro: " + id + ":\n" +
                "Titular: " + titular + ":\n" +
                "Habitación: " + habitacionSolicitada.getNumero() + ":\n" +
                "Cantidad de pasajeros: " + pasajeros.size() + ":\n" +
                "Fecha inicio: " + fechaInicio + ":\n" +
                "Fecha fin: " + fechaFin + ":\n" +
                "Estado: " + estado + ":\n";
    }

    public void asignarId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException(
                    "El ID debe ser mayor que cero.");
        }

        if (this.id != 0) {
            throw new IllegalStateException(
                    "La reserva ya tiene un ID asignado.");
        }

        this.id = id;
    }

    public void confirmarReserva (Reserva r)
    {
        r.setEstado(EstadoReserva.CONFIRMADA);
    }

    public void cancelarReserva (Reserva r)
    {
        r.setEstado(EstadoReserva.CANCELADA);
    }
}