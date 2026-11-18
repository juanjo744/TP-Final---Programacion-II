package Gestor;

import Enums.EstadoHabitacion;
import Enums.EstadoReserva;
import Exceptions.JDBCException;
import Exceptions.VPasajeroInvalidoException;
import Exceptions.VReservaInvalidaException;
import Interfaces.IGestorGenerico;
import Models.Habitacion;
import Models.Ocupacion;
import Models.Pasajero;
import Models.Reserva;
import Repositorio.OcupacionJDBC;
import Repositorio.PasajeroJDBC;
import Repositorio.ReservaJDBC;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GestorPasajeros implements IGestorGenerico<Pasajero, String> {

    private PasajeroJDBC pasajeroJDBC;
    private ReservaJDBC reservaJDBC;
    private OcupacionJDBC ocupacionJDBC;

    public GestorPasajeros(PasajeroJDBC pasajeroJDBC, OcupacionJDBC ocupacionJDBC, ReservaJDBC reservaJDBC) {
        this.pasajeroJDBC = pasajeroJDBC;
        this.ocupacionJDBC = ocupacionJDBC;
        this.reservaJDBC = reservaJDBC;
    }

    @Override
    public void cargarElemento(Pasajero pasajero) throws VPasajeroInvalidoException, JDBCException {
        if (pasajero == null){
            throw new VPasajeroInvalidoException("Debe ingresar un pasajero válido.");
        }
        if (pasajero.getDNI() == null || !pasajero.getDNI().matches("[0-9]{8}")) {
            throw new VPasajeroInvalidoException("El DNI debe contener exactamente ocho números.");
        }
        if (pasajero.getNombre() == null){
            throw new VPasajeroInvalidoException("El pasajero debe tener un nombre asociado.");
        }
        if (pasajero.getOrigen() == null || pasajero.getDomicilio() == null){
            throw new VPasajeroInvalidoException("El origen del pasajero debe estar registrado.");
        }

        Pasajero pasajeroEncontrado = pasajeroJDBC.buscarPorID(pasajero.getDNI());

        if (pasajeroEncontrado != null){
            throw new VPasajeroInvalidoException("Ya existe un pasajero con ese ID.");
        }

        pasajeroJDBC.guardar(pasajero);
    }

    @Override
    public Pasajero buscarPorID(String DNI) throws VPasajeroInvalidoException, JDBCException {
        if (DNI == null || !DNI.matches("[0-9]{8}")) {
            throw new VPasajeroInvalidoException("El DNI debe contener exactamente ocho números.");
        }

        return pasajeroJDBC.buscarPorID(DNI);
    }

    @Override
    public void actualizar(Pasajero pasajero, String DNI) throws VPasajeroInvalidoException, JDBCException {
        if (pasajero == null){
            throw new VPasajeroInvalidoException("El Pasajero no puede ser nulo.");
        }
        if (pasajero.getDNI() == null){
            throw new VPasajeroInvalidoException("El pasajero debe estar identificado por su número de documento.");
        }
        if (!DNI.equals(pasajero.getDNI())) {
            throw new VPasajeroInvalidoException("No se puede cambiar el DNI del pasajero.");
        }
        if (pasajero.getNombre() == null){
            throw new VPasajeroInvalidoException("El pasajero debe tener un nombre asociado.");
        }
        if (pasajero.getOrigen() == null || pasajero.getDomicilio() == null){
            throw new VPasajeroInvalidoException("El origen del pasajero debe estar registrado.");
        }

        Pasajero pasajeroEncontrado = pasajeroJDBC.buscarPorID(pasajero.getDNI());

        if (pasajeroEncontrado == null){
            throw new VPasajeroInvalidoException("El pasajero no fue encontrado");
        }

        if (DNI == null)
            throw new VPasajeroInvalidoException("Debe ingresar un número de documento");

        if (DNI.length() != 8)
            throw new VPasajeroInvalidoException("Debe ingresar un DNI válido.");

        pasajeroJDBC.actualizar(pasajero, DNI);
    }

    @Override
    public List<Pasajero> listar() throws JDBCException {
        return pasajeroJDBC.listarTodos();
    }

    public Map<String, Pasajero> listarPorEstado(EstadoReserva estado) throws JDBCException {

        Map<String, Pasajero> pasajerosDisponibles = new HashMap<>();
        List<Reserva> reservas = reservaJDBC.listarTodos();

        for (Reserva r : reservas)
        {
            if (r.getEstado().equals(estado))
            {
                for (Pasajero p : r.getPasajeros())
                {
                    pasajerosDisponibles.put(p.getDNI(), p);
                }
            }
        }

        return pasajerosDisponibles;
    }

    public Map<String, Pasajero> listarAlojados() throws JDBCException {
        Map<String, Pasajero> pasajeros = new HashMap<>();

        for (Ocupacion ocupacion : ocupacionJDBC.listarTodos()) {
            if (ocupacion.isActiva()) {
                for (Pasajero pasajero : ocupacion.getPasajeros()) {
                    pasajeros.put(pasajero.getDNI(), pasajero);
                }
            }
        }

        return pasajeros;
    }

    @Override
    public void bajar(String DNI) throws VPasajeroInvalidoException, JDBCException {

        if (DNI == null || !DNI.matches("[0-9]{8}")) {
            throw new VPasajeroInvalidoException("El DNI debe contener exactamente ocho números.");
        }

        Pasajero pasajeroEncontrado = pasajeroJDBC.buscarPorID(DNI);

        if (pasajeroEncontrado == null){
            throw new VPasajeroInvalidoException("El pasajero no fue encontrado");
        }

        pasajeroJDBC.eliminar(DNI);
    }
}
