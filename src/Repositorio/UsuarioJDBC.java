package Repositorio;

import Connection.DBConnection;
import Enums.TipoUsuario;
import Exceptions.JDBCException;
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
            ")";

    @Override
    public void crearTabla() throws JDBCException {
        DBConnection db = new DBConnection();

        try (Connection conn = db.crearConexion();
             PreparedStatement ps = conn.prepareStatement(sqlUsuario)) {

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new JDBCException("Error al crear la tabla.");
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
                            return new Administrador(nombre, null, TipoUsuario.ADMINISTRADOR, gestorUsuarios);

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
    @Override
    public void actualizar(Usuario usuario, String nombreUsuario) throws JDBCException {

        String sql = """
            UPDATE Usuario
            SET contrasenia = ?
            WHERE nombreUsuario = ?
            """;

        DBConnection db = new DBConnection();

        try (Connection conn = db.crearConexion();
             PreparedStatement consulta = conn.prepareStatement(sql)) {

            consulta.setInt(1, usuario.obtenerHashContrasenia());
            consulta.setString(2, nombreUsuario);

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
}
