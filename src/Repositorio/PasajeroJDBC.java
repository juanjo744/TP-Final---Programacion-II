package Repositorio;

import Connection.DBConnection;
import Enums.EstadoHabitacion;
import Exceptions.JDBCException;
import Interfaces.IHotelJDBC;
import Models.Cliente;
import Models.Habitacion;
import Models.Pasajero;
import Models.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PasajeroJDBC implements IHotelJDBC<Pasajero, String> {
    private static String sqlPasajero = "CREATE TABLE IF NOT EXISTS Pasajero (" +
            "DNI VARCHAR(15) PRIMARY KEY, " +
            "nombre VARCHAR(100) NOT NULL, " +
            "origen VARCHAR(60) NOT NULL, " +
            "domicilio VARCHAR(150) NOT NULL, " +
            "cliente VARCHAR(50) NOT NULL, " +
            "FOREIGN KEY (cliente) REFERENCES Usuario(nombreUsuario)" +
            ");";

    @Override
    public void crearTabla() throws JDBCException {
        DBConnection db = new DBConnection();

        try (Connection conn = db.crearConexion();
             PreparedStatement ps = conn.prepareStatement(sqlPasajero)) {

            ps.executeUpdate();

        } catch (SQLException ex) {
            throw new JDBCException("Error al crear la tabla de pasajeros.", ex);
        }
    }

    @Override
    public void guardar(Pasajero pasajero) throws JDBCException{
        DBConnection db = new DBConnection();

        try (Connection conn = db.crearConexion();
             PreparedStatement ps = conn.prepareStatement("INSERT INTO Pasajero VALUES (?,?,?,?,?)")) {

            ps.setString(1, pasajero.getDNI());
            ps.setString(2, pasajero.getNombre());
            ps.setString(3, pasajero.getOrigen());
            ps.setString(4, pasajero.getDomicilio());
            ps.setString(5, pasajero.getCliente().getNombreUsuario());

            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new JDBCException("Error al guardar el pasajero.", ex);
        }
    }

    @Override
    public Pasajero buscarPorID(String DNI) throws JDBCException {
        DBConnection db = new DBConnection();
        Pasajero pasajeroSolicitado;
        UsuarioJDBC usuarioJDBC = new UsuarioJDBC();

        try (Connection conn = db.crearConexion();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM Pasajero WHERE DNI = ?");

        ) {
            ps.setString(1, DNI);
            try (ResultSet rs = ps.executeQuery()) {
                if(rs.next()) {
                    pasajeroSolicitado = new Pasajero(rs.getString("DNI"),
                            rs.getString("nombre"),
                            rs.getString("origen"),
                            rs.getString("domicilio"),
                            (Cliente) usuarioJDBC.buscarPorID(rs.getString("cliente")));

                    return pasajeroSolicitado;
                } else
                    return null;
            }
        } catch (SQLException ex) {
            throw new JDBCException("Error al buscar el pasajero.", ex);
        }
    }

    @Override
    public List<Pasajero> listarTodos() throws JDBCException {
        List<Pasajero> pasajeros = new ArrayList<>();
        UsuarioJDBC usuarioJDBC = new UsuarioJDBC();

        String sql = "SELECT * FROM Pasajero";
        DBConnection db = new DBConnection();

        try (Connection conn = db.crearConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Pasajero pasajero = new Pasajero(rs.getString("DNI"),
                        rs.getString("nombre"),
                        rs.getString("origen"),
                        rs.getString("domicilio"),
                        (Cliente) usuarioJDBC.buscarPorID(rs.getString("cliente")));

                pasajeros.add(pasajero);
            }

            return pasajeros;

        } catch (SQLException ex) {
            throw new JDBCException("No se pudieron listar los pasajeros.");
        }
    }

    @Override
    public void actualizar(Pasajero pasajero, String DNI) throws JDBCException {

        String sql = """
            UPDATE Pasajero
            SET nombre = ?, origen = ?, domicilio = ?, cliente = ?
            WHERE DNI = ?
            """;

        DBConnection db = new DBConnection();

        try (Connection conn = db.crearConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, pasajero.getNombre());
            ps.setString(2, pasajero.getOrigen());
            ps.setString(3, pasajero.getDomicilio());
            ps.setString(4, pasajero.getCliente().getNombreUsuario());
            ps.setString(5, DNI);

            ps.executeUpdate();

        } catch (SQLException ex) {
            throw new JDBCException("No se pudo actualizar el pasajero.", ex);
        }
    }

    @Override
    public void eliminar(String DNI) throws JDBCException {
        String sql = "DELETE FROM Pasajero WHERE DNI = ?";

        DBConnection db = new DBConnection();

        try (Connection conn = db.crearConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, DNI);
            ps.executeUpdate();

        } catch (SQLException ex) {
            throw new JDBCException("No se pudo eliminar el Pasajero.", ex);
        }
    }
}