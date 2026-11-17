package Repositorio;

import Connection.DBConnection;
import Exceptions.JDBCException;
import Interfaces.IHotelJDBC;
import Models.Habitacion;
import Models.Ocupacion;
import Models.Pasajero;

import java.sql.*;
import java.util.ArrayList;
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

    private static String sqlOcupacionPasajero = """
            CREATE TABLE IF NOT EXISTS OcupacionPasajero (
                ocupacionId INT NOT NULL,
                pasajeroDni VARCHAR(15) NOT NULL,
                PRIMARY KEY (ocupacionId, pasajeroDni),
                FOREIGN KEY (ocupacionId) REFERENCES Ocupacion(id),
                FOREIGN KEY (pasajeroDni) REFERENCES Pasajero(DNI)
            )
            """;
    @Override
    public void crearTabla() throws JDBCException {
        DBConnection db = new DBConnection();

        try (Connection conn = db.crearConexion();
             PreparedStatement ps = conn.prepareStatement(sqlOcupacion)) {

            ps.executeUpdate();

            try (PreparedStatement relacion = conn.prepareStatement(sqlOcupacionPasajero)) {
                relacion.executeUpdate();
            }

        } catch (SQLException e) {
            throw new JDBCException("Error al crear la tabla.");
        }
    }

    @Override
    public void guardar(Ocupacion ocupacion) throws JDBCException {
        String sqlOcupacion = """
            INSERT INTO Ocupacion
            (habitacionReservada, cantidadPasajeros,
             fechaCheckIn, fechaCheckOut, activa)
            VALUES (?, ?, ?, ?, ?)
            """;

        String sqlPasajero = """
            INSERT INTO OcupacionPasajero (ocupacionId, pasajeroDni)
            VALUES (?, ?)
            """;

        DBConnection db = new DBConnection();

        try (Connection conn = db.crearConexion()) {
            conn.setAutoCommit(false);

            try {
                int idGenerado;

                try (PreparedStatement consulta = conn.prepareStatement(
                        sqlOcupacion, Statement.RETURN_GENERATED_KEYS)) {

                    consulta.setInt(
                            1, ocupacion.getHabitacionReservada().getNumero());
                    consulta.setInt(2, ocupacion.getPasajeros().size());
                    consulta.setDate(
                            3, java.sql.Date.valueOf(ocupacion.getFechaCheckIn()));
                    consulta.setDate(
                            4, java.sql.Date.valueOf(ocupacion.getFechaCheckOut()));
                    consulta.setBoolean(5, ocupacion.isActiva());

                    if (consulta.executeUpdate() != 1) {
                        throw new SQLException("No se insertó la ocupación.");
                    }

                    try (ResultSet claves = consulta.getGeneratedKeys()) {
                        if (!claves.next()) {
                            throw new SQLException(
                                    "No se pudo recuperar el ID de la ocupación.");
                        }

                        idGenerado = claves.getInt(1);
                    }
                }

                try (PreparedStatement consulta =
                             conn.prepareStatement(sqlPasajero)) {

                    for (Pasajero pasajero : ocupacion.getPasajeros()) {
                        consulta.setInt(1, idGenerado);
                        consulta.setString(2, pasajero.getDNI());
                        consulta.executeUpdate();
                    }
                }

                conn.commit();
                ocupacion.asignarId(idGenerado);

            } catch (SQLException | RuntimeException ex) {
                try {
                    conn.rollback();
                } catch (SQLException errorRollback) {
                    ex.addSuppressed(errorRollback);
                }

                throw ex;
            }

        } catch (SQLException ex) {
            throw new JDBCException(
                    "No se pudo guardar la ocupación y sus pasajeros.", ex);
        }
    }

    @Override
    public Ocupacion buscarPorID(Integer id) throws JDBCException {
        String sql = "SELECT * FROM Ocupacion WHERE id = ?";

        String sqlPasajeros = """
            SELECT pasajeroDni
            FROM OcupacionPasajero
            WHERE ocupacionId = ?
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
                        resultado.getInt("habitacionReservada"));

                if (habitacion == null) {
                    throw new JDBCException(
                            "No se encontró la habitación de la ocupación.");
                }

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
                                throw new JDBCException(
                                        "No se pudo recuperar un pasajero de la ocupación.");
                            }

                            pasajeros.add(pasajero);
                        }
                    }
                }

                return new Ocupacion(
                        habitacion,
                        pasajeros,
                        resultado.getDate("fechaCheckIn").toLocalDate(),
                        resultado.getDate("fechaCheckOut").toLocalDate(),
                        resultado.getBoolean("activa"),
                        resultado.getInt("id")
                );
            }

        } catch (SQLException ex) {
            throw new JDBCException(
                    "No se pudo buscar la ocupación.", ex);
        }
    }

    @Override
    public List<Ocupacion> listarTodos() throws JDBCException {
        List<Ocupacion> ocupaciones = new ArrayList<>();
        List<Integer> ids = new ArrayList<>();

        String sql = "SELECT id FROM Ocupacion ORDER BY id";
        DBConnection db = new DBConnection();

        try (Connection conn = db.crearConexion();
             PreparedStatement consulta = conn.prepareStatement(sql);
             ResultSet resultado = consulta.executeQuery()) {

            while (resultado.next()) {
                ids.add(resultado.getInt("id"));
            }

        } catch (SQLException ex) {
            throw new JDBCException(
                    "No se pudieron listar las ocupaciones.", ex);
        }

        for (Integer id : ids) {
            Ocupacion ocupacion = buscarPorID(id);

            if (ocupacion != null) {
                ocupaciones.add(ocupacion);
            }
        }

        return ocupaciones;
    }

    @Override
    public void actualizar(Ocupacion ocupacion, Integer id)
            throws JDBCException {

        String sqlActualizar = """
            UPDATE Ocupacion
            SET habitacionReservada = ?, cantidadPasajeros = ?,
                fechaCheckIn = ?, fechaCheckOut = ?, activa = ?
            WHERE id = ?
            """;

        String sqlBorrarPasajeros = """
            DELETE FROM OcupacionPasajero
            WHERE ocupacionId = ?
            """;

        String sqlAgregarPasajero = """
            INSERT INTO OcupacionPasajero (ocupacionId, pasajeroDni)
            VALUES (?, ?)
            """;

        DBConnection db = new DBConnection();

        try (Connection conn = db.crearConexion()) {
            conn.setAutoCommit(false);

            try {
                // Comprobar que existe y bloquearla durante la actualización.
                try (PreparedStatement consulta = conn.prepareStatement(
                        "SELECT id FROM Ocupacion WHERE id = ? FOR UPDATE")) {

                    consulta.setInt(1, id);

                    try (ResultSet resultado = consulta.executeQuery()) {
                        if (!resultado.next()) {
                            throw new SQLException(
                                    "No existe una ocupación con ese ID.");
                        }
                    }
                }

                try (PreparedStatement consulta =
                             conn.prepareStatement(sqlActualizar)) {

                    consulta.setInt(
                            1, ocupacion.getHabitacionReservada().getNumero());
                    consulta.setInt(2, ocupacion.getPasajeros().size());
                    consulta.setDate(
                            3, java.sql.Date.valueOf(ocupacion.getFechaCheckIn()));
                    consulta.setDate(
                            4, java.sql.Date.valueOf(ocupacion.getFechaCheckOut()));
                    consulta.setBoolean(5, ocupacion.isActiva());
                    consulta.setInt(6, id);

                    consulta.executeUpdate();
                }

                // Quitar las relaciones anteriores, sin borrar los pasajeros.
                try (PreparedStatement consulta =
                             conn.prepareStatement(sqlBorrarPasajeros)) {

                    consulta.setInt(1, id);
                    consulta.executeUpdate();
                }

                // Guardar las relaciones con la lista actual de pasajeros.
                try (PreparedStatement consulta =
                             conn.prepareStatement(sqlAgregarPasajero)) {

                    for (Pasajero pasajero : ocupacion.getPasajeros()) {
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
            throw new JDBCException(
                    "No se pudo actualizar la ocupación y sus pasajeros.", ex);
        }
    }

    @Override
    public void eliminar(Integer id) throws JDBCException {
        String sqlPasajeros =
                "DELETE FROM OcupacionPasajero WHERE ocupacionId = ?";

        String sqlOcupacion =
                "DELETE FROM Ocupacion WHERE id = ?";

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
                             conn.prepareStatement(sqlOcupacion)) {

                    consulta.setInt(1, id);

                    if (consulta.executeUpdate() != 1) {
                        throw new SQLException(
                                "No existe una ocupación con ese ID.");
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
            throw new JDBCException(
                    "No se pudo eliminar la ocupación.", ex);
        }
    }
}
