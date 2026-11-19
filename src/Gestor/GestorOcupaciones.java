package Gestor;

import Enums.Consumos;
import Enums.EstadoHabitacion;
import Enums.EstadoReserva;
import Exceptions.JDBCException;
import Exceptions.VOcupacionInvalidaException;
import Exceptions.ValidacionException;
import Interfaces.IGestorGenerico;
import Interfaces.IHotelJDBC;
import Models.Habitacion;
import Models.Ocupacion;
import Models.Pasajero;
import Models.Reserva;
import Repositorio.HabitacionJDBC;
import Repositorio.OcupacionJDBC;
import Repositorio.PasajeroJDBC;
import Repositorio.ReservaJDBC;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class GestorOcupaciones extends GestorGenerico<Ocupacion, Integer> {
    private OcupacionJDBC ocupacionJDBC;

    public GestorOcupaciones(OcupacionJDBC ocupacionJDBC) {
        super(ocupacionJDBC);
        this.ocupacionJDBC = ocupacionJDBC;
    }

    @Override
    public void cargarElemento(Ocupacion ocupacion) throws VOcupacionInvalidaException, JDBCException {
        if (ocupacion == null)
            throw new VOcupacionInvalidaException("Debe ingresar una ocupación válida.");

        if (ocupacion.getHabitacionReservada() == null)
            throw new VOcupacionInvalidaException("La ocupacion debe tener una habitación asociada.");

        if (ocupacion.getPasajeros().size() <= 0)
            throw new VOcupacionInvalidaException("La ocupación debe tener al menos un pasajero asociado.");

        if(ocupacion.getFechaCheckIn() == null)
            throw new VOcupacionInvalidaException("La ocupación no existe al no poder confirmarse.");

        ocupacionJDBC.guardar(ocupacion);
    }

    @Override
    public Ocupacion buscarPorID(Integer ID) throws VOcupacionInvalidaException, JDBCException {
        if (ID == null || ID <= 0)
            throw new VOcupacionInvalidaException("Debe ingresar un ID válido.");

        Ocupacion ocupacionEncontrada = ocupacionJDBC.buscarPorID(ID);

        if (ocupacionEncontrada == null)
            throw new VOcupacionInvalidaException("La ocupación no fue encontrada.");

        return ocupacionJDBC.buscarPorID(ID);
    }

    @Override
    public void actualizar(Ocupacion ocupacion, Integer ID) throws VOcupacionInvalidaException, JDBCException {
        if (ocupacion == null)
            throw new VOcupacionInvalidaException("Debe ingresar una ocupación válida.");

        if (ocupacion.getHabitacionReservada() == null)
            throw new VOcupacionInvalidaException("La ocupacion debe tener una habitación asociada.");

        if (ocupacion.getPasajeros().size() <= 0)
            throw new VOcupacionInvalidaException("La ocupación debe tener al menos un pasajero asociado.");

        if(ocupacion.getFechaCheckIn() == null)
            throw new VOcupacionInvalidaException("La ocupación no existe al no poder confirmarse.");

        if (ID == null || ID <= 0)
            throw new VOcupacionInvalidaException("Debe ingresar un ID válido.");

        ocupacionJDBC.actualizar(ocupacion, ID);
    }

    public Map<Integer, Ocupacion> listarPorActividad(boolean activa) throws JDBCException
    {
        List<Ocupacion> ocupaciones = ocupacionJDBC.listarTodos();
        Map<Integer, Ocupacion> ocupacionMap = new LinkedHashMap<>();

        for (Ocupacion o : ocupaciones)
        {
            if (o.isActiva() == activa)
                ocupacionMap.put(o.getId(), o);
        }

        return ocupacionMap;
    }

    @Override
    public void bajar(Integer ID) throws VOcupacionInvalidaException, JDBCException {
        if (ID == null || ID <= 0)
            throw new VOcupacionInvalidaException("Debe ingresar un ID válido.");

        Ocupacion ocupacionEncontrada = ocupacionJDBC.buscarPorID(ID);

        if (ocupacionEncontrada == null)
            throw new VOcupacionInvalidaException("La ocupación no fue encontrada.");

        ocupacionJDBC.eliminar(ID);
    }

    public Ocupacion hacerCheckIn(Reserva reserva) throws VOcupacionInvalidaException, JDBCException {

        if (reserva == null || reserva.getId() <= 0) {
            throw new VOcupacionInvalidaException("Debe indicar una reserva registrada.");
        }

        ReservaJDBC reservaJDBC = new ReservaJDBC();
        Reserva reservaEncontrada = reservaJDBC.buscarPorID(reserva.getId());

        if (reservaEncontrada == null) {
            throw new VOcupacionInvalidaException("La reserva no existe.");
        }

        if (reservaEncontrada.getEstado() != EstadoReserva.CONFIRMADA) {
            throw new VOcupacionInvalidaException("Solo se puede hacer check-in de una reserva confirmada.");
        }

        LocalDate inicio = reservaEncontrada.getFechaInicio();
        LocalDate fin = reservaEncontrada.getFechaFin();

        if (inicio == null || fin == null || !fin.isAfter(inicio)) {
            throw new VOcupacionInvalidaException("Las fechas de la reserva no son válidas.");
        }

        LocalDate hoy = LocalDate.now();

        if (hoy.isBefore(inicio) || !hoy.isBefore(fin)) {
            throw new VOcupacionInvalidaException("El check-in debe realizarse dentro del período de la reserva.");
        }

        if (reservaEncontrada.getHabitacionSolicitada() == null) {
            throw new VOcupacionInvalidaException("La reserva no tiene una habitación asignada.");
        }

        HabitacionJDBC habitacionJDBC = new HabitacionJDBC();

        Habitacion habitacion = habitacionJDBC.buscarPorID(reservaEncontrada.getHabitacionSolicitada().getNumero());

        if (habitacion == null) {
            throw new VOcupacionInvalidaException("La habitación no existe.");
        }

        if (habitacion.getEstado() != EstadoHabitacion.DISPONIBLE) {
            throw new VOcupacionInvalidaException("La habitación no está disponible para el ingreso.");
        }

        if (reservaEncontrada.getPasajeros() == null || reservaEncontrada.getPasajeros().isEmpty()) {

            throw new VOcupacionInvalidaException("La reserva debe tener al menos un pasajero.");
        }

        if (reservaEncontrada.getPasajeros().size() > habitacion.getCapacidad()) {

            throw new VOcupacionInvalidaException("La cantidad de pasajeros supera la capacidad.");
        }

        Ocupacion ocupacion = new Ocupacion(habitacion, new ArrayList<>(reservaEncontrada.getPasajeros()), hoy, fin, true);

        ocupacionJDBC.registrarCheckIn(ocupacion, reservaEncontrada.getId());

        return ocupacion;
    }

    public Ocupacion hacerCheckIn(Habitacion habitacion, Pasajero pasajero, LocalDate fechaSalida) throws VOcupacionInvalidaException, JDBCException {

        if (habitacion == null || habitacion.getNumero() <= 0) {
            throw new VOcupacionInvalidaException("Debe seleccionar una habitacion valida.");
        }

        if (pasajero == null || pasajero.getDNI() == null || pasajero.getDNI().isBlank()) {

            throw new VOcupacionInvalidaException("Debe indicar un pasajero con DNI.");
        }

        LocalDate hoy = LocalDate.now();

        if (fechaSalida == null || !fechaSalida.isAfter(hoy)) {
            throw new VOcupacionInvalidaException("La fecha de salida debe ser posterior a hoy.");
        }

        HabitacionJDBC habitacionJDBC = new HabitacionJDBC();
        Habitacion habitacionEncontrada = habitacionJDBC.buscarPorID(habitacion.getNumero());

        if (habitacionEncontrada == null) {
            throw new VOcupacionInvalidaException("La habitación no existe.");
        }

        if (habitacionEncontrada.getEstado() != EstadoHabitacion.DISPONIBLE) {

            throw new VOcupacionInvalidaException("La habitación no está disponible.");
        }

        if (habitacionEncontrada.getCapacidad() < 1) {
            throw new VOcupacionInvalidaException("La habitación no tiene capacidad para alojar al pasajero.");
        }

        PasajeroJDBC pasajeroJDBC = new PasajeroJDBC();
        Pasajero pasajeroEncontrado = pasajeroJDBC.buscarPorID(pasajero.getDNI());

        if (pasajeroEncontrado == null) {
            throw new VOcupacionInvalidaException("Debe registrar al pasajero antes de hacer el check-in.");
        }

        ArrayList<Pasajero> pasajeros = new ArrayList<>();
        pasajeros.add(pasajeroEncontrado);

        Ocupacion ocupacion = new Ocupacion(habitacionEncontrada, pasajeros, hoy, fechaSalida, true);

        ocupacionJDBC.registrarCheckInSinReserva(ocupacion);

        return ocupacion;
    }

    public void hacerCheckOut(Ocupacion ocupacion) throws VOcupacionInvalidaException, JDBCException {

        if (ocupacion == null || ocupacion.getId() <= 0) {
            throw new VOcupacionInvalidaException("Debe indicar una ocupación registrada.");
        }

        Ocupacion ocupacionEncontrada = ocupacionJDBC.buscarPorID(ocupacion.getId());

        if (ocupacionEncontrada == null) {
            throw new VOcupacionInvalidaException("La ocupación no existe.");
        }

        if (!ocupacionEncontrada.isActiva()) {
            throw new VOcupacionInvalidaException("El check-out de esta ocupación ya fue realizado.");
        }

        LocalDate hoy = LocalDate.now();

        if (ocupacionEncontrada.getFechaCheckIn() == null || hoy.isBefore(ocupacionEncontrada.getFechaCheckIn())) {

            throw new VOcupacionInvalidaException("La fecha de salida no puede ser anterior al ingreso.");
        }

        ocupacionJDBC.registrarCheckOut(ocupacionEncontrada.getId(), hoy);

        ocupacion.setFechaCheckOut(hoy);
        ocupacion.setActiva(false);

        if (ocupacion.getHabitacionReservada() != null) {
            ocupacion.getHabitacionReservada().setEstado(EstadoHabitacion.LIMPIEZA);
        }
    }

    public void registrarConsumo(Integer ocupacionId, String nombreCliente, Consumos consumo) throws VOcupacionInvalidaException, JDBCException {

        if (ocupacionId == null || ocupacionId <= 0) {
            throw new VOcupacionInvalidaException("Debe indicar una ocupación válida.");
        }

        if (nombreCliente == null || nombreCliente.isBlank()) {
            throw new VOcupacionInvalidaException("Debe indicar el cliente que realiza el consumo.");
        }

        if (consumo == null) {
            throw new VOcupacionInvalidaException("Debe seleccionar un consumo.");
        }

        ocupacionJDBC.registrarConsumo(ocupacionId, nombreCliente, consumo);
    }

    public BigDecimal obtenerGastos(Integer ocupacionId, String nombreCliente) throws VOcupacionInvalidaException, JDBCException {

        if (ocupacionId == null || ocupacionId <= 0) {
            throw new VOcupacionInvalidaException("Debe indicar una ocupación válida.");
        }

        if (nombreCliente == null || nombreCliente.isBlank()) {
            throw new VOcupacionInvalidaException("Debe indicar un cliente.");
        }

        return ocupacionJDBC.obtenerGastos(ocupacionId, nombreCliente);
    }

    public List<Ocupacion> listarDelCliente(String nombreCliente) throws JDBCException {
        return ocupacionJDBC.listarTodos().stream()
                .filter(ocupacion -> perteneceAlCliente(ocupacion, nombreCliente))
                .toList();
    }

    private boolean perteneceAlCliente(Ocupacion ocupacion, String nombreCliente) {
        return ocupacion.getPasajeros() != null && ocupacion.getPasajeros().stream()
                .anyMatch(pasajero -> pasajero != null && pasajero.getCliente() != null
                        && nombreCliente.equals(pasajero.getCliente().getNombreUsuario()));
    }

    public BigDecimal calcularTotalEstadia(Integer ocupacionId, String nombreCliente) throws VOcupacionInvalidaException, JDBCException {

        if (ocupacionId == null || ocupacionId <= 0) {
            throw new VOcupacionInvalidaException("Debe indicar una ocupación válida.");
        }

        if (nombreCliente == null || nombreCliente.isBlank()) {
            throw new VOcupacionInvalidaException("Debe indicar un cliente.");
        }

        Ocupacion ocupacion = ocupacionJDBC.buscarPorID(ocupacionId);

        if (ocupacion == null) {
            throw new VOcupacionInvalidaException("La ocupación no existe.");
        }

        if (!perteneceAlCliente(ocupacion, nombreCliente)) {
            throw new VOcupacionInvalidaException("No estás asociado a esta estadía.");
        }

        LocalDate inicio = ocupacion.getFechaCheckIn();

        LocalDate fin = ocupacion.isActiva() ? LocalDate.now() : ocupacion.getFechaCheckOut();

        if (inicio == null || fin == null || fin.isBefore(inicio)) {
            throw new VOcupacionInvalidaException("Las fechas de la estadía no son válidas.");
        }

        if (ocupacion.getHabitacionReservada() == null) {
            throw new VOcupacionInvalidaException("La ocupación no tiene una habitación asociada.");
        }

        double precio = ocupacion.getHabitacionReservada().getPrecioPorNoche();

        if (!Double.isFinite(precio) || precio <= 0) {
            throw new VOcupacionInvalidaException("El precio por noche no es válido.");
        }

        long noches = Math.max(1, ChronoUnit.DAYS.between(inicio, fin));

        BigDecimal alojamiento = BigDecimal.valueOf(precio).multiply(BigDecimal.valueOf(noches));

        BigDecimal consumos = ocupacionJDBC.obtenerGastos(ocupacionId, nombreCliente);

        return alojamiento.add(consumos).setScale(2, RoundingMode.HALF_UP);
    }

}
