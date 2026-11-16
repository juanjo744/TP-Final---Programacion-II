package Repositorio;

import Connection.DBConnection;
import Exceptions.JDBCException;
import Interfaces.IHotelJDBC;
import Models.Pasajero;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class PasajeroJDBC implements IHotelJDBC<Pasajero, String> {
    private static String sqlPasajero = "CREATE TABLE IF NOT EXISTS Pasajero (" +
            "DNI VARCHAR(15) PRIMARY KEY, " +
            "nombre VARCHAR(100) NOT NULL, " +
            "origen VARCHAR(60) NOT NULL, " +
            "domicilio VARCHAR(150) NOT NULL, " +
            "cliente VARCHAR(50) NOT NULL, " +
            "FOREIGN KEY (cliente) REFERENCES Cliente(nombreUsuario)" +
            ")";

    @Override
    public void crearTabla() throws JDBCException {
        DBConnection db = new DBConnection();

        try (Connection conn = db.crearConexion();
             PreparedStatement ps = conn.prepareStatement(sqlPasajero)) {

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new JDBCException("Error al crear la tabla.");
        }
    }

    @Override
    public void guardar(Pasajero elemento) {

    }

    @Override
    public Pasajero buscarPorID(String s) {
        return null;
    }

    @Override
    public List<Pasajero> listarTodos() {
        return List.of();
    }

    @Override
    public void actualizar(Pasajero elemento, String s) {

    }

    @Override
    public void eliminar(String s) {

    }
}
