package Repositorio;

import Enums.EstadoHabitacion;
import Exceptions.JDBCException;
import Interfaces.IHotelJDBC;
import Models.Habitacion;

import java.sql.*;
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
    public void guardar(Habitacion habitacion) throws JDBCException{
        DBConnection db = new DBConnection();

        try (Connection conn = db.crearConexion();
             PreparedStatement ps = conn.prepareStatement("INSERT INTO Habitacion VALUES (?,?,?,?)")) {

            ps.setInt(1, habitacion.getNumero());
            ps.setInt(2, habitacion.getCapacidad());
            ps.setDouble(3, habitacion.getPrecioPorNoche());
            ps.setString(4, habitacion.getEstado().name());

            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new JDBCException("Error al guardar la habitación." + ex);
        }
    }

    @Override
    public Habitacion buscarPorID(Integer numero) throws JDBCException {
        DBConnection db = new DBConnection();
        Habitacion habitacionBuscada = null;

        try (Connection conn = db.crearConexion();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM Habitacion WHERE numero = ?");

             ) {
            ps.setInt(1, numero);
            try (ResultSet rs = ps.executeQuery()) {
                if(rs.next()) {
                    habitacionBuscada = new Habitacion(rs.getInt("numero"),
                            rs.getInt("capacidad"),
                            rs.getDouble("precioPorNoche"),
                            EstadoHabitacion.valueOf(rs.getString("estado")));

                    return habitacionBuscada;
                } else
                    throw new JDBCException("La habitación solicitada no existe.");
            }
        } catch (SQLException ex) {
            throw new JDBCException("Error al buscar la habitación.", ex);
        }
    }

    @Override
    public List<Habitacion> listarTodos() {
        return List.of();
    }

    @Override
    public void actualizar(Habitacion habitacion, Integer numero) {

    }

    @Override
    public void eliminar(Integer numero) {

    }
}
