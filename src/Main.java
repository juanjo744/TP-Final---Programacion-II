/// TRABAJO PRÁCTICO FINAL: PROGRAMACIÓN II
/// GESTÓR DE HOTEL
/// Alumnos: Juan José Sánchez Miller y Franco Miguel Fanelli

import Enums.Consumos;
import Connection.DBBackup;
import Enums.EstadoHabitacion;
import Enums.EstadoReserva;
import Enums.TipoUsuario;
import Exceptions.JDBCException;
import Exceptions.LoginException;
import Exceptions.ValidacionException;
import Gestor.*;
import Models.*;
import Repositorio.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.io.IOException;

public class Main { public static void main(String[] args){
    HabitacionJDBC habitacionJDBC = new HabitacionJDBC();
    ReservaJDBC reservaJDBC = new ReservaJDBC();
    OcupacionJDBC ocupacionJDBC = new OcupacionJDBC();
    PasajeroJDBC pasajeroJDBC = new PasajeroJDBC();
    UsuarioJDBC usuarioJDBC = new UsuarioJDBC();

    GestorHabitaciones gestorHabitaciones = new GestorHabitaciones(habitacionJDBC, ocupacionJDBC);
    GestorReservas gestorReservas = new GestorReservas(reservaJDBC, habitacionJDBC, ocupacionJDBC);
    GestorOcupaciones gestorOcupaciones = new GestorOcupaciones(ocupacionJDBC);
    GestorPasajeros gestorPasajeros = new GestorPasajeros(pasajeroJDBC, ocupacionJDBC, reservaJDBC);
    GestorUsuarios gestorUsuarios = new GestorUsuarios(usuarioJDBC);

    usuarioJDBC.configurarGestores(gestorReservas, gestorHabitaciones, gestorOcupaciones, gestorPasajeros, gestorUsuarios);

    try (Scanner scanner = new Scanner(System.in)) {
        GestorJDBC gestorJDBC = new GestorJDBC();
        gestorJDBC.cargarTablas();

        if (gestorUsuarios.listar().isEmpty()) {

            System.out.println("Configuracion inicial del sistema.");

            System.out.println("Nombre del administrador:");
            String nombreAdmin = scanner.nextLine();

            System.out.println("Contraseña del administrador:");
            String contraseniaAdmin = scanner.nextLine();

            Administrador admin = new Administrador(nombreAdmin, contraseniaAdmin, gestorUsuarios);

            gestorUsuarios.cargarElemento(admin);

            System.out.println("Administrador inicial creado correctamente.");
        }

        System.out.println("Ingrese su nombre de usuario: ");
        String nombreUsuario = scanner.nextLine();
        System.out.println("Ingrese su contraseña: ");
        String contrasenia = scanner.nextLine();

        Usuario usuario = usuarioJDBC.autenticar(nombreUsuario, contrasenia);

        System.out.println("Programa de gestión hotelera. Bienvenido " + usuario.getNombreUsuario() + ".");
        int opc = 0;

        switch (usuario.getTipo())
        {
            case ADMINISTRADOR ->
            {
                do {
                    try {

                    System.out.println("Menu: ADMINISTRADOR");
                    System.out.println("1. Crear usuario.");
                    System.out.println("2. Ver usuarios.");
                    System.out.println("3. Reasignar permisos a usuario.");
                    System.out.println("4. Cambiar contraseña de usuario.");
                    System.out.println("5. Eliminar usuario.");
                    System.out.println("6. Crear backup.");
                    System.out.println("7. Salir.");
                    opc = scanner.nextInt();

                    switch (opc) {
                        case 1:
                        {
                            scanner.nextLine();
                            System.out.println("Defina los datos de nuevo usuario...");
                            System.out.println("Nombre de usuario: ");
                            String nombreUsuarioN = scanner.nextLine();
                            System.out.println("Contraseña: ");
                            String contraseniaN = scanner.nextLine();
                            System.out.println("Defina el tipo de usuario." +
                                    "1 - Administrador.\n" +
                                    "2 - Recepcionista.\n" +
                                    "3 - Cliente.\n");
                            int opcUsuario = scanner.nextInt();

                            switch (opcUsuario)
                            {
                                case 1:
                                {
                                    System.out.println("------------CREACIÓN DE USUARIO------------");
                                    Usuario usuario1 = new Administrador(nombreUsuarioN, contraseniaN, gestorUsuarios);
                                    gestorUsuarios.cargarElemento(usuario1);

                                    break;
                                }
                                case 2:
                                {
                                    Usuario usuario1 = new Recepcionista(nombreUsuarioN, contraseniaN, gestorHabitaciones,
                                            gestorReservas, gestorOcupaciones, gestorPasajeros);
                                    gestorUsuarios.cargarElemento(usuario1);
                                    break;
                                }
                                case 3:
                                {
                                    Usuario usuario1 = new Cliente(nombreUsuarioN, contraseniaN, gestorReservas, gestorOcupaciones);
                                    gestorUsuarios.cargarElemento(usuario1);
                                    break;
                                }
                                default:
                                {
                                    break;
                                }
                            }
                            break;
                        }
                        case 2:
                        {
                            scanner.nextLine();
                            System.out.println("------------LISTA DE USUARIOS------------");
                            System.out.println(gestorUsuarios.listarString(usuarioJDBC.listarTodos()));
                            break;
                        }
                        case 3:
                        {
                            scanner.nextLine();
                            System.out.println("------------REASIGNAR PERMISOS------------");
                            System.out.println(gestorUsuarios.listarString(usuarioJDBC.listarTodos()));
                            System.out.println("Elija un usuario para modificar su tipo.");
                            String identificador = scanner.nextLine();

                            Usuario usuarioActualizar = gestorUsuarios.buscarPorID(identificador);
                            System.out.println("Defina su nuevo tipo: " +
                                    "1 - Administrador" +
                                    "2 - Recepcionista" +
                                    "3 - Cliente");
                            int opcActUs = scanner.nextInt();
                            scanner.nextLine();

                            switch (opcActUs)
                            {
                                case 1:
                                {
                                    gestorUsuarios.actualizarPermisos(usuarioActualizar, TipoUsuario.ADMINISTRADOR, usuario);
                                    break;
                                }
                                case 2:
                                {
                                    gestorUsuarios.actualizarPermisos(usuarioActualizar, TipoUsuario.RECEPCIONISTA, usuario);
                                    break;
                                }
                                case 3:
                                {
                                    gestorUsuarios.actualizarPermisos(usuarioActualizar, TipoUsuario.CLIENTE, usuario);
                                    break;
                                }
                                default:
                                {
                                    break;
                                }
                            }
                            break;
                        }
                        case 4: {
                            scanner.nextLine();

                            System.out.println("----- CAMBIAR CONTRASEÑA -----");
                            System.out.println(gestorUsuarios.listarString(gestorUsuarios.listar()));

                            System.out.print("Nombre del usuario: ");
                            String identificador = scanner.nextLine();

                            System.out.print("Nueva contraseña: ");
                            String nuevaContrasenia = scanner.nextLine();

                            gestorUsuarios.cambiarContrasenia(identificador, nuevaContrasenia);

                            System.out.println("Contraseña actualizada correctamente.");
                            break;
                        }
                        case 5:
                        {
                            scanner.nextLine();
                            System.out.println("------------ELIMINAR USUARIO------------");
                            System.out.println(gestorUsuarios.listarString(usuarioJDBC.listarTodos()));
                            System.out.println("Elija un usuario para eliminar.");
                            String identificador = scanner.nextLine();

                            if (usuario.getNombreUsuario().equals(identificador)) {
                                throw new ValidacionException("No podés eliminar tu propio usuario mientras estás usando el sistema.");
                            }

                            gestorUsuarios.bajar(identificador);
                            break;
                        }
                        case 6:
                        {
                            scanner.nextLine();
                            System.out.println("------------CREAR BACKUP------------");
                            System.out.println("Ruta elegida: C:\\Users\\Documents");
                            DBBackup dbBackup = new DBBackup();
                            dbBackup.crearBackup();
                            break;
                        }
                        default:
                        {
                            break;
                        }
                    }
                    } catch (InputMismatchException ex) {
                        scanner.nextLine();
                        System.out.println("Debe ingresar un numero valido. Intente nuevamente.");
                    } catch (ValidacionException ex) {
                        System.out.println("No se pudo completar la operacion: " + ex.getMessage());
                    } catch (JDBCException ex) {
                        System.out.println("Error de base de datos: " + ex.getMessage());
                    } catch (IOException ex) {
                        System.out.println("No se pudo crear el backup. Revise la ruta y la configuracion.");
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                        System.out.println("La creacion del backup fue interrumpida.");
                        return;
                    }
                } while (opc != 7);
            }

            case RECEPCIONISTA ->

            {
                Recepcionista recepcionista = (Recepcionista) usuario;
                do {
                    System.out.println("Menu: RECEPCIONISTA");
                    System.out.println("1. Realizar Check-Ins.");
                    System.out.println("2. Realizar Check-Outs.");
                    System.out.println("3. Gestionar pasajeros.");
                    System.out.println("4. Gestionar habitaciones.");
                    System.out.println("5. Gestionar reservas.");
                    System.out.println("6. Gestionar ocupaciones");
                    System.out.println("7. Volver atrás.");
                    try {
                    opc = scanner.nextInt();

                    switch (opc)
                    {
                        case 1: {
                            scanner.nextLine();

                            System.out.println("\n----- REALIZAR CHECK-IN -----");
                            System.out.println("1. Con reserva.");
                            System.out.println("2. Sin reserva.");
                            System.out.println("0. Volver.");
                            System.out.print("Seleccione una opción: ");

                            try {
                                int opcionCheckIn = Integer.parseInt(scanner.nextLine());

                                switch (opcionCheckIn) {
                                    case 1 -> {
                                        System.out.print("Número de reserva: ");
                                        int reservaId = Integer.parseInt(scanner.nextLine());

                                        Reserva reserva = gestorReservas.buscarPorID(reservaId);

                                        if (reserva == null) {
                                            System.out.println("La reserva no existe.");
                                            break;
                                        }

                                        Ocupacion ocupacion = recepcionista.realizarCheckIn(reserva);

                                        System.out.println("Check-in realizado correctamente.");
                                        System.out.println("Número de ocupación: " + ocupacion.getId());
                                    }

                                    case 2 -> {
                                        System.out.print("Número de habitación: ");
                                        int numeroHabitacion = Integer.parseInt(scanner.nextLine());

                                        Habitacion habitacion = gestorHabitaciones.buscarPorID(numeroHabitacion);

                                        if (habitacion == null) {
                                            System.out.println("La habitación no existe.");
                                            break;
                                        }

                                        System.out.print("DNI del pasajero: ");
                                        String dni = scanner.nextLine();

                                        Pasajero pasajero = gestorPasajeros.buscarPorID(dni);

                                        if (pasajero == null) {
                                            System.out.println("Debe registrar al pasajero antes del ingreso.");
                                            break;
                                        }

                                        System.out.print("Fecha de salida (AAAA-MM-DD): ");
                                        LocalDate fechaSalida = LocalDate.parse(scanner.nextLine());

                                        Ocupacion ocupacion = recepcionista.realizarCheckIn(habitacion, pasajero, fechaSalida);

                                        System.out.println("Check-in realizado correctamente.");
                                        System.out.println("Número de ocupación: " + ocupacion.getId());
                                    }

                                    case 0 -> {
                                        // Volver al menú de recepcionista.
                                    }

                                    default -> System.out.println("Opción inválida.");
                                }

                            } catch (NumberFormatException ex) {
                                System.out.println("Debe ingresar un número válido.");

                            } catch (DateTimeParseException ex) {
                                System.out.println("Fecha inválida. Use el formato AAAA-MM-DD.");

                            } catch (ValidacionException | JDBCException ex) {
                                System.out.println(ex.getMessage());
                            }

                            break;
                        }
                        case 2: {
                            scanner.nextLine();

                            try {
                                System.out.println("\n----- REALIZAR CHECK-OUT -----");
                                System.out.print("Número de ocupación: ");

                                int ocupacionId = Integer.parseInt(scanner.nextLine());

                                Ocupacion ocupacion = gestorOcupaciones.buscarPorID(ocupacionId);

                                if (ocupacion == null) {
                                    System.out.println("La ocupación no existe.");
                                    break;
                                }

                                recepcionista.realizarCheckOut(ocupacion);

                                System.out.println("Check-out realizado correctamente.");
                                System.out.println("La habitación quedó en estado LIMPIEZA.");

                            } catch (NumberFormatException ex) {
                                System.out.println("Debe ingresar un número válido.");

                            } catch (ValidacionException | JDBCException ex) {
                                System.out.println(ex.getMessage());
                            }

                            break;
                        }
                        case 3: {
                            scanner.nextLine();

                            System.out.println("\n----- GESTIONAR PASAJEROS -----");
                            System.out.println("1. Cargar pasajero.");
                            System.out.println("2. Buscar pasajero.");
                            System.out.println("3. Actualizar pasajero.");
                            System.out.println("4. Listar pasajeros.");
                            System.out.println("5. Listar alojados.");
                            System.out.println("6. Eliminar pasajero.");
                            System.out.println("0. Volver.");
                            System.out.print("Seleccione una opción: ");

                            try {
                                int opcionPasajero =
                                        Integer.parseInt(scanner.nextLine());

                                switch (opcionPasajero) {
                                    case 1 -> {
                                        System.out.print("Nombre de usuario del cliente asociado: ");
                                        String nombreCliente = scanner.nextLine();

                                        Usuario usuarioCliente = gestorUsuarios.buscarPorID(nombreCliente);

                                        if (!(usuarioCliente instanceof Cliente)) {
                                            System.out.println("Debe indicar un usuario existente de tipo cliente.");
                                            break;
                                        }

                                        Cliente clienteAsociado = (Cliente) usuarioCliente;

                                        System.out.print("Nombre del pasajero: ");
                                        String nombre = scanner.nextLine();

                                        System.out.print("DNI: ");
                                        String dni = scanner.nextLine();

                                        System.out.print("Origen: ");
                                        String origen = scanner.nextLine();

                                        System.out.print("Domicilio de origen: ");
                                        String domicilio = scanner.nextLine();

                                        Pasajero pasajero = new Pasajero(nombre, dni, origen, domicilio, clienteAsociado);

                                        recepcionista.cargarPasajero(pasajero);

                                        System.out.println("Pasajero registrado correctamente.");
                                    }

                                    case 2 -> {
                                        System.out.print("DNI del pasajero: ");
                                        String dni = scanner.nextLine();

                                        Pasajero pasajero = gestorPasajeros.buscarPorID(dni);

                                        if (pasajero == null) {
                                            System.out.println("No existe un pasajero con ese DNI.");
                                            break;
                                        }

                                        System.out.println("\n----- DATOS DEL PASAJERO -----");
                                        System.out.println(pasajero);

                                        if (pasajero.getCliente() != null) {
                                            System.out.println("Cliente asociado: " + pasajero.getCliente().getNombreUsuario());
                                        } else {
                                            System.out.println("Sin cliente asociado.");
                                        }
                                    }

                                    case 3 -> {
                                        System.out.print("DNI del pasajero que desea actualizar: ");
                                        String dni = scanner.nextLine();

                                        Pasajero pasajero = gestorPasajeros.buscarPorID(dni);

                                        if (pasajero == null) {
                                            System.out.println("No existe un pasajero con ese DNI.");
                                            break;
                                        }

                                        System.out.println("Datos actuales:");
                                        System.out.println(pasajero);

                                        System.out.print("Nuevo nombre: ");
                                        String nombre = scanner.nextLine();

                                        System.out.print("Nuevo origen: ");
                                        String origen = scanner.nextLine();

                                        System.out.print("Nuevo domicilio: ");
                                        String domicilio = scanner.nextLine();

                                        Pasajero actualizado = new Pasajero(nombre, pasajero.getDNI(), origen, domicilio, pasajero.getCliente());

                                        gestorPasajeros.actualizar(actualizado, dni);

                                        System.out.println("Pasajero actualizado correctamente.");
                                    }

                                    case 4 -> {
                                        List<Pasajero> pasajeros = gestorPasajeros.listar();

                                        if (pasajeros.isEmpty()) {
                                            System.out.println("No hay pasajeros registrados.");
                                            break;
                                        }

                                        System.out.println("\n----- LISTA DE PASAJEROS -----");

                                        for (Pasajero pasajero : pasajeros) {
                                            System.out.println(pasajero);
                                            System.out.println("-----------------------------");
                                        }
                                    }

                                    case 5 -> {
                                        Map<String, Pasajero> alojados = gestorPasajeros.listarAlojados();

                                        if (alojados.isEmpty()) {
                                            System.out.println("No hay pasajeros alojados actualmente.");
                                            break;
                                        }

                                        System.out.println("\n----- PASAJEROS ALOJADOS -----");

                                        for (Pasajero pasajero : alojados.values()) {
                                            System.out.println(pasajero);
                                            System.out.println("-----------------------------");
                                        }
                                    }

                                    case 6 -> {
                                        System.out.print("DNI del pasajero que desea eliminar: ");
                                        String dni = scanner.nextLine();

                                        Pasajero pasajero = gestorPasajeros.buscarPorID(dni);

                                        if (pasajero == null) {
                                            System.out.println("No existe un pasajero con ese DNI.");
                                            break;
                                        }

                                        System.out.println(pasajero);
                                        System.out.print("¿Confirma la eliminación? (S/N): ");

                                        String confirmacion = scanner.nextLine();

                                        if (!confirmacion.equalsIgnoreCase("S")) {
                                            System.out.println("Eliminación cancelada.");
                                            break;
                                        }

                                        gestorPasajeros.bajar(dni);

                                        System.out.println("Pasajero eliminado correctamente.");
                                    }

                                    case 0 -> {
                                        // Volver.
                                    }

                                    default -> System.out.println("Opción inválida.");
                                }

                            } catch (NumberFormatException ex) {
                                System.out.println("Debe ingresar un número válido.");

                            } catch (ValidacionException | JDBCException ex) {
                                System.out.println(ex.getMessage());
                            }

                            break;
                        }
                        case 4: {
                            scanner.nextLine();

                            System.out.println("\n----- GESTIONAR HABITACIONES -----");
                            System.out.println("1. Cargar habitación.");
                            System.out.println("2. Buscar habitación.");
                            System.out.println("3. Actualizar habitación.");
                            System.out.println("4. Listar habitaciones.");
                            System.out.println("5. Listar disponibles.");
                            System.out.println("6. Listar no disponibles.");
                            System.out.println("7. Eliminar habitación.");
                            System.out.println("8. Cambiar estado de habitación.");
                            System.out.println("0. Volver.");
                            System.out.print("Seleccione una opción: ");

                            try {
                                int opcionHabitacion = Integer.parseInt(scanner.nextLine());

                                switch (opcionHabitacion) {
                                    case 1 -> {
                                        System.out.print("Número de habitación: ");
                                        int numero = Integer.parseInt(scanner.nextLine());

                                        System.out.print("Capacidad: ");
                                        int capacidad = Integer.parseInt(scanner.nextLine());

                                        System.out.print("Precio por noche (ejemplo: 30000.50): ");
                                        double precio = Double.parseDouble(scanner.nextLine());

                                        Habitacion habitacion = new Habitacion(numero, capacidad, precio, EstadoHabitacion.DISPONIBLE);

                                        gestorHabitaciones.cargarElemento(habitacion);

                                        System.out.println("Habitación registrada correctamente.");
                                    }

                                    case 2 -> {
                                        System.out.print("Número de habitación: ");
                                        int numero = Integer.parseInt(scanner.nextLine());

                                        Habitacion habitacion = gestorHabitaciones.buscarPorID(numero);

                                        if (habitacion == null) {
                                            System.out.println("No existe una habitación con ese número.");
                                            break;
                                        }

                                        System.out.println("\n----- DATOS DE LA HABITACIÓN -----");
                                        System.out.println(habitacion);
                                    }

                                    case 3 -> {
                                        System.out.print("Número de habitación que desea actualizar: ");
                                        int numero = Integer.parseInt(scanner.nextLine());

                                        Habitacion habitacion = gestorHabitaciones.buscarPorID(numero);

                                        if (habitacion == null) {
                                            System.out.println("No existe una habitación con ese número.");
                                            break;
                                        }

                                        System.out.println("Datos actuales:");
                                        System.out.println(habitacion);

                                        System.out.print("Nueva capacidad: ");
                                        int capacidad = Integer.parseInt(scanner.nextLine());

                                        System.out.print("Nuevo precio por noche: ");
                                        double precio = Double.parseDouble(scanner.nextLine());

                                        Habitacion actualizada = new Habitacion(habitacion.getNumero(), capacidad, precio, habitacion.getEstado());

                                        gestorHabitaciones.actualizar(actualizada, numero);

                                        System.out.println("Habitación actualizada correctamente.");
                                    }

                                    case 4 -> {
                                        List<Habitacion> habitaciones = gestorHabitaciones.listar();

                                        if (habitaciones.isEmpty()) {
                                            System.out.println("No hay habitaciones registradas.");
                                            break;
                                        }

                                        System.out.println("\n----- LISTA DE HABITACIONES -----");

                                        for (Habitacion habitacion : habitaciones) {
                                            System.out.println(habitacion);
                                            System.out.println("--------------------------------");
                                        }
                                    }

                                    case 5 -> {
                                        List<Habitacion> disponibles = gestorHabitaciones.listarDisponibles();

                                        if (disponibles.isEmpty()) {
                                            System.out.println("No hay habitaciones disponibles actualmente.");
                                            break;
                                        }

                                        System.out.println("\n----- HABITACIONES DISPONIBLES -----");

                                        for (Habitacion habitacion : disponibles) {
                                            System.out.println(habitacion);
                                            System.out.println("-----------------------------------");
                                        }
                                    }

                                    case 6 -> {
                                        List<Habitacion> noDisponibles = gestorHabitaciones.listarNoDisponibles();

                                        if (noDisponibles.isEmpty()) {
                                            System.out.println("No hay habitaciones no disponibles.");
                                            break;
                                        }

                                        System.out.println("\n----- HABITACIONES NO DISPONIBLES -----");

                                        for (Habitacion habitacion : noDisponibles) {
                                            System.out.println(habitacion);
                                            System.out.println("--------------------------------------");
                                        }
                                    }

                                    case 7 -> {
                                        System.out.print("Número de habitación que desea eliminar: ");
                                        int numero = Integer.parseInt(scanner.nextLine());

                                        Habitacion habitacion = gestorHabitaciones.buscarPorID(numero);

                                        if (habitacion == null) {
                                            System.out.println("No existe una habitación con ese número.");
                                            break;
                                        }

                                        System.out.println(habitacion);
                                        System.out.print("¿Confirma la eliminación? (S/N): ");

                                        String confirmacion = scanner.nextLine();

                                        if (!confirmacion.equalsIgnoreCase("S")) {
                                            System.out.println("Eliminación cancelada.");
                                            break;
                                        }

                                        gestorHabitaciones.bajar(numero);

                                        System.out.println("Habitación eliminada correctamente.");
                                    }

                                    case 8 -> {
                                        System.out.print("Número de habitación: ");
                                        int numero = Integer.parseInt(scanner.nextLine());

                                        EstadoHabitacion[] estados = {EstadoHabitacion.DISPONIBLE, EstadoHabitacion.LIMPIEZA, EstadoHabitacion.REPARACION, EstadoHabitacion.DESINFECCION
                                        };

                                        for (int i = 0; i < estados.length; i++) {
                                            System.out.println((i + 1) + ". " + estados[i]);
                                        }

                                        System.out.print("Seleccione el nuevo estado: ");
                                        int opcionEstado = Integer.parseInt(scanner.nextLine());

                                        if (opcionEstado < 1 || opcionEstado > estados.length) {
                                            System.out.println("Estado inválido.");
                                            break;
                                        }

                                        gestorHabitaciones.cambiarEstado(numero, estados[opcionEstado - 1]);

                                        System.out.println("Estado actualizado correctamente.");
                                    }

                                    case 0 -> {
                                        // Volver.
                                    }

                                    default -> System.out.println("Opción inválida.");
                                }

                            } catch (NumberFormatException ex) {
                                System.out.println("Debe ingresar números válidos. " + "Para los decimales use un punto.");

                            } catch (ValidacionException | JDBCException ex) {
                                System.out.println(ex.getMessage());
                            }

                            break;
                        }
                        case 5: {
                            scanner.nextLine();

                            System.out.println("\n----- GESTIONAR RESERVAS -----");
                            System.out.println("1. Crear reserva.");
                            System.out.println("2. Buscar reserva.");
                            System.out.println("3. Actualizar reserva.");
                            System.out.println("4. Listar reservas.");
                            System.out.println("5. Cancelar reserva.");
                            System.out.println("6. Confirmar reserva.");
                            System.out.println("0. Volver.");
                            System.out.print("Seleccione una opción: ");

                            try {
                                int opcionReserva = Integer.parseInt(scanner.nextLine());

                                switch (opcionReserva) {
                                    case 1 -> {
                                        System.out.print("Nombre de usuario del cliente titular: ");
                                        String nombreCliente = scanner.nextLine();

                                        Usuario usuarioTitular = gestorUsuarios.buscarPorID(nombreCliente);

                                        if (!(usuarioTitular instanceof Cliente)) {
                                            System.out.println("Debe indicar un usuario existente de tipo cliente.");
                                            break;
                                        }

                                        Cliente titular = (Cliente) usuarioTitular;

                                        System.out.print("Número de habitación: ");
                                        int numeroHabitacion = Integer.parseInt(scanner.nextLine());

                                        Habitacion habitacion = gestorHabitaciones.buscarPorID(numeroHabitacion);

                                        if (habitacion == null) {
                                            System.out.println("La habitación no existe.");
                                            break;
                                        }

                                        System.out.print("Fecha de ingreso (AAAA-MM-DD): ");
                                        LocalDate inicio = LocalDate.parse(scanner.nextLine());

                                        System.out.print("Fecha de salida (AAAA-MM-DD): ");
                                        LocalDate fin = LocalDate.parse(scanner.nextLine());

                                        System.out.print("Cantidad de pasajeros: ");
                                        int cantidad = Integer.parseInt(scanner.nextLine());

                                        if (cantidad <= 0 || cantidad > habitacion.getCapacidad()) {
                                            System.out.println("La cantidad debe ser mayor que cero " + "y no superar la capacidad de la habitación.");
                                            break;
                                        }

                                        ArrayList<Pasajero> pasajeros = new ArrayList<>();
                                        boolean listaValida = true;

                                        for (int i = 0; i < cantidad; i++) {
                                            System.out.print("DNI del pasajero " + (i + 1) + ": ");
                                            String dni = scanner.nextLine();

                                            Pasajero pasajero = gestorPasajeros.buscarPorID(dni);

                                            if (pasajero == null) {
                                                System.out.println("Debe registrar al pasajero antes de crear la reserva.");
                                                listaValida = false;
                                                break;
                                            }

                                            if (pasajeros.contains(pasajero)) {
                                                System.out.println("No se puede agregar dos veces al mismo pasajero.");
                                                listaValida = false;
                                                break;
                                            }

                                            pasajeros.add(pasajero);
                                        }

                                        if (!listaValida) {
                                            break;
                                        }

                                        Reserva reserva = new Reserva(habitacion, titular, pasajeros, inicio, fin);

                                        recepcionista.crearReserva(reserva);

                                        System.out.println("Reserva creada correctamente. Número: " + reserva.getId());
                                        System.out.println("Estado: " + reserva.getEstado());
                                    }

                                    case 2 -> {
                                        System.out.print("Número de reserva: ");
                                        int id = Integer.parseInt(scanner.nextLine());

                                        Reserva reserva = gestorReservas.buscarPorID(id);

                                        if (reserva == null) {
                                            System.out.println("No existe una reserva con ese número.");
                                            break;
                                        }

                                        System.out.println("\n----- DATOS DE LA RESERVA -----");
                                        System.out.println(reserva);

                                        System.out.println("Pasajeros:");

                                        for (Pasajero pasajero : reserva.getPasajeros()) {
                                            System.out.println(pasajero);
                                            System.out.println("-----------------------------");
                                        }
                                    }
                                    case 3 -> {
                                        System.out.print("Número de reserva que desea actualizar: ");
                                        int id = Integer.parseInt(scanner.nextLine());

                                        Reserva reserva = gestorReservas.buscarPorID(id);

                                        if (reserva == null) {
                                            System.out.println("No existe una reserva con ese número.");
                                            break;
                                        }

                                        if (reserva.getEstado() != EstadoReserva.PENDIENTE && reserva.getEstado() != EstadoReserva.CONFIRMADA) {

                                            System.out.println("Solo se pueden actualizar reservas pendientes o confirmadas.");
                                            break;
                                        }

                                        System.out.println("Datos actuales:");
                                        System.out.println(reserva);

                                        System.out.print("Nuevo número de habitación: ");
                                        int numeroHabitacion = Integer.parseInt(scanner.nextLine());

                                        Habitacion habitacion = gestorHabitaciones.buscarPorID(numeroHabitacion);

                                        if (habitacion == null) {
                                            System.out.println("La habitación no existe.");
                                            break;
                                        }

                                        System.out.print("Nueva fecha de ingreso (AAAA-MM-DD): ");
                                        LocalDate inicio = LocalDate.parse(scanner.nextLine());

                                        System.out.print("Nueva fecha de salida (AAAA-MM-DD): ");
                                        LocalDate fin = LocalDate.parse(scanner.nextLine());

                                        Reserva actualizada = new Reserva(reserva.getId(), habitacion, reserva.getTitular(), new ArrayList<>(reserva.getPasajeros()), inicio, fin, reserva.getEstado());

                                        gestorReservas.actualizar(actualizada, id);

                                        System.out.println("Reserva actualizada correctamente.");
                                    }

                                    case 4 -> {
                                        List<Reserva> reservas = gestorReservas.listar();

                                        if (reservas.isEmpty()) {
                                            System.out.println("No hay reservas registradas.");
                                            break;
                                        }

                                        System.out.println("\n----- LISTA DE RESERVAS -----");

                                        for (Reserva reserva : reservas) {
                                            System.out.println(reserva);
                                            System.out.println("----------------------------");
                                        }
                                    }

                                    case 5 -> {
                                        System.out.print("Número de reserva que desea cancelar: ");
                                        int id = Integer.parseInt(scanner.nextLine());

                                        Reserva reserva = gestorReservas.buscarPorID(id);

                                        if (reserva == null) {
                                            System.out.println("No existe una reserva con ese número.");
                                            break;
                                        }

                                        if (reserva.getEstado() != EstadoReserva.PENDIENTE && reserva.getEstado() != EstadoReserva.CONFIRMADA) {

                                            System.out.println("Solo se pueden cancelar reservas pendientes o confirmadas.");
                                            break;
                                        }

                                        System.out.println(reserva);
                                        System.out.print("¿Confirma la cancelación? (S/N): ");

                                        String confirmacion = scanner.nextLine();

                                        if (!confirmacion.toUpperCase().equals("S")) {
                                            System.out.println("Operación cancelada.");
                                            break;
                                        }

                                        gestorReservas.bajar(id);

                                        System.out.println("Reserva cancelada correctamente.");
                                    }

                                    case 6 -> {
                                        System.out.print("Número de reserva que desea confirmar: ");
                                        int id = Integer.parseInt(scanner.nextLine());

                                        gestorReservas.confirmarReserva(id);

                                        System.out.println("Reserva confirmada correctamente.");
                                    }

                                    case 0 -> {
                                        // Volver.
                                    }

                                    default -> System.out.println("Opción inválida.");
                                }

                            } catch (NumberFormatException ex) {
                                System.out.println("Debe ingresar un número válido.");

                            } catch (DateTimeParseException ex) {
                                System.out.println("Fecha inválida. Use el formato AAAA-MM-DD.");
                            }
                            catch (ValidacionException | JDBCException ex) {
                                System.out.println(ex.getMessage());
                            }

                            break;
                        }
                        case 6: {
                            scanner.nextLine();

                            System.out.println("\n----- GESTIONAR OCUPACIONES -----");
                            System.out.println("1. Buscar ocupación.");
                            System.out.println("2. Listar todas.");
                            System.out.println("3. Listar activas.");
                            System.out.println("4. Listar finalizadas.");
                            System.out.println("0. Volver.");
                            System.out.print("Seleccione una opción: ");

                            try {
                                int opcionOcupacion =
                                        Integer.parseInt(scanner.nextLine());

                                switch (opcionOcupacion) {
                                    case 1 -> {
                                        System.out.print("Número de ocupación: ");
                                        int id = Integer.parseInt(scanner.nextLine());

                                        Ocupacion ocupacion = gestorOcupaciones.buscarPorID(id);

                                        if (ocupacion == null) {
                                            System.out.println("No existe una ocupación con ese número.");
                                            break;
                                        }

                                        System.out.println("\n----- DATOS DE LA OCUPACIÓN -----");
                                        System.out.println(ocupacion);

                                        System.out.println("Pasajeros: ");

                                        for (Pasajero pasajero : ocupacion.getPasajeros()) {
                                            System.out.println(pasajero);
                                            System.out.println("-----------------------------");
                                        }
                                    }

                                    case 2 -> {
                                        List<Ocupacion> ocupaciones = gestorOcupaciones.listar();

                                        if (ocupaciones.isEmpty()) {
                                            System.out.println("No hay ocupaciones registradas.");
                                            break;
                                        }

                                        System.out.println("\n----- LISTA DE OCUPACIONES -----");

                                        for (Ocupacion ocupacion : ocupaciones) {
                                            System.out.println(ocupacion);
                                            System.out.println("--------------------------------");
                                        }
                                    }

                                    case 3 -> {
                                        Map<Integer, Ocupacion> activas = gestorOcupaciones.listarPorActividad(true);

                                        if (activas.isEmpty()) {
                                            System.out.println("No hay ocupaciones activas.");
                                            break;
                                        }

                                        System.out.println("\n----- OCUPACIONES ACTIVAS -----");

                                        for (Ocupacion ocupacion : activas.values()) {
                                            System.out.println(ocupacion);
                                            System.out.println("-------------------------------");
                                        }
                                    }

                                    case 4 -> {
                                        Map<Integer, Ocupacion> finalizadas = gestorOcupaciones.listarPorActividad(false);

                                        if (finalizadas.isEmpty()) {
                                            System.out.println("No hay ocupaciones finalizadas.");
                                            break;
                                        }

                                        System.out.println("\n----- OCUPACIONES FINALIZADAS -----");

                                        for (Ocupacion ocupacion : finalizadas.values()) {
                                            System.out.println(ocupacion);
                                            System.out.println("----------------------------------");
                                        }
                                    }

                                    case 0 -> {
                                        // Volver.
                                    }

                                    default -> System.out.println("Opción inválida.");
                                }

                            } catch (NumberFormatException ex) {
                                System.out.println("Debe ingresar un número válido.");
                            }
                            catch (ValidacionException | JDBCException ex) {
                                System.out.println(ex.getMessage());
                            }

                            break;
                        }
                    }
                    } catch (InputMismatchException ex) {
                        scanner.nextLine();
                        System.out.println("Debe ingresar un numero valido. Intente nuevamente.");
                    }
                } while (opc != 7);
            }

            case CLIENTE -> {
                Cliente cliente = (Cliente) usuario;
                int opcionCliente;

                do {
                    System.out.println("\n----- MENÚ CLIENTE -----");
                    System.out.println("1. Solicitar reserva.");
                    System.out.println("2. Ver mis reservas.");
                    System.out.println("3. Realizar consumo.");
                    System.out.println("4. Ver gastos adicionales.");
                    System.out.println("5. Ver total de mi estadía.");
                    System.out.println("0. Cerrar sesión.");
                    System.out.print("Seleccione una opción: ");

                    try {
                        opcionCliente = Integer.parseInt(scanner.nextLine());
                    } catch (NumberFormatException ex) {
                        System.out.println("Debe ingresar un número.");
                        continue;
                    }

                    switch (opcionCliente) {
                        case 1 -> {
                            try {
                                System.out.println("\n----- SOLICITAR RESERVA -----");

                                System.out.print("Número de habitación: ");
                                int numeroHabitacion = Integer.parseInt(scanner.nextLine());

                                Habitacion habitacion = gestorHabitaciones.buscarPorID(numeroHabitacion);

                                if (habitacion == null) {
                                    System.out.println("No existe esa habitación.");
                                    break;
                                }

                                System.out.print("Fecha de ingreso (AAAA-MM-DD): ");
                                LocalDate fechaInicio = LocalDate.parse(scanner.nextLine());

                                System.out.print("Fecha de salida (AAAA-MM-DD): ");
                                LocalDate fechaFin = LocalDate.parse(scanner.nextLine());

                                System.out.print("Cantidad de pasajeros: ");
                                int cantidad = Integer.parseInt(scanner.nextLine());

                                if (cantidad <= 0 || cantidad > habitacion.getCapacidad()) {
                                    System.out.println("La cantidad debe ser mayor que cero " + "y no superar la capacidad de la habitación.");
                                    break;
                                }

                                ArrayList<Pasajero> pasajeros = new ArrayList<>();
                                boolean listaValida = true;

                                for (int i = 0; i < cantidad; i++) {
                                    System.out.print("DNI del pasajero " + (i + 1) + ": ");

                                    String dni = scanner.nextLine();
                                    Pasajero pasajero = gestorPasajeros.buscarPorID(dni);

                                    if (pasajero == null) {
                                        System.out.println("El pasajero debe registrarse en recepción.");
                                        listaValida = false;
                                        break;
                                    }

                                    if (pasajero.getCliente() == null || !cliente.getNombreUsuario().equals(pasajero.getCliente().getNombreUsuario())) {

                                        System.out.println("El pasajero no está asociado a tu cuenta.");
                                        listaValida = false;
                                        break;
                                    }

                                    if (pasajeros.contains(pasajero)) {
                                        System.out.println("No se puede agregar dos veces al mismo pasajero.");
                                        listaValida = false;
                                        break;
                                    }

                                    pasajeros.add(pasajero);
                                }

                                if (!listaValida) {
                                    break;
                                }

                                Reserva reserva = new Reserva(habitacion, cliente, pasajeros, fechaInicio, fechaFin);

                                cliente.solicitarReserva(reserva);

                                System.out.println("Reserva solicitada correctamente. Número: " + reserva.getId());
                                System.out.println("Estado: " + reserva.getEstado());

                            } catch (NumberFormatException ex) {
                                System.out.println("Debe ingresar un número válido.");

                            } catch (DateTimeParseException ex) {
                                System.out.println("Fecha inválida. Use el formato AAAA-MM-DD.");

                            } catch (ValidacionException | JDBCException ex) {
                                System.out.println(ex.getMessage());
                            }
                        }

                        case 2 -> {
                            try {
                                System.out.println("\n----- MIS RESERVAS -----");

                                List<Reserva> reservas = gestorReservas.listar();
                                boolean tieneReservas = false;

                                for (Reserva reserva : reservas) {
                                    if (reserva.getTitular() != null && cliente.getNombreUsuario().equals(reserva.getTitular().getNombreUsuario())) {

                                        System.out.println("Reserva número: " + reserva.getId());
                                        System.out.println("Habitación: " + reserva.getHabitacionSolicitada().getNumero());
                                        System.out.println("Ingreso: " + reserva.getFechaInicio());
                                        System.out.println("Salida: " + reserva.getFechaFin());
                                        System.out.println("Estado: " + reserva.getEstado());
                                        System.out.println("Pasajeros: ");

                                        for (Pasajero pasajero : reserva.getPasajeros()) {
                                            System.out.println("- " + pasajero.getNombre() + " | DNI: " + pasajero.getDNI());
                                        }

                                        System.out.println("-------------------------");
                                        tieneReservas = true;
                                    }
                                }

                                if (!tieneReservas) {
                                    System.out.println("No tenés reservas registradas.");
                                }

                            } catch (JDBCException ex) {
                                System.out.println(ex.getMessage());
                            }
                        }

                        case 3 -> {
                            try {
                                System.out.println("\n----- REALIZAR CONSUMO -----");

                                System.out.print("Número de ocupación: ");
                                int ocupacionId = Integer.parseInt(scanner.nextLine());

                                Consumos[] consumos = Consumos.values();

                                for (int i = 0; i < consumos.length; i++) {
                                    System.out.println((i + 1) + ". " + consumos[i].name() + " - $" + consumos[i].getPrecio());
                                }

                                System.out.println("0. Volver.");
                                System.out.print("Seleccione un consumo: ");

                                int opcionConsumo = Integer.parseInt(scanner.nextLine());

                                if (opcionConsumo == 0) {
                                    break;
                                }

                                if (opcionConsumo < 1 || opcionConsumo > consumos.length) {

                                    System.out.println("Opción inválida.");
                                    break;
                                }

                                Consumos consumoElegido = consumos[opcionConsumo - 1];

                                cliente.realizarConsumo(ocupacionId, consumoElegido);

                                System.out.println("Consumo guardado correctamente: " + consumoElegido.name());

                            } catch (NumberFormatException ex) {
                                System.out.println("Debe ingresar un número válido.");

                            } catch (ValidacionException | JDBCException ex) {
                                System.out.println(ex.getMessage());
                            }
                        }

                        case 4 -> {
                            try {
                                System.out.print("Número de ocupación: ");
                                int ocupacionId = Integer.parseInt(scanner.nextLine());

                                System.out.println("Tus gastos adicionales en esta estadía: $" + cliente.consultarGastos(ocupacionId).toPlainString());

                            } catch (NumberFormatException ex) {
                                System.out.println("Debe ingresar un número válido.");

                            } catch (ValidacionException | JDBCException ex) {
                                System.out.println(ex.getMessage());
                            }
                        }
                        case 5 -> {
                            try {
                                System.out.print("Número de ocupación: ");
                                int ocupacionId = Integer.parseInt(scanner.nextLine());

                                BigDecimal total = cliente.consultarTotalEstadia(ocupacionId);

                                System.out.println("Total del alojamiento y tus consumos: $" + total.toPlainString());

                                System.out.println("Si la estadía sigue activa, es el acumulado " + "hasta hoy, con mínimo de una noche.");

                            } catch (NumberFormatException ex) {
                                System.out.println("Debe ingresar un número válido.");

                            } catch (ValidacionException | JDBCException ex) {
                                System.out.println(ex.getMessage());
                            }
                        }

                        case 0 -> {
                            cliente.cerrarSesion();
                            System.out.println("Sesión cerrada.");
                        }

                        default -> System.out.println("Opción inválida.");
                    }

                } while (cliente.isSesionActiva());
            }

            default ->
            {

            }
        }
    } catch (JDBCException e) {
        System.out.println("Error de base de datos: " + e.getMessage());
    } catch (LoginException e) {
        System.out.println(e.getMessage());
    } catch (ValidacionException e) {
        System.out.println("No se pudo completar la operacion: " + e.getMessage());
    } catch (InputMismatchException e) {
        System.out.println("Debe ingresar un numero valido.");
    }
}
}