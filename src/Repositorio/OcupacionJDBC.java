package Repositorio;

import Connection.DBConnection;
import Enums.EstadoHabitacion;
import Exceptions.JDBCException;
import Interfaces.IHotelJDBC;
import Models.Habitacion;
import Models.Ocupacion;
import Models.Pasajero;

import java.sql.*;
import java.time.LocalDate;
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
            "reservaId INT NULL,\n" +
            "FOREIGN KEY (reservaId) REFERENCES Reserva(id));";

    private static String sqlOcupacionPasajero = """
            CREATE TABLE IF NOT EXISTS OcupacionPasajero (
                ocupacionId INT NOT NULL,
                pasajeroDni VARCHAR(15) NOT NULL,
                PRIMARY KEY (ocupacionId, pasajeroDni),
                FOREIGN KEY (ocupacionId) REFERENCES Ocupacion(id),
                FOREIGN KEY (pasajeroDni) REFERENCES Pasajero(DNI)
            );
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
            throw new JDBCException("Error al crear la tabla de ocupaciones.");
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
                            throw new SQLException("No se pudo recuperar el ID de la ocupación.");
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
            throw new JDBCException("No se pudo guardar la ocupación y sus pasajeros.", ex);
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
                    throw new JDBCException("No se encontró la habitación de la ocupación.");
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
                                throw new JDBCException("No se pudo recuperar un pasajero de la ocupación.");
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
            throw new JDBCException("No se pudo buscar la ocupación.", ex);
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
            throw new JDBCException("No se pudieron listar las ocupaciones.", ex);
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
                            throw new SQLException("No existe una ocupación con ese ID.");
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
            throw new JDBCException("No se pudo actualizar la ocupación y sus pasajeros.", ex);
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
                        throw new SQLException("No existe una ocupación con ese ID.");
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
            throw new JDBCException("No se pudo eliminar la ocupación.", ex);
        }
    }

    public void registrarCheckIn(Ocupacion ocupacion, Integer reservaId) throws JDBCException {

        if (ocupacion == null || ocupacion.getId() != 0 || reservaId == null || reservaId <= 0) {
            throw new JDBCException("Debe indicar una ocupación nueva y una reserva válida.");
        }

        String sqlReserva = """
            SELECT habitacionSolicitada, fechaInicio, fechaFin, estado
            FROM Reserva WHERE id = ? FOR UPDATE
            """;

        String sqlHabitacion = """
            SELECT capacidad, estado
            FROM Habitacion WHERE numero = ? FOR UPDATE
            """;

        String sqlConflictos = """
            SELECT id FROM Reserva
            WHERE habitacionSolicitada = ?
              AND id <> ?
              AND estado IN ('PENDIENTE', 'CONFIRMADA', 'EN_CURSO')
              AND fechaInicio < ? AND fechaFin > ?
            FOR UPDATE
            """;

        String sqlOcupaciones = """
            SELECT id FROM Ocupacion
            WHERE habitacionReservada = ? AND activa = TRUE
            FOR UPDATE
            """;

        String sqlGuardar = """
            INSERT INTO Ocupacion
            (habitacionReservada, cantidadPasajeros,
             fechaCheckIn, fechaCheckOut, activa, reservaId)
            VALUES (?, ?, ?, ?, TRUE, ?)
            """;

        String sqlPasajeros = """
            INSERT INTO OcupacionPasajero (ocupacionId, pasajeroDni)
            VALUES (?, ?)
            """;

        DBConnection db = new DBConnection();
        int idGenerado;

        try (Connection conn = db.crearConexion()) {
            conn.setAutoCommit(false);

            try {
                int numeroHabitacion;
                LocalDate hoy = LocalDate.now();
                LocalDate fechaFin;


                try (PreparedStatement consulta = conn.prepareStatement(sqlReserva)) {

                    consulta.setInt(1, reservaId);

                    try (ResultSet resultado = consulta.executeQuery()) {
                        if (!resultado.next()) {
                            throw new SQLException("La reserva no existe.");
                        }

                        if (!"CONFIRMADA".equals(resultado.getString("estado"))) {
                            throw new SQLException("La reserva ya no permite realizar el check-in.");
                        }

                        LocalDate fechaInicio = resultado.getDate("fechaInicio").toLocalDate();
                        fechaFin = resultado.getDate("fechaFin").toLocalDate();

                        if (hoy.isBefore(fechaInicio) || !hoy.isBefore(fechaFin)) {
                            throw new SQLException("La fecha de ingreso está fuera de la reserva.");
                        }

                        numeroHabitacion = resultado.getInt("habitacionSolicitada");
                    }
                }

                if (ocupacion.getHabitacionReservada() == null || ocupacion.getHabitacionReservada().getNumero() != numeroHabitacion
                        || !hoy.equals(ocupacion.getFechaCheckIn())
                        || !fechaFin.equals(ocupacion.getFechaCheckOut())
                        || !ocupacion.isActiva()) {
                    throw new SQLException("La ocupación no coincide con los datos del check-in.");
                }

                if (ocupacion.getPasajeros() == null || ocupacion.getPasajeros().isEmpty()) {
                    throw new SQLException("Debe indicar al menos un pasajero.");
                }


                try (PreparedStatement consulta = conn.prepareStatement(sqlHabitacion)) {

                    consulta.setInt(1, numeroHabitacion);

                    try (ResultSet resultado = consulta.executeQuery()) {
                        if (!resultado.next() || !"DISPONIBLE".equals(resultado.getString("estado"))) {
                            throw new SQLException("La habitación no está disponible.");
                        }

                        if (ocupacion.getPasajeros().size() > resultado.getInt("capacidad")) {
                            throw new SQLException("La cantidad de pasajeros supera la capacidad.");
                        }
                    }
                }

                try (PreparedStatement consulta = conn.prepareStatement(sqlOcupaciones)) {

                    consulta.setInt(1, numeroHabitacion);

                    try (ResultSet resultado = consulta.executeQuery()) {
                        if (resultado.next()) {
                            throw new SQLException("La habitación tiene una ocupación activa.");
                        }
                    }
                }


                try (PreparedStatement consulta = conn.prepareStatement(sqlConflictos)) {

                    consulta.setInt(1, numeroHabitacion);
                    consulta.setInt(2, reservaId);
                    consulta.setDate(3, java.sql.Date.valueOf(fechaFin));
                    consulta.setDate(4, java.sql.Date.valueOf(hoy));

                    try (ResultSet resultado = consulta.executeQuery()) {
                        if (resultado.next()) {
                            throw new SQLException("Existe otra reserva para ese período.");
                        }
                    }
                }

                try (PreparedStatement consulta = conn.prepareStatement(
                        sqlGuardar, Statement.RETURN_GENERATED_KEYS)) {

                    consulta.setInt(1, numeroHabitacion);
                    consulta.setInt(2, ocupacion.getPasajeros().size());
                    consulta.setDate(3, java.sql.Date.valueOf(hoy));
                    consulta.setDate(4, java.sql.Date.valueOf(fechaFin));
                    consulta.setInt(5, reservaId);

                    if (consulta.executeUpdate() != 1) {
                        throw new SQLException("No se guardó la ocupación.");
                    }

                    try (ResultSet claves = consulta.getGeneratedKeys()) {
                        if (!claves.next()) {
                            throw new SQLException("No se pudo recuperar el ID de la ocupación.");
                        }

                        idGenerado = claves.getInt(1);
                    }
                }

                try (PreparedStatement consulta = conn.prepareStatement(sqlPasajeros)) {

                    for (Pasajero pasajero : ocupacion.getPasajeros()) {
                        consulta.setInt(1, idGenerado);
                        consulta.setString(2, pasajero.getDNI());
                        consulta.executeUpdate();
                    }
                }

                try (PreparedStatement consulta = conn.prepareStatement(
                        "UPDATE Habitacion SET estado = 'OCUPADA' "
                                + "WHERE numero = ?")) {

                    consulta.setInt(1, numeroHabitacion);

                    if (consulta.executeUpdate() != 1) {
                        throw new SQLException("No se actualizó la habitación.");
                    }
                }

                try (PreparedStatement consulta = conn.prepareStatement(
                        "UPDATE Reserva SET estado = 'EN_CURSO' WHERE id = ?")) {

                    consulta.setInt(1, reservaId);

                    if (consulta.executeUpdate() != 1) {
                        throw new SQLException("No se actualizó la reserva.");
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
            throw new JDBCException("No se pudo registrar el check-in.", ex);
        }

        ocupacion.asignarId(idGenerado);
        ocupacion.getHabitacionReservada().setEstado(EstadoHabitacion.OCUPADA);
    }

    public void registrarCheckInSinReserva(Ocupacion ocupacion) throws JDBCException {

        if (ocupacion == null || ocupacion.getId() != 0 || ocupacion.getHabitacionReservada() == null || ocupacion.getPasajeros() == null || ocupacion.getPasajeros().isEmpty()) {

            throw new JDBCException("Debe indicar una ocupación nueva con habitación y pasajeros.");
        }

        LocalDate hoy = LocalDate.now();

        if (!hoy.equals(ocupacion.getFechaCheckIn()) || ocupacion.getFechaCheckOut() == null || !ocupacion.getFechaCheckOut().isAfter(hoy) || !ocupacion.isActiva()) {

            throw new JDBCException("Las fechas o el estado de la ocupación no son válidos.");
        }

        String sqlHabitacion = """
            SELECT capacidad, estado
            FROM Habitacion WHERE numero = ? FOR UPDATE
            """;

        String sqlReservas = """
            SELECT id FROM Reserva
            WHERE habitacionSolicitada = ?
              AND estado IN ('PENDIENTE', 'CONFIRMADA', 'EN_CURSO')
              AND fechaInicio < ? AND fechaFin > ?
            FOR UPDATE
            """;

        String sqlOcupaciones = """
            SELECT id FROM Ocupacion
            WHERE habitacionReservada = ? AND activa = TRUE
            FOR UPDATE
            """;

        String sqlGuardar = """
            INSERT INTO Ocupacion
            (habitacionReservada, cantidadPasajeros,
             fechaCheckIn, fechaCheckOut, activa)
            VALUES (?, ?, ?, ?, TRUE)
            """;

        String sqlPasajeros = """
            INSERT INTO OcupacionPasajero (ocupacionId, pasajeroDni)
            VALUES (?, ?)
            """;

        String sqlActualizarHabitacion = """
            UPDATE Habitacion SET estado = 'OCUPADA'
            WHERE numero = ?
            """;

        int numeroHabitacion = ocupacion.getHabitacionReservada().getNumero();

        int idGenerado;
        DBConnection db = new DBConnection();

        try (Connection conn = db.crearConexion()) {
            conn.setAutoCommit(false);

            try {

                try (PreparedStatement consulta = conn.prepareStatement(sqlHabitacion)) {

                    consulta.setInt(1, numeroHabitacion);

                    try (ResultSet resultado = consulta.executeQuery()) {
                        if (!resultado.next()) {
                            throw new SQLException("La habitación no existe.");
                        }

                        if (!"DISPONIBLE".equals(resultado.getString("estado"))) {
                            throw new SQLException("La habitación no está disponible.");
                        }

                        if (ocupacion.getPasajeros().size() > resultado.getInt("capacidad")) {
                            throw new SQLException("La cantidad de pasajeros supera la capacidad.");
                        }
                    }
                }

                try (PreparedStatement consulta = conn.prepareStatement(sqlOcupaciones)) {

                    consulta.setInt(1, numeroHabitacion);

                    try (ResultSet resultado = consulta.executeQuery()) {
                        if (resultado.next()) {
                            throw new SQLException("La habitación tiene una ocupación activa.");
                        }
                    }
                }


                try (PreparedStatement consulta = conn.prepareStatement(sqlReservas)) {

                    consulta.setInt(1, numeroHabitacion);
                    consulta.setDate(2, java.sql.Date.valueOf(ocupacion.getFechaCheckOut()));
                    consulta.setDate(3, java.sql.Date.valueOf(ocupacion.getFechaCheckIn()));

                    try (ResultSet resultado = consulta.executeQuery()) {
                        if (resultado.next()) {
                            throw new SQLException("La habitación tiene una reserva para ese período.");
                        }
                    }
                }

                try (PreparedStatement consulta = conn.prepareStatement(
                        sqlGuardar, Statement.RETURN_GENERATED_KEYS)) {

                    consulta.setInt(1, numeroHabitacion);
                    consulta.setInt(2, ocupacion.getPasajeros().size());
                    consulta.setDate(3, java.sql.Date.valueOf(ocupacion.getFechaCheckIn()));
                    consulta.setDate(4, java.sql.Date.valueOf(ocupacion.getFechaCheckOut()));

                    if (consulta.executeUpdate() != 1) {
                        throw new SQLException("No se guardó la ocupación.");
                    }

                    try (ResultSet claves = consulta.getGeneratedKeys()) {
                        if (!claves.next()) {
                            throw new SQLException("No se pudo recuperar el ID de la ocupación.");
                        }

                        idGenerado = claves.getInt(1);
                    }
                }

                try (PreparedStatement consulta = conn.prepareStatement(sqlPasajeros)) {

                    for (Pasajero pasajero : ocupacion.getPasajeros()) {
                        consulta.setInt(1, idGenerado);
                        consulta.setString(2, pasajero.getDNI());
                        consulta.executeUpdate();
                    }
                }

                try (PreparedStatement consulta = conn.prepareStatement(sqlActualizarHabitacion)) {

                    consulta.setInt(1, numeroHabitacion);

                    if (consulta.executeUpdate() != 1) {
                        throw new SQLException("No se actualizó la habitación.");
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
            throw new JDBCException("No se pudo registrar el check-in sin reserva.", ex);
        }

        ocupacion.asignarId(idGenerado);
        ocupacion.getHabitacionReservada().setEstado(EstadoHabitacion.OCUPADA);
    }

    public void registrarCheckOut(Integer id, LocalDate fechaSalida) throws JDBCException {

        if (id == null || id <= 0 || fechaSalida == null) {
            throw new JDBCException("Debe indicar una ocupación y una fecha de salida válidas.");
        }

        String sqlBuscar = """
            SELECT habitacionReservada, reservaId, fechaCheckIn, activa
            FROM Ocupacion
            WHERE id = ?
            FOR UPDATE
            """;

        String sqlCerrarOcupacion = """
            UPDATE Ocupacion
            SET fechaCheckOut = ?, activa = FALSE
            WHERE id = ?
            """;

        String sqlHabitacion = """
            UPDATE Habitacion
            SET estado = 'LIMPIEZA'
            WHERE numero = ?
            """;

        String sqlReserva = """
            UPDATE Reserva
            SET estado = 'FINALIZADA'
            WHERE id = ? AND estado = 'EN_CURSO'
            """;

        DBConnection db = new DBConnection();

        try (Connection conn = db.crearConexion()) {
            conn.setAutoCommit(false);

            try {
                int numeroHabitacion;
                Integer reservaId;

                try (PreparedStatement consulta = conn.prepareStatement(sqlBuscar)) {

                    consulta.setInt(1, id);

                    try (ResultSet resultado = consulta.executeQuery()) {
                        if (!resultado.next()) {
                            throw new SQLException("La ocupación no existe.");
                        }

                        if (!resultado.getBoolean("activa")) {
                            throw new SQLException("El check-out ya fue realizado.");
                        }

                        LocalDate fechaIngreso = resultado.getDate("fechaCheckIn").toLocalDate();

                        if (fechaSalida.isBefore(fechaIngreso)) {
                            throw new SQLException("La salida no puede ser anterior al ingreso.");
                        }

                        numeroHabitacion = resultado.getInt("habitacionReservada");

                        int valorReserva = resultado.getInt("reservaId");
                        reservaId = resultado.wasNull() ? null : valorReserva;
                    }
                }

                try (PreparedStatement consulta = conn.prepareStatement(sqlCerrarOcupacion)) {

                    consulta.setDate(1, java.sql.Date.valueOf(fechaSalida));
                    consulta.setInt(2, id);

                    if (consulta.executeUpdate() != 1) {
                        throw new SQLException("No se pudo cerrar la ocupación.");
                    }
                }

                try (PreparedStatement consulta = conn.prepareStatement(sqlHabitacion)) {

                    consulta.setInt(1, numeroHabitacion);

                    if (consulta.executeUpdate() != 1) {
                        throw new SQLException("No se pudo actualizar la habitación.");
                    }
                }

                if (reservaId != null) {
                    try (PreparedStatement consulta = conn.prepareStatement(sqlReserva)) {

                        consulta.setInt(1, reservaId);

                        if (consulta.executeUpdate() != 1) {
                            throw new SQLException("La reserva asociada no existe o no está en curso.");
                        }
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
            throw new JDBCException("No se pudo registrar el check-out.", ex);
        }
    }

    public boolean tieneOcupacionEnPeriodo(int numeroHabitacion, LocalDate fechaInicio, LocalDate fechaFin) throws JDBCException {

        String sql = """
            SELECT id
            FROM Ocupacion
            WHERE habitacionReservada = ?
              AND activa = TRUE
              AND fechaCheckIn < ?
              AND fechaCheckOut > ?
            LIMIT 1
            """;

        DBConnection db = new DBConnection();

        try (Connection conn = db.crearConexion();
             PreparedStatement consulta = conn.prepareStatement(sql)) {

            consulta.setInt(1, numeroHabitacion);
            consulta.setDate(2, java.sql.Date.valueOf(fechaFin));
            consulta.setDate(3, java.sql.Date.valueOf(fechaInicio));

            try (ResultSet resultado = consulta.executeQuery()) {
                return resultado.next();
            }

        } catch (SQLException ex) {
            throw new JDBCException("No se pudo comprobar la disponibilidad de ocupaciones.", ex);
        }
    }


}
