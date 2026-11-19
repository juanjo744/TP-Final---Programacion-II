package Models;

import Enums.TipoUsuario;
import Exceptions.*;
import Gestor.*;

import java.time.LocalDate;
import java.util.List;

public class Recepcionista extends Usuario {

    private GestorReservas gestorReservas;
    private GestorHabitaciones gestorHabitaciones;
    private GestorOcupaciones gestorOcupaciones;
    private GestorPasajeros gestorPasajeros;
    private GestorUsuarios gestorUsuarios;

    public Recepcionista(String nombreUsuario, String contrasenia, GestorReservas gestorReservas, GestorHabitaciones gestorHabitaciones, GestorOcupaciones gestorOcupaciones, GestorPasajeros gestorPasajeros, GestorUsuarios gestorUsuarios) {
        super(nombreUsuario, contrasenia, TipoUsuario.RECEPCIONISTA);
        this.gestorReservas = gestorReservas;
        this.gestorHabitaciones = gestorHabitaciones;
        this.gestorOcupaciones = gestorOcupaciones;
        this.gestorPasajeros = gestorPasajeros;
        this.gestorUsuarios = gestorUsuarios;
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

    public void crearCliente(Cliente cliente) throws ValidacionException, JDBCException {
        gestorUsuarios.cargarElemento(cliente);
    }
}
