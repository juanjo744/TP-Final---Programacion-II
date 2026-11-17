package Models;

import Exceptions.JDBCException;
import Exceptions.VReservaInvalidaException;
import Gestor.GestorHabitaciones;
import Gestor.GestorReservas;

import java.util.List;

public class Recepcionista extends Usuario {

    private GestorReservas gestorReservas;
    private GestorHabitaciones gestorHabitaciones;

    public Recepcionista(String nombreUsuario, String contrasenia, GestorHabitaciones gestorHabitaciones, GestorReservas gestorReservas) {
        super(nombreUsuario, contrasenia);
        this.gestorHabitaciones = gestorHabitaciones;
        this.gestorReservas = gestorReservas;
    }

    public void realizarCheckIn(Reserva reserva) {

    }

    public void realizarCheckIn(Habitacion habitacion, Pasajero pasajero){

    }
    public void realizarCheckOut(Ocupacion ocupacion){

    }

    public void cargarPasajero(){

    }

    public void crearReserva(Reserva reserva) throws VReservaInvalidaException, JDBCException {

        gestorReservas.cargarElemento(reserva);

    }


    public List<Habitacion> estadoHabitaciones() throws JDBCException {
        return gestorHabitaciones.listar();
    }


}
