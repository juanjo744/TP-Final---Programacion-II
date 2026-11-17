/// TRABAJO PRÁCTICO FINAL: PROGRAMACIÓN II
/// GESTÓR DE HOTEL
/// Alumnos: Juan José Sánchez Miller y Franco Miguel Fanelli

import Exceptions.JDBCException;
import Exceptions.LoginException;
import Gestor.*;
import Models.Usuario;
import Repositorio.*;
import java.util.Scanner;

public class Main { public static void main(String[] args){
    HabitacionJDBC habitacionJDBC = new HabitacionJDBC();
    ReservaJDBC reservaJDBC = new ReservaJDBC();
    OcupacionJDBC ocupacionJDBC = new OcupacionJDBC();
    PasajeroJDBC pasajeroJDBC = new PasajeroJDBC();
    UsuarioJDBC usuarioJDBC = new UsuarioJDBC();

    GestorHabitaciones gestorHabitaciones = new GestorHabitaciones(habitacionJDBC);
    GestorReservas gestorReservas = new GestorReservas(reservaJDBC, habitacionJDBC, ocupacionJDBC);
    GestorOcupaciones gestorOcupaciones = new GestorOcupaciones(ocupacionJDBC);
    GestorPasajeros gestorPasajeros = new GestorPasajeros(pasajeroJDBC, ocupacionJDBC, reservaJDBC);
    GestorUsuarios gestorUsuarios = new GestorUsuarios(usuarioJDBC);

    usuarioJDBC.configurarGestores(gestorReservas, gestorHabitaciones, gestorOcupaciones, gestorPasajeros, gestorUsuarios);

    try (Scanner scanner = new Scanner(System.in)) {
        GestorJDBC gestorJDBC = new GestorJDBC();
        gestorJDBC.cargarTablas();
    } catch (JDBCException e) {
        throw new RuntimeException(e);
    }
}
}