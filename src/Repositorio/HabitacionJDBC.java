package Repositorio;

import Exceptions.JDBCException;
import Interfaces.IHotelJDBC;
import Models.Habitacion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import Connection.DBConnection;

public class HabitacionJDBC implements IHotelJDBC<Habitacion, Integer> {

    private static String sqlHabitacion = "CREATE TABLE IF NOT EXISTS Habitacion (" +
            "numero INT PRIMARY KEY, " +
            "capacidad INT NOT NULL, " +
            "precioPorNoche DOUBLE NOT NULL, " +
            "estado VARCHAR(30) NOT NULL" +
            ")";

    @Override
    public void crearTabla() throws JDBCException{
        DBConnection db = new DBConnection();

        try (Connection conn = db.crearConexion();
             PreparedStatement ps = conn.prepareStatement(sqlHabitacion)) {

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new JDBCException("Error al crear la tabla.");
        }
    }

    @Override
    public void guardar(Habitacion elemento) {

    }

    @Override
    public Habitacion buscarPorID(Integer integer) {
        return null;
    }

    @Override
    public List<Habitacion> listarTodos() {
        return List.of();
    }

    @Override
    public void actualizar(Habitacion elemento, Integer integer) {

    }

    @Override
    public void eliminar(Integer integer) {

    }
}
