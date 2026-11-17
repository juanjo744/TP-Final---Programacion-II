package Repositorio;

import Connection.DBConnection;
import Enums.EstadoHabitacion;
import Enums.EstadoReserva;
import Exceptions.JDBCException;
import Interfaces.IHotelJDBC;
import Models.Cliente;
import Models.Habitacion;
import Models.Pasajero;
import Models.Reserva;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
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
            "FOREIGN KEY (titular) REFERENCES Usuario(nombreUsuario)" +
            ")";
    private static String sqlReservaPasajero = "CREATE TABLE IF NOT EXISTS ReservaPasajero (" +
            "reservaId INT NOT NULL," +
            "pasajeroDni VARCHAR(15) NOT NULL," +
            "PRIMARY KEY (reservaId, pasajeroDni)," +
            "FOREIGN KEY (reservaId) REFERENCES Reserva(id)," +
            "FOREIGN KEY (pasajeroDni) REFERENCES Pasajero(DNI)" +
            ");";

    @Override
    public void crearTabla() throws JDBCException {
        DBConnection db = new DBConnection();

        try (Connection conn = db.crearConexion();
             PreparedStatement ps = conn.prepareStatement(sqlReserva)) {

            ps.executeUpdate();
            try(PreparedStatement relacion = conn.prepareStatement(sqlReservaPasajero)){
                relacion.executeUpdate();
            }

        } catch (SQLException e) {
            throw new JDBCException("Error al crear la tabla.");
        }
    }

    @Override
    public void guardar(Reserva reserva) throws JDBCException {
        if (reserva.getId() != 0) {
            throw new JDBCException("La reserva ya tiene un ID asignado.");
        }

        String sqlGuardarReserva = """
            INSERT INTO Reserva
            (habitacionSolicitada, titular, cantidadPasajeros,
             fechaInicio, fechaFin, estado)
            VALUES (?, ?, ?, ?, ?, ?)
            """;

        String sqlGuardarPasajero = """
            INSERT INTO ReservaPasajero (reservaId, pasajeroDni)
            VALUES (?, ?)
            """;

        DBConnection db = new DBConnection();

        try (Connection conn = db.crearConexion()) {
            conn.setAutoCommit(false);

            try {
                int idGenerado;

                try (PreparedStatement consulta = conn.prepareStatement(
                        sqlGuardarReserva, Statement.RETURN_GENERATED_KEYS)) {

                    consulta.setInt(
                            1, reserva.getHabitacionSolicitada().getNumero());
                    consulta.setString(
                            2, reserva.getTitular().getNombreUsuario());
                    consulta.setInt(3, reserva.getPasajeros().size());
                    consulta.setDate(
                            4, java.sql.Date.valueOf(reserva.getFechaInicio()));
                    consulta.setDate(
                            5, java.sql.Date.valueOf(reserva.getFechaFin()));
                    consulta.setString(6, reserva.getEstado().name());

                    if (consulta.executeUpdate() != 1) {
                        throw new SQLException("No se insertó la reserva.");
                    }

                    try (ResultSet claves = consulta.getGeneratedKeys()) {
                        if (!claves.next()) {
                            throw new SQLException("No se pudo recuperar el ID de la reserva.");
                        }

                        idGenerado = claves.getInt(1);
                    }
                }

                try (PreparedStatement consulta =
                             conn.prepareStatement(sqlGuardarPasajero)) {

                    for (Pasajero pasajero : reserva.getPasajeros()) {
                        consulta.setInt(1, idGenerado);
                        consulta.setString(2, pasajero.getDNI());
                        consulta.executeUpdate();
                    }
                }

                conn.commit();
                reserva.asignarId(idGenerado);

            } catch (SQLException | RuntimeException ex) {
                try {
                    conn.rollback();
                } catch (SQLException errorRollback) {
                    ex.addSuppressed(errorRollback);
                }

                throw ex;
            }

        } catch (SQLException ex) {
            throw new JDBCException("No se pudo guardar la reserva y sus pasajeros.", ex);
        }
    }

    @Override
    public Reserva buscarPorID(Integer id) throws JDBCException {
        String sql = "SELECT * FROM Reserva WHERE id = ?";

        String sqlPasajeros = """
            SELECT pasajeroDni
            FROM ReservaPasajero
            WHERE reservaId = ?
            """;

        DBConnection db = new DBConnection();

        try (Connection conn = db.crearConexion();
             PreparedStatement consulta = conn.prepareStatement(sql)) {

            consulta.setInt(1, id);

            try (ResultSet resultado = consulta.executeQuery()) {
                if (!resultado.next()) {
                    return null;
                }

                HabitacionJDBC habitacionJDBC = new HabitacionJDBC();
                Habitacion habitacion = habitacionJDBC.buscarPorID(
                        resultado.getInt("habitacionSolicitada"));

                if (habitacion == null) {
                    throw new JDBCException("No se encontró la habitación de la reserva.");
                }

                UsuarioJDBC usuarioJDBC = new UsuarioJDBC();
                Object usuario = usuarioJDBC.buscarPorID(
                        resultado.getString("titular"));

                if (!(usuario instanceof Cliente)) {
                    throw new JDBCException("No se pudo recuperar el cliente titular.");
                }

                Cliente titular = (Cliente) usuario;

                ArrayList<Pasajero> pasajeros = new ArrayList<>();
                PasajeroJDBC pasajeroJDBC = new PasajeroJDBC();

                try (PreparedStatement consultaPasajeros =
                             conn.prepareStatement(sqlPasajeros)) {

                    consultaPasajeros.setInt(1, id);

                    try (ResultSet filas = consultaPasajeros.executeQuery()) {
                        while (filas.next()) {
                            Pasajero pasajero = pasajeroJDBC.buscarPorID(
                                    filas.getString("pasajeroDni"));

                            if (pasajero == null) {
                                throw new JDBCException("No se pudo recuperar un pasajero de la reserva.");
                            }

                            pasajeros.add(pasajero);
                        }
                    }
                }

                return new Reserva(
                        resultado.getInt("id"),
                        habitacion,
                        titular,
                        pasajeros,
                        resultado.getDate("fechaInicio").toLocalDate(),
                        resultado.getDate("fechaFin").toLocalDate(),
                        EstadoReserva.valueOf(resultado.getString("estado"))
                );
            }

        } catch (SQLException ex) {
            throw new JDBCException("No se pudo buscar la reserva.", ex);
        }
    }

    @Override
    public List<Reserva> listarTodos() throws JDBCException {
        List<Reserva> reservas = new ArrayList<>();
        List<Integer> ids = new ArrayList<>();

        String sql = "SELECT id FROM Reserva ORDER BY id";
        DBConnection db = new DBConnection();

        try (Connection conn = db.crearConexion();
             PreparedStatement consulta = conn.prepareStatement(sql);
             ResultSet resultado = consulta.executeQuery()) {

            while (resultado.next()) {
                ids.add(resultado.getInt("id"));
            }

        } catch (SQLException ex) {
            throw new JDBCException("No se pudieron listar las reservas.", ex);
        }

        for (Integer id : ids) {
            Reserva reserva = buscarPorID(id);

            if (reserva != null) {
                reservas.add(reserva);
            }
        }

        return reservas;
    }

    @Override
    public void actualizar(Reserva reserva, Integer id)
            throws JDBCException {

        String sqlActualizar = """
            UPDATE Reserva
            SET habitacionSolicitada = ?, titular = ?,
                cantidadPasajeros = ?, fechaInicio = ?,
                fechaFin = ?, estado = ?
            WHERE id = ?
            """;

        String sqlBorrarPasajeros = """
            DELETE FROM ReservaPasajero
            WHERE reservaId = ?
            """;

        String sqlAgregarPasajero = """
            INSERT INTO ReservaPasajero (reservaId, pasajeroDni)
            VALUES (?, ?)
            """;

        DBConnection db = new DBConnection();

        try (Connection conn = db.crearConexion()) {
            conn.setAutoCommit(false);

            try {
                // Verificar que existe y bloquearla durante el cambio.
                try (PreparedStatement consulta = conn.prepareStatement(
                        "SELECT id FROM Reserva WHERE id = ? FOR UPDATE")) {

                    consulta.setInt(1, id);

                    try (ResultSet resultado = consulta.executeQuery()) {
                        if (!resultado.next()) {
                            throw new SQLException("No existe una reserva con ese ID.");
                        }
                    }
                }

                try (PreparedStatement consulta =
                             conn.prepareStatement(sqlActualizar)) {

                    consulta.setInt(
                            1, reserva.getHabitacionSolicitada().getNumero());
                    consulta.setString(
                            2, reserva.getTitular().getNombreUsuario());
                    consulta.setInt(3, reserva.getPasajeros().size());
                    consulta.setDate(
                            4, java.sql.Date.valueOf(reserva.getFechaInicio()));
                    consulta.setDate(
                            5, java.sql.Date.valueOf(reserva.getFechaFin()));
                    consulta.setString(6, reserva.getEstado().name());
                    consulta.setInt(7, id);

                    consulta.executeUpdate();
                }

                // Quitar las relaciones anteriores.
                try (PreparedStatement consulta =
                             conn.prepareStatement(sqlBorrarPasajeros)) {

                    consulta.setInt(1, id);
                    consulta.executeUpdate();
                }

                // Agregar las relaciones con la lista actual.
                try (PreparedStatement consulta =
                             conn.prepareStatement(sqlAgregarPasajero)) {

                    for (Pasajero pasajero : reserva.getPasajeros()) {
                        consulta.setInt(1, id);
                        consulta.setString(2, pasajero.getDNI());
                        consulta.executeUpdate();
                    }
                }

                conn.commit();

            } catch (SQLException | RuntimeException ex) {
                try {
                    conn.rollback();
                } catch (SQLException errorRollback) {
                    ex.addSuppressed(errorRollback);
                }

                throw ex;
            }

        } catch (SQLException ex) {
            throw new JDBCException("No se pudo actualizar la reserva y sus pasajeros.", ex);
        }
    }

    @Override
    public void eliminar(Integer id) throws JDBCException {
        String sqlPasajeros =
                "DELETE FROM ReservaPasajero WHERE reservaId = ?";

        String sqlReserva =
                "DELETE FROM Reserva WHERE id = ?";

        DBConnection db = new DBConnection();

        try (Connection conn = db.crearConexion()) {
            conn.setAutoCommit(false);

            try {
                try (PreparedStatement consulta =
                             conn.prepareStatement(sqlPasajeros)) {

                    consulta.setInt(1, id);
                    consulta.executeUpdate();
                }

                try (PreparedStatement consulta =
                             conn.prepareStatement(sqlReserva)) {

                    consulta.setInt(1, id);

                    if (consulta.executeUpdate() != 1) {
                        throw new SQLException("No existe una reserva con ese ID.");
                    }
                }

                conn.commit();

            } catch (SQLException | RuntimeException ex) {
                try {
                    conn.rollback();
                } catch (SQLException errorRollback) {
                    ex.addSuppressed(errorRollback);
                }

                throw ex;
            }

        } catch (SQLException ex) {
            throw new JDBCException("No se pudo eliminar la reserva.", ex);
        }
    }

    public boolean tieneReservaEnPeriodo(int numeroHabitacion, LocalDate fechaInicio, LocalDate fechaFin, Integer idExcluir) throws JDBCException {

        String sql = """
            SELECT id
            FROM Reserva
            WHERE habitacionSolicitada = ?
              AND estado IN ('PENDIENTE', 'CONFIRMADA', 'EN_CURSO')
              AND fechaInicio < ?
              AND fechaFin > ?
            """;

        if (idExcluir != null) {
            sql += " AND id <> ?";
        }

        sql += " LIMIT 1";

        DBConnection db = new DBConnection();

        try (Connection conn = db.crearConexion();
             PreparedStatement consulta = conn.prepareStatement(sql)) {

            consulta.setInt(1, numeroHabitacion);
            consulta.setDate(2, java.sql.Date.valueOf(fechaFin));
            consulta.setDate(3, java.sql.Date.valueOf(fechaInicio));

            if (idExcluir != null) {
                consulta.setInt(4, idExcluir);
            }

            try (ResultSet resultado = consulta.executeQuery()) {
                return resultado.next();
            }

        } catch (SQLException ex) {
            throw new JDBCException("No se pudo comprobar la disponibilidad de reservas.", ex);
        }
    }
}
