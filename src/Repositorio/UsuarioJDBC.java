package Repositorio;

import Connection.DBConnection;
import Exceptions.JDBCException;
import Interfaces.IHotelJDBC;
import Models.Cliente;
import Models.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class UsuarioJDBC implements IHotelJDBC {
    private static String sqlUsuario = "CREATE TABLE IF NOT EXISTS Usuario (" +
            "nombreUsuario VARCHAR(50) PRIMARY KEY, " +
            "contrasenia VARCHAR(255) NOT NULL, " +
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
    public void guardar(Object elemento) {

    }

    @Override
    public Object buscarPorID(Object object) {
        return null;
    }

    public Cliente buscarClientePorNombreUsuario(String nombreUsuario) {
        return null;
    }

    @Override
    public List listarTodos() {
        return List.of();
    }

    @Override
    public void actualizar(Object elemento, Object object) {

    }

    @Override
    public void eliminar(Object object) {

    }
}
