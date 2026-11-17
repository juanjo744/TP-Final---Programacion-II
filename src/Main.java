/// TRABAJO PRÁCTICO FINAL: PROGRAMACIÓN II
/// GESTÓR DE HOTEL
/// Alumnos: Juan José Sánchez Miller y Franco Miguel Fanelli

import Enums.TipoUsuario;
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

        System.out.println("Ingrese su nombre de usuario: ");
        String nombreUsuario = scanner.nextLine();
        System.out.println("Ingrese su contraseña: ");
        String contrasenia = scanner.nextLine();

        Usuario usuario = usuarioJDBC.autenticar(nombreUsuario, contrasenia);

        System.out.println("Programa de gestión hotelera. Bienvenido " + usuario.getNombreUsuario() + ".");

        switch (usuario.getTipo())
        {
            case ADMINISTRADOR ->
            {
                System.out.println("Menu: ADMINISTRADOR");
                System.out.println("1. Crear usuario.");
                System.out.println("2. Asignar permisos a usuario.");
                System.out.println("3. Ver usuarios.");
                System.out.println("4. Actualizar usuario.");
                System.out.println("5. Eliminar usuario.");
                System.out.println("6. Crear backup.");

                break;
            }

            case RECEPCIONISTA ->
            {
                break;
            }

            case CLIENTE ->
            {
                break;
            }

            default ->
            {

            }
        }
    } catch (JDBCException e) {
        throw new RuntimeException(e);
    } catch (LoginException e) {
        throw new RuntimeException(e);
    }
}
}