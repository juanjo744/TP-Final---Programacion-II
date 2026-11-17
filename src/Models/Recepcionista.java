package Models;

import Enums.TipoUsuario;
import Exceptions.JDBCException;
import Exceptions.VOcupacionInvalidaException;
import Exceptions.VPasajeroInvalidoException;
import Exceptions.VReservaInvalidaException;
import Gestor.GestorHabitaciones;
import Gestor.GestorOcupaciones;
import Gestor.GestorPasajeros;
import Gestor.GestorReservas;

import java.time.LocalDate;
import java.util.List;

public class Recepcionista extends Usuario {

    private GestorReservas gestorReservas;
    private GestorHabitaciones gestorHabitaciones;
    private GestorOcupaciones gestorOcupaciones;
    private GestorPasajeros gestorPasajeros;

    public Recepcionista(String nombreUsuario, String contrasenia, GestorHabitaciones gestorHabitaciones, GestorReservas gestorReservas, GestorOcupaciones gestorOcupaciones, GestorPasajeros gestorPasajeros) {
        super(nombreUsuario, contrasenia, TipoUsuario.RECEPCIONISTA);
        this.gestorHabitaciones = gestorHabitaciones;
        this.gestorReservas = gestorReservas;
        this.gestorOcupaciones = gestorOcupaciones;
        this.gestorPasajeros = gestorPasajeros;
    }

    public Ocupacion realizarCheckIn(Reserva reserva) throws JDBCException, VOcupacionInvalidaException {

        return gestorOcupaciones.hacerCheckIn(reserva);
    }

    public Ocupacion realizarCheckIn(Habitacion habitacion, Pasajero pasajero, LocalDate fechaSalida) throws VOcupacionInvalidaException, JDBCException {

        return gestorOcupaciones.hacerCheckIn(habitacion, pasajero, fechaSalida);
    }
    public void realizarCheckOut(Ocupacion ocupacion) throws VOcupacionInvalidaException, JDBCException {

        gestorOcupaciones.hacerCheckOut(ocupacion);
    }

    public void cargarPasajero(Pasajero pasajero) throws JDBCException, VPasajeroInvalidoException {

        gestorPasajeros.cargarElemento(pasajero);
    }

    public void crearReserva(Reserva reserva) throws VReservaInvalidaException, JDBCException {

        gestorReservas.cargarElemento(reserva);

    }


    public List<Habitacion> estadoHabitaciones() throws JDBCException {
        return gestorHabitaciones.listar();
    }


}
