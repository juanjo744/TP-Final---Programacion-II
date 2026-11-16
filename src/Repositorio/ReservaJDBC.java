package Repositorio;

import Connection.DBConnection;
import Exceptions.JDBCException;
import Interfaces.IHotelJDBC;
import Models.Reserva;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class ReservaJDBC implements IHotelJDBC<Reserva, Integer> {
    private static String sqlReserva = "CREATE TABLE IF NOT EXISTS Reserva (" +
            "id INT PRIMARY KEY AUTO_INCREMENT, " +
            "habitacionSolicitada INT NOT NULL, " +
            "titular VARCHAR(50) NOT NULL, " +
            "cantidadPasajeros INT NOT NULL, " +
            "fechaInicio DATE NOT NULL, " +
            "fechaFin DATE NOT NULL, " +
            "estado VARCHAR(30) NOT NULL, " +
            "FOREIGN KEY (habitacionSolicitada) REFERENCES Habitacion(numero), " +
            "FOREIGN KEY (titular) REFERENCES Cliente(nombreUsuario)" +
            ")";

    @Override
    public void crearTabla() throws JDBCException {
        DBConnection db = new DBConnection();

        try (Connection conn = db.crearConexion();
             PreparedStatement ps = conn.prepareStatement(sqlReserva)) {

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new JDBCException("Error al crear la tabla.");
        }
    }

    @Override
    public void guardar(Reserva elemento) {

    }

    @Override
    public Reserva buscarPorID(Integer integer) {
        return null;
    }

    @Override
    public List<Reserva> listarTodos() {
        return List.of();
    }

    @Override
    public void actualizar(Reserva elemento, Integer integer) {

    }

    @Override
    public void eliminar(Integer integer) {

    }
}
