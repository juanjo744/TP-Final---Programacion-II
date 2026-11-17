package Gestor;

import Enums.EstadoReserva;
import Exceptions.JDBCException;
import Exceptions.VReservaInvalidaException;
import Interfaces.IGestorGenerico;
import Models.Habitacion;
import Models.Reserva;
import Repositorio.HabitacionJDBC;
import Repositorio.OcupacionJDBC;
import Repositorio.ReservaJDBC;

import java.util.List;

public class GestorReservas implements IGestorGenerico<Reserva, Integer> {

    private ReservaJDBC reservaJDBC;
    private HabitacionJDBC habitacionJDBC;
    private OcupacionJDBC ocupacionJDBC;

    public GestorReservas(ReservaJDBC reservaJDBC, HabitacionJDBC habitacionJDBC, OcupacionJDBC ocupacionJDBC) {
        this.reservaJDBC = reservaJDBC;
        this.habitacionJDBC = habitacionJDBC;
        this.ocupacionJDBC = ocupacionJDBC;
    }

    @Override
    public void cargarElemento(Reserva reserva) throws VReservaInvalidaException, JDBCException {

        if (reserva == null){
            throw new VReservaInvalidaException("Debe ingresar una reserva válida.");
        }
        if (reserva.getHabitacionSolicitada() == null){
            throw new VReservaInvalidaException("Tiene que seleccionar una habitacion para la reserva.");
        }
        if (reserva.getTitular() == null){
            throw new VReservaInvalidaException("La reserva tiene que tener un titular.");
        }
        if (reserva.getFechaInicio() == null || reserva.getFechaFin() == null){
            throw new VReservaInvalidaException("Tiene que indicar la fecha de inicio y fin de la reserva");
        }
        if (!reserva.getFechaFin().isAfter(reserva.getFechaInicio())){
            throw new VReservaInvalidaException("La fecha de fin tiene que ser posterior a la de inicio.");
        }
        if (reserva.getPasajeros() == null || reserva.getPasajeros().isEmpty()){
            throw new VReservaInvalidaException("Tiene que indicar al menos un pasajero.");
        }

        Habitacion habitacionEncontrada = habitacionJDBC.buscarPorID(reserva.getHabitacionSolicitada().getNumero());

        if (habitacionEncontrada == null){
            throw new VReservaInvalidaException("La habitacion no fue encontrada");
        }
        if (reserva.getPasajeros().size() > habitacionEncontrada.getCapacidad()){
            throw new VReservaInvalidaException("La cantidad de pasajeros no puede superar la capacidad de la habitacion.");
        }

        if (reservaJDBC.tieneReservaEnPeriodo(habitacionEncontrada.getNumero(), reserva.getFechaInicio(), reserva.getFechaFin(), null)) {

            throw new VReservaInvalidaException("La habitación tiene otra reserva para esas fechas.");
        }

        if (ocupacionJDBC.tieneOcupacionEnPeriodo(habitacionEncontrada.getNumero(), reserva.getFechaInicio(), reserva.getFechaFin())) {

            throw new VReservaInvalidaException("La habitación tiene una ocupación para esas fechas.");
        }

        reservaJDBC.guardar(reserva);

    }

    @Override
    public Reserva buscarPorID(Integer id) throws VReservaInvalidaException, JDBCException {

        if (id == null){
            throw new VReservaInvalidaException("Debe ingresar un numero de reserva");
        }
        if (id <= 0){
            throw new VReservaInvalidaException("El numero de reserva tiene que ser mayor a cero");
        }

        return reservaJDBC.buscarPorID(id);
    }

    @Override
    public void actualizar(Reserva reserva, Integer id) throws VReservaInvalidaException, JDBCException {

        if (id == null){
            throw new VReservaInvalidaException("Debe ingresar un numero de reserva");
        }
        if (id <= 0){
            throw new VReservaInvalidaException("El numero de reserva tiene que ser mayor a cero");
        }

        Reserva reservaEncontrada = reservaJDBC.buscarPorID(id);

        if (reservaEncontrada == null){
            throw new VReservaInvalidaException("No existe una reserva con ese numero");
        }
        if (reservaEncontrada.getEstado() == EstadoReserva.CANCELADA || reservaEncontrada.getEstado() == EstadoReserva.FINALIZADA){
            throw new VReservaInvalidaException("No se puede modificar una reserva cancelada o finalizada");
        }

        if (reserva == null){
            throw new VReservaInvalidaException("La reserva no puede ser nula.");
        }
        if (reserva.getHabitacionSolicitada() == null){
            throw new VReservaInvalidaException("Tiene que seleccionar una habitacion para la reserva.");
        }
        if (reserva.getTitular() == null){
            throw new VReservaInvalidaException("La reserva tiene que tener un titular.");
        }
        if (reserva.getFechaInicio() == null || reserva.getFechaFin() == null){
            throw new VReservaInvalidaException("Tiene que indicar la fecha de inicio y fin de la reserva");
        }
        if (!reserva.getFechaFin().isAfter(reserva.getFechaInicio())){
            throw new VReservaInvalidaException("La fecha de fin tiene que ser posterior a la de inicio.");
        }
        if (reserva.getPasajeros() == null || reserva.getPasajeros().isEmpty()){
            throw new VReservaInvalidaException("Tiene que indicar al menos un pasajero.");
        }

        Habitacion habitacionEncontrada = habitacionJDBC.buscarPorID(reserva.getHabitacionSolicitada().getNumero());

        if (habitacionEncontrada == null){
            throw new VReservaInvalidaException("La habitacion no fue encontrada");
        }
        if (reserva.getPasajeros().size() > habitacionEncontrada.getCapacidad()){
            throw new VReservaInvalidaException("La cantidad de pasajeros no puede superar la capacidad de la habitacion.");
        }
        if (ocupacionJDBC.tieneOcupacionEnPeriodo(habitacionEncontrada.getNumero(), reserva.getFechaInicio(), reserva.getFechaFin())) {

            throw new VReservaInvalidaException("La habitación tiene una ocupación para esas fechas.");
        }

        if (reservaJDBC.tieneReservaEnPeriodo(habitacionEncontrada.getNumero(), reserva.getFechaInicio(), reserva.getFechaFin(), id)) {

            throw new VReservaInvalidaException("La habitación tiene otra reserva para esas fechas.");
        }


        reservaJDBC.actualizar(reserva, id);

    }

    @Override
    public List<Reserva> listar() throws JDBCException {
        return reservaJDBC.listarTodos();
    }

    @Override
    public void bajar(Integer id) throws VReservaInvalidaException, JDBCException {

        if (id == null){
            throw new VReservaInvalidaException("Debe ingresar un numero de reserva");
        }
        if (id <= 0){
            throw new VReservaInvalidaException("El numero de reserva tiene que ser mayor a cero");
        }

        Reserva reservaEncontrada = reservaJDBC.buscarPorID(id);

        if (reservaEncontrada == null){
            throw new VReservaInvalidaException("La reserva no fue encontrada.");
        }

        if (reservaEncontrada.getEstado() == EstadoReserva.CANCELADA || reservaEncontrada.getEstado() == EstadoReserva.FINALIZADA){
            throw new VReservaInvalidaException("La reserva no se pudo cancelar ya que esta cancelada o finalizada");
        }

        reservaEncontrada.setEstado(EstadoReserva.CANCELADA);

        reservaJDBC.actualizar(reservaEncontrada, id);
        
    }
}
