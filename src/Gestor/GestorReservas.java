package Gestor;

import Enums.EstadoReserva;
import Exceptions.ReservaInvalidaException;
import Interfaces.IGestorGenerico;
import Models.Habitacion;
import Models.Reserva;
import Repositorio.HabitacionJDBC;
import Repositorio.ReservaJDBC;

import java.util.List;

public class GestorReservas implements IGestorGenerico<Reserva, Integer> {

    private ReservaJDBC reservaJDBC;
    private HabitacionJDBC habitacionJDBC;

    public GestorReservas(ReservaJDBC reservaJDBC, HabitacionJDBC habitacionJDBC) {
        this.reservaJDBC = reservaJDBC;
        this.habitacionJDBC = habitacionJDBC;
    }


    @Override
    public void cargarElemento(Reserva reserva) throws ReservaInvalidaException {

        if (reserva == null){
            throw new ReservaInvalidaException("La reserva no puede ser nula.");
        }
        if (reserva.getHabitacionSolicitada() == null){
            throw new ReservaInvalidaException("Tiene que seleccionar una habitacion para la reserva.");
        }
        if (reserva.getTitular() == null){
            throw new ReservaInvalidaException("La reserva tiene que tener un titular.");
        }
        if (reserva.getFechaInicio() == null || reserva.getFechaFin() == null){
            throw new ReservaInvalidaException("Tiene que indicar la fecha de inicio y fin de la reserva");
        }
        if (!reserva.getFechaFin().isAfter(reserva.getFechaInicio())){
            throw new ReservaInvalidaException("La fecha de fin tiene que ser posterior a la de inicio.");
        }
        if (reserva.getPasajeros() == null || reserva.getPasajeros().isEmpty()){
            throw new ReservaInvalidaException("Tiene que indicar al menos un pasajero.");
        }

        Habitacion habitacionEncontrada = habitacionJDBC.buscarPorID(reserva.getHabitacionSolicitada().getNumero());

        if (habitacionEncontrada == null){
            throw new ReservaInvalidaException("La habitacion no fue encontrada");
        }
        if (reserva.getPasajeros().size() > habitacionEncontrada.getCapacidad()){
            throw new ReservaInvalidaException("La cantidad de pasajeros no puede superar la capacidad de la habitacion.");
        }

        // PENDIENTE: Antes de guardar, comprobar que la habitacion no tenga reservas pendientes o confirmadas
        // ni ocupaciones que coincidan con las fechas solicitadas. Agregar las consultas JDBC correspondientes.

        reservaJDBC.guardar(reserva);

    }

    @Override
    public Reserva buscarPorID(Integer id) throws ReservaInvalidaException {

        if (id == null){
            throw new ReservaInvalidaException("Debe ingresar un numero de reserva");
        }
        if (id <= 0){
            throw new ReservaInvalidaException("El numero de reserva tiene que ser mayor a cero");
        }

        return reservaJDBC.buscarPorID(id);
    }

    @Override
    public void actualizar(Reserva reserva, Integer id) throws ReservaInvalidaException {

        if (id == null){
            throw new ReservaInvalidaException("Debe ingresar un numero de reserva");
        }
        if (id <= 0){
            throw new ReservaInvalidaException("El numero de reserva tiene que ser mayor a cero");
        }

        Reserva reservaEncontrada = reservaJDBC.buscarPorID(id);

        if (reservaEncontrada == null){
            throw new ReservaInvalidaException("No existe una reserva con ese numero");
        }
        if (reservaEncontrada.getEstado() == EstadoReserva.CANCELADA || reservaEncontrada.getEstado() == EstadoReserva.FINALIZADA){
            throw new ReservaInvalidaException("No se puede modificar una reserva cancelada o finalizada");
        }

        if (reserva == null){
            throw new ReservaInvalidaException("La reserva no puede ser nula.");
        }
        if (reserva.getHabitacionSolicitada() == null){
            throw new ReservaInvalidaException("Tiene que seleccionar una habitacion para la reserva.");
        }
        if (reserva.getTitular() == null){
            throw new ReservaInvalidaException("La reserva tiene que tener un titular.");
        }
        if (reserva.getFechaInicio() == null || reserva.getFechaFin() == null){
            throw new ReservaInvalidaException("Tiene que indicar la fecha de inicio y fin de la reserva");
        }
        if (!reserva.getFechaFin().isAfter(reserva.getFechaInicio())){
            throw new ReservaInvalidaException("La fecha de fin tiene que ser posterior a la de inicio.");
        }
        if (reserva.getPasajeros() == null || reserva.getPasajeros().isEmpty()){
            throw new ReservaInvalidaException("Tiene que indicar al menos un pasajero.");
        }

        Habitacion habitacionEncontrada = habitacionJDBC.buscarPorID(reserva.getHabitacionSolicitada().getNumero());

        if (habitacionEncontrada == null){
            throw new ReservaInvalidaException("La habitacion no fue encontrada");
        }
        if (reserva.getPasajeros().size() > habitacionEncontrada.getCapacidad()){
            throw new ReservaInvalidaException("La cantidad de pasajeros no puede superar la capacidad de la habitacion.");
        }

        // PENDIENTE: Comprobar con consultas JDBC que no haya otras reservas pendientes o confirmadas
        // ni ocupaciones que coincidan con las nuevas fechas para la habitacion elegida.
        // Excluir de la busqueda la propia reserva identificada por id.

        reservaJDBC.actualizar(reserva, id);

    }

    @Override
    public List<Reserva> listar() {
        return reservaJDBC.listarTodos();
    }

    @Override
    public void bajar(Integer id) throws ReservaInvalidaException {

        if (id == null){
            throw new ReservaInvalidaException("Debe ingresar un numero de reserva");
        }
        if (id <= 0){
            throw new ReservaInvalidaException("El numero de reserva tiene que ser mayor a cero");
        }

        Reserva reservaEncontrada = reservaJDBC.buscarPorID(id);

        if (reservaEncontrada == null){
            throw new ReservaInvalidaException("La reserva no fue encontrada.");
        }

        if (reservaEncontrada.getEstado() == EstadoReserva.CANCELADA || reservaEncontrada.getEstado() == EstadoReserva.FINALIZADA){
            throw new ReservaInvalidaException("La reserva no se pudo cancelar ya que esta cancelada o finalizada");
        }

        reservaEncontrada.setEstado(EstadoReserva.CANCELADA);

        reservaJDBC.actualizar(reservaEncontrada, id);
        
    }
}
