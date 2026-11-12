package Models;

import Enums.EstadoReserva;

import java.time.LocalDate;
import java.util.ArrayList;

public class Reserva {
    private static int count = 0;
    private int id;
    private Habitacion habitacionSolicitada;
    private Object titular;
    private ArrayList<Pasajero> pasajeros;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private EstadoReserva estado;

    public Reserva(int id, Habitacion habitacionSolicitada, Object titular, ArrayList<Pasajero> pasajeros, LocalDate fechaInicio, LocalDate fechaFin) {
        count++;
        this.id = count;
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

    public Object getTitular() {
        return titular;
    }

    public void setTitular(Object titular) {
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

    public void confirmarReserva (Reserva r)
    {
        r.setEstado(EstadoReserva.CONFIRMADA);
    }

    public void cancelarReserva (Reserva r)
    {
        r.setEstado(EstadoReserva.CANCELADA);
    }
}