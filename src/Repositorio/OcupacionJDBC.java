package Repositorio;

import Connection.DBConnection;
import Exceptions.JDBCException;
import Interfaces.IHotelJDBC;
import Models.Ocupacion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class OcupacionJDBC implements IHotelJDBC<Ocupacion, Integer> {
    private static String sqlOcupacion = "CREATE TABLE IF NOT EXISTS Ocupacion (" +
            "id INT PRIMARY KEY AUTO_INCREMENT, " +
            "habitacionReservada INT NOT NULL, " +
            "cantidadPasajeros INT NOT NULL, " +
            "fechaCheckIn DATE NOT NULL, " +
            "fechaCheckOut DATE NOT NULL, " +
            "activa BOOLEAN NOT NULL, " +
            "FOREIGN KEY (habitacionReservada) REFERENCES Habitacion(numero)" +
            ")";

    @Override
    public void crearTabla() throws JDBCException {
        DBConnection db = new DBConnection();

        try (Connection conn = db.crearConexion();
             PreparedStatement ps = conn.prepareStatement(sqlOcupacion)) {

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new JDBCException("Error al crear la tabla.");
        }
    }

    @Override
    public void guardar(Ocupacion elemento) {

    }

    @Override
    public Ocupacion buscarPorID(Integer integer) {
        return null;
    }

    @Override
    public List<Ocupacion> listarTodos() {
        return List.of();
    }

    @Override
    public void actualizar(Ocupacion elemento, Integer integer) {

    }

    @Override
    public void eliminar(Integer integer) {

    }
}
