/// TRABAJO PRÁCTICO FINAL: PROGRAMACIÓN II
/// GESTÓR DE HOTEL
/// Alumnos: Juan José Sánchez Miller y Franco Miguel Fanelli

import Connection.DBBackup;
import Enums.TipoUsuario;
import Exceptions.JDBCException;
import Exceptions.LoginException;
import Exceptions.ValidacionException;
import Gestor.*;
import Models.Administrador;
import Models.Cliente;
import Models.Recepcionista;
import Models.Usuario;
import Repositorio.*;

import java.io.IOException;
import java.util.List;
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
        int opc;

        switch (usuario.getTipo())
        {
            case ADMINISTRADOR ->
            {
                do {

                    System.out.println("Menu: ADMINISTRADOR");
                    System.out.println("1. Crear usuario.");
                    System.out.println("2. Ver usuarios.");
                    System.out.println("3. Reasignar permisos a usuario.");
                    System.out.println("4. Actualizar usuario.");
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
                                    Usuario usuario1 = new Cliente(nombreUsuarioN, contraseniaN, gestorReservas);
                                    gestorUsuarios.cargarElemento(usuario1);
                                    break;
                                }
                                default:
                                {
                                    break;
                                }
                            }
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
                                    usuarioJDBC.actualizar(usuarioActualizar, TipoUsuario.ADMINISTRADOR.name());
                                    break;
                                }
                                case 2:
                                {
                                    gestorUsuarios.actualizarPermisos(usuarioActualizar, TipoUsuario.RECEPCIONISTA, usuario);
                                    usuarioJDBC.actualizar(usuarioActualizar, TipoUsuario.RECEPCIONISTA.name());
                                    break;
                                }
                                case 3:
                                {
                                    gestorUsuarios.actualizarPermisos(usuarioActualizar, TipoUsuario.CLIENTE, usuario);
                                    usuarioJDBC.actualizar(usuarioActualizar, TipoUsuario.CLIENTE.name());
                                    break;
                                }
                                default:
                                {
                                    break;
                                }
                            }
                        }
                        case 4:
                        {
                            scanner.nextLine();
                            System.out.println("------------ACTUALIZAR USUARIO------------");
                            System.out.println(gestorUsuarios.listarString(usuarioJDBC.listarTodos()));
                            System.out.println("Elija un usuario para modificar.");
                            String identificador = scanner.nextLine();
                            System.out.println("Elija su nuevo nombre de usuario: ");
                            String nuevoNombreUsuario = scanner.nextLine();
                            System.out.println("Elija su nueva contraseña: ");
                            String nuevaContrasenia = scanner.nextLine();

                            Usuario usuarioActualizar = gestorUsuarios.buscarPorID(identificador);
                            gestorUsuarios.actualizar(usuarioActualizar, nuevoNombreUsuario);
                            usuarioJDBC.actualizarUsuario(usuarioActualizar, nuevoNombreUsuario, nuevaContrasenia);
                            break;
                        }
                        case 5:
                        {
                            scanner.nextLine();
                            System.out.println("------------ELIMINAR USUARIO------------");
                            System.out.println(gestorUsuarios.listarString(usuarioJDBC.listarTodos()));
                            System.out.println("Elija un usuario para eliminar.");
                            String identificador = scanner.nextLine();

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
                } while (opc <= 6);
            }

            case RECEPCIONISTA ->
            {
                do {
                    System.out.println("Menu: RECEPCIONISTA");
                    System.out.println("1. Realizar Check-Ins.");
                    System.out.println("2. Realizar Check-Outs.");
                    System.out.println("3. Gestionar pasajeros.");
                    System.out.println("4. Gestionar habitaciones.");
                    System.out.println("5. Gestionar reservas.");
                    System.out.println("6. Gestionar ocupaciones");
                    System.out.println("7. Volver atrás.");
                    opc = scanner.nextInt();

                    switch (opc)
                    {
                        case 1:
                        {
                            scanner.nextLine();
                            System.out.println("------------REALIZAR CHECK-INS------------");
                        }
                        case 2:
                        {
                            scanner.nextLine();
                            System.out.println("------------REALIZAR CHECK-OUTS------------");
                        }
                        case 3:
                        {
                            scanner.nextLine();
                            System.out.println("------------GESTIONAR PASAJEROS------------");
                            System.out.println("1. Cargar pasajero.");
                            System.out.println("2. Buscar pasajero.");
                            System.out.println("3. Actualizar pasajero.");
                            System.out.println("4. Listar pasajeros.");
                            System.out.println("5. Listar alojados.");
                            System.out.println("6. Eliminar pasajero.");
                            System.out.println("7. Volver atrás.");
                        }
                        case 4:
                        {
                            scanner.nextLine();
                            System.out.println("------------GESTIONAR HABITACIONES------------");
                            System.out.println("1. Cargar habitación.");
                            System.out.println("2. Buscar habitación.");
                            System.out.println("3. Actualizar habitación.");
                            System.out.println("4. Listar habitaciones.");
                            System.out.println("5. Listar disponibles.");
                            System.out.println("6. Listar no disponibles.");
                            System.out.println("7. Eliminar habitación.");
                        }
                        case 5:
                        {
                            scanner.nextLine();
                            System.out.println("------------GESTIONAR RESERVAS------------");
                            System.out.println("1. Cargar reserva.");
                            System.out.println("2. Buscar reserva.");
                            System.out.println("3. Actualizar reserva.");
                            System.out.println("4. Listar reservas.");
                            System.out.println("5. Eliminar reserva.");
                        }
                        case 6:
                        {
                            scanner.nextLine();
                            System.out.println("------------GESTIONAR OCUPACIONES------------");
                            System.out.println("1. Cargar ocupación.");
                            System.out.println("2. Buscar ocupación.");
                            System.out.println("3. Actualizar ocupación.");
                            System.out.println("4. Listar ocupaciones.");
                            System.out.println("5. Listar por estado.");
                            System.out.println("6. Eliminar ocupación.");
                        }
                    }
                } while (opc <= 7);
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
    } catch (ValidacionException e) {
        throw new RuntimeException(e);
    } catch (IOException e) {
        throw new RuntimeException(e);
    } catch (InterruptedException e) {
        throw new RuntimeException(e);
    }
}
}