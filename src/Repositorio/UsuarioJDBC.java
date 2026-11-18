package Repositorio;

import Connection.DBConnection;
import Enums.TipoUsuario;
import Exceptions.JDBCException;
import Exceptions.LoginException;
import Gestor.*;
import Interfaces.IHotelJDBC;
import Models.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UsuarioJDBC implements IHotelJDBC<Usuario, String> {
    private GestorReservas gestorReservas;
    private GestorHabitaciones gestorHabitaciones;
    private GestorOcupaciones gestorOcupaciones;
    private GestorPasajeros gestorPasajeros;
    private GestorUsuarios gestorUsuarios;

    private static String sqlUsuario = "CREATE TABLE IF NOT EXISTS Usuario (" +
            "nombreUsuario VARCHAR(50) PRIMARY KEY, " +
            "contrasenia VARCHAR(255) NOT NULL, " +
            "tipoUsuario VARCHAR(30) NOT NULL" +
            ");";

    public void configurarGestores(GestorReservas gestorReservas, GestorHabitaciones gestorHabitaciones, GestorOcupaciones gestorOcupaciones, GestorPasajeros gestorPasajeros, GestorUsuarios gestorUsuarios) {
        this.gestorReservas = gestorReservas;
        this.gestorHabitaciones = gestorHabitaciones;
        this.gestorOcupaciones = gestorOcupaciones;
        this.gestorPasajeros = gestorPasajeros;
        this.gestorUsuarios = gestorUsuarios;
    }

    @Override
    public void crearTabla() throws JDBCException {
        DBConnection db = new DBConnection();

        try (Connection conn = db.crearConexion();
             PreparedStatement ps = conn.prepareStatement(sqlUsuario)) {

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new JDBCException("Error al crear la tabla de usuarios.");
        }
    }

    @Override
    public void guardar(Usuario usuario) throws JDBCException{
        DBConnection db = new DBConnection();

        try (Connection conn = db.crearConexion();
             PreparedStatement ps = conn.prepareStatement("INSERT INTO Usuario VALUES (?,?,?)")) {

            ps.setString(1, usuario.getNombreUsuario());
            ps.setInt(2, usuario.obtenerHashContrasenia());
            ps.setString(3, usuario.getTipo().name());

            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new JDBCException("Error al guardar el usuario.", ex);
        }
    }

    @Override
    public Usuario buscarPorID(String nombreUsuario) throws JDBCException {
        DBConnection db = new DBConnection();

        String sql = "SELECT nombreUsuario, tipoUsuario " +
                "FROM Usuario WHERE nombreUsuario = ?";

        try (Connection conn = db.crearConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nombreUsuario);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    String nombre = rs.getString("nombreUsuario");
                    TipoUsuario tipo = TipoUsuario.valueOf(
                            rs.getString("tipoUsuario")
                    );

                    switch (tipo) {
                        case CLIENTE:
                            return new Cliente(nombre, null, gestorReservas);

                        case ADMINISTRADOR:
                            return new Administrador(nombre, null, gestorUsuarios);

                        case RECEPCIONISTA:
                            return new Recepcionista(nombre, null, gestorHabitaciones, gestorReservas, gestorOcupaciones, gestorPasajeros);

                        default:
                            throw new JDBCException("Tipo de usuario desconocido.");
                    }
                }
            }

        } catch (SQLException ex) {
            throw new JDBCException("Error al buscar el usuario.", ex);
        }

        return null;
    }

    @Override
    public List<Usuario> listarTodos() throws JDBCException {
        List<Usuario> usuarios = new ArrayList<>();
        List<String> nombres = new ArrayList<>();

        String sql = """
            SELECT nombreUsuario
            FROM Usuario
            ORDER BY nombreUsuario
            """;

        DBConnection db = new DBConnection();

        try (Connection conn = db.crearConexion();
             PreparedStatement consulta = conn.prepareStatement(sql);
             ResultSet resultado = consulta.executeQuery()) {

            while (resultado.next()) {
                nombres.add(resultado.getString("nombreUsuario"));
            }

        } catch (SQLException ex) {
            throw new JDBCException("No se pudieron listar los usuarios.", ex);
        }

        for (String nombre : nombres) {
            Usuario usuario = buscarPorID(nombre);

            if (usuario != null) {
                usuarios.add(usuario);
            }
        }

        return usuarios;
    }


    public void actualizarUsuario(Usuario usuario, String nuevoNombreUsuario, String nuevaContrasenia) throws JDBCException {

        String sql = """
            UPDATE Usuario
            SET contrasenia = ?
            SET nombreUsuario = ?
            WHERE nombreUsuario = ?
            """;

        DBConnection db = new DBConnection();

        try (Connection conn = db.crearConexion();
             PreparedStatement consulta = conn.prepareStatement(sql)) {

            consulta.setInt(1, usuario.obtenerHashContrasenia(nuevaContrasenia));
            consulta.setString(2, nuevoNombreUsuario);
            consulta.setString(3, usuario.getNombreUsuario());

            consulta.executeUpdate();

        } catch (SQLException ex) {
            throw new JDBCException("No se pudo actualizar el usuario.", ex);
        }
    }

    @Override
    public void actualizar(Usuario usuario, String nuevoTipo) throws JDBCException {

        String sql = """
            UPDATE Usuario
            SET tipoUsuario = ?
            WHERE nombreUsuario = ?
            """;

        DBConnection db = new DBConnection();

        try (Connection conn = db.crearConexion();
             PreparedStatement consulta = conn.prepareStatement(sql)) {

            consulta.setString(1, nuevoTipo);
            consulta.setString(2, usuario.getNombreUsuario());

            consulta.executeUpdate();

        } catch (SQLException ex) {
            throw new JDBCException("No se pudo actualizar el usuario.", ex);
        }
    }

    @Override
    public void eliminar(String nombreUsuario) throws JDBCException {
        String sql = "DELETE FROM Usuario WHERE nombreUsuario = ?";

        DBConnection db = new DBConnection();

        try (Connection conn = db.crearConexion();
             PreparedStatement consulta = conn.prepareStatement(sql)) {

            consulta.setString(1, nombreUsuario);

            if (consulta.executeUpdate() != 1) {
                throw new SQLException("No existe un usuario con ese nombre.");
            }

        } catch (SQLException ex) {
            throw new JDBCException("No se pudo eliminar el usuario.", ex);
        }
    }

    public Usuario autenticar(String nombreUsuario, String contraseniaIngresada) throws JDBCException, LoginException {

        if (nombreUsuario == null || nombreUsuario.isBlank() || contraseniaIngresada == null || contraseniaIngresada.isBlank()) {
            throw new LoginException();
        }

        String sql = """
            SELECT contrasenia
            FROM Usuario
            WHERE nombreUsuario = ?
            """;

        DBConnection db = new DBConnection();

        try (Connection conn = db.crearConexion();
             PreparedStatement consulta = conn.prepareStatement(sql)) {

            consulta.setString(1, nombreUsuario);

            try (ResultSet resultado = consulta.executeQuery()) {
                if (!resultado.next()) {
                    throw new LoginException();
                }

                int hashGuardado = resultado.getInt("contrasenia");
                int hashIngresado = contraseniaIngresada.hashCode() * 14;

                if (hashGuardado != hashIngresado) {
                    throw new LoginException();
                }
            }

        } catch (SQLException ex) {
            throw new JDBCException("No se pudo comprobar el inicio de sesión.", ex);
        }

        Usuario usuario = buscarPorID(nombreUsuario);

        if (usuario == null) {
            throw new LoginException();
        }

        usuario.iniciarSesion();
        return usuario;


    }
}
