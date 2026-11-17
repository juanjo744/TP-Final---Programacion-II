package Repositorio;

import Connection.DBConnection;
import Enums.EstadoHabitacion;
import Exceptions.JDBCException;
import Interfaces.IHotelJDBC;
import Models.Habitacion;
import Models.Reserva;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
    public void guardar(Reserva reserva) throws JDBCException{
        DBConnection db = new DBConnection();

        try (Connection conn = db.crearConexion();
             PreparedStatement ps = conn.prepareStatement("INSERT INTO Habitacion VALUES (?,?,?,?,?,?)")) {

            ps.setInt(1, reserva.getId());
            ps.setInt(2, reserva.getHabitacionSolicitada().getNumero());
            ps.setString(3, reserva.getTitular().getNombreUsuario());
            ps.setInt(4, reserva.getPasajeros().size());
            ps.setDate(5, java.sql.Date.valueOf(reserva.getFechaInicio()));
            ps.setDate(6, java.sql.Date.valueOf(reserva.getFechaFin()));
            ps.setString(7, reserva.getEstado().name());

            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new JDBCException("Error al guardar la reserva.", ex);
        }
    }

    @Override
    public Reserva buscarPorID(Integer id) throws JDBCException {
        DBConnection db = new DBConnection();
        Reserva reservaBuscada = null;

        try (Connection conn = db.crearConexion();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM Habitacion WHERE numero = ?");

        ) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if(rs.next()) {
                    reservaBuscada = new Reserva(rs.getInt("id"),
                            rs.getInt("habitacionSolicitada"),
                            rs.getDouble("titular"),
                            EstadoHabitacion.valueOf(rs.getString("estado")));

                    return reservaBuscada;
                } else
                    return null;
            }
        } catch (SQLException ex) {
            throw new JDBCException("Error al buscar la habitación.", ex);
        }
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
