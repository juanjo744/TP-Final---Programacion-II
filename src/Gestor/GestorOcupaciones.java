package Gestor;

import Enums.EstadoHabitacion;
import Enums.EstadoReserva;
import Exceptions.JDBCException;
import Exceptions.VOcupacionInvalidaException;
import Exceptions.ValidacionException;
import Interfaces.IGestorGenerico;
import Models.Habitacion;
import Models.Ocupacion;
import Models.Pasajero;
import Models.Reserva;
import Repositorio.HabitacionJDBC;
import Repositorio.OcupacionJDBC;
import Repositorio.PasajeroJDBC;
import Repositorio.ReservaJDBC;

import java.time.LocalDate;
import java.util.*;

public class GestorOcupaciones implements IGestorGenerico<Ocupacion, Integer> {
    private OcupacionJDBC ocupacionJDBC;

    public GestorOcupaciones(OcupacionJDBC ocupacionJDBC) {
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

    @Override
    public List<Ocupacion> listar() throws JDBCException {
        return ocupacionJDBC.listarTodos();
    }

    public Map<Integer, Ocupacion> listarPorActividad(Boolean activa) throws JDBCException
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


}
