package Repositorio;

import Connection.DBConnection;
import Enums.TipoUsuario;
import Exceptions.JDBCException;
import Gestor.GestorHabitaciones;
import Gestor.GestorReservas;
import Interfaces.IHotelJDBC;
import Models.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class UsuarioJDBC implements IHotelJDBC<Usuario, String> {
    private GestorReservas gestorReservas;
    private GestorHabitaciones gestorHabitaciones;

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
                            return new Administrador(nombre, null);

                        case RECEPCIONISTA:
                            return new Recepcionista(nombre, null);

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
        return List.of();
    }

    @Override
    public void actualizar(Usuario elemento, String s) throws JDBCException {

    }

    @Override
    public void eliminar(String s) throws JDBCException {

    }
}
