package Gestor;

import Enums.EstadoHabitacion;
import Exceptions.JDBCException;
import Exceptions.VHabitacionInvalidaException;
import Interfaces.IGestorGenerico;
import Interfaces.IHotelJDBC;
import Models.Habitacion;
import Models.Ocupacion;
import Repositorio.HabitacionJDBC;
import Repositorio.OcupacionJDBC;

import java.util.ArrayList;
import java.util.List;

public class GestorHabitaciones extends GestorGenerico<Habitacion, Integer> {

    private HabitacionJDBC habitacionJDBC;
    private OcupacionJDBC ocupacionJDBC;

    public GestorHabitaciones(HabitacionJDBC habitacionJDBC, OcupacionJDBC ocupacionJDBC) {
        super(habitacionJDBC);
        this.habitacionJDBC = habitacionJDBC;
        this.ocupacionJDBC = ocupacionJDBC;
    }

    @Override
    public void cargarElemento(Habitacion habitacion) throws VHabitacionInvalidaException, JDBCException {

        if (habitacion == null){
            throw new VHabitacionInvalidaException("La habitacion no puede ser nula.");
        }

        if (habitacion.getNumero() <= 0){
            throw new VHabitacionInvalidaException("El numero de la habitacion tiene que ser mayor que cero.");
        }

        if (habitacion.getCapacidad() <= 0){
            throw new VHabitacionInvalidaException("La capacidad tiene que ser mayor que cero.");
        }
        if (habitacion.getPrecioPorNoche() <= 0 || !Double.isFinite(habitacion.getPrecioPorNoche())){
            throw new VHabitacionInvalidaException("El precio por noche tiene que ser mayor que cero.");
        }
        if (habitacion.getEstado() == null){
            throw new VHabitacionInvalidaException("El estado de las habitaciones es obligatorio.");
        }

        if (habitacionJDBC.buscarPorID(habitacion.getNumero()) != null){
            throw new VHabitacionInvalidaException("El numero ya esta registrado.");
        }

        habitacionJDBC.guardar(habitacion);
    }

    @Override
    public Habitacion buscarPorID(Integer id) throws VHabitacionInvalidaException, JDBCException {

        if (id == null){
            throw new VHabitacionInvalidaException("Debe ingresar un numero de habitacion.");
        }
        if (id <= 0){
            throw new VHabitacionInvalidaException("El numero de la habitacion tiene que ser mayor que cero");
        }

        return habitacionJDBC.buscarPorID(id);
    }

    @Override
    public void actualizar(Habitacion habitacion, Integer id) throws VHabitacionInvalidaException, JDBCException {

        if (id == null){
            throw new VHabitacionInvalidaException("Debe ingresar un numero de habitacion.");
        }
        if (id <= 0){
            throw new VHabitacionInvalidaException("El numero de la habitacion tiene que ser mayor que cero");
        }
        if (habitacion == null){
            throw new VHabitacionInvalidaException("La habitacion no puede ser nula.");
        }
        if (habitacion.getCapacidad() <= 0){
            throw new VHabitacionInvalidaException("La capacidad tiene que ser mayor que cero.");
        }
        if (habitacion.getPrecioPorNoche() <= 0 || !Double.isFinite(habitacion.getPrecioPorNoche())){
            throw new VHabitacionInvalidaException("El precio por noche tiene que ser mayor que cero.");
        }
        if (habitacion.getEstado() == null){
            throw new VHabitacionInvalidaException("El estado de las habitaciones es obligatorio.");
        }
        if (habitacionJDBC.buscarPorID(id) == null){
            throw new VHabitacionInvalidaException("No existe una habitacion con ese numero.");
        }

        habitacionJDBC.actualizar(habitacion, id);

    }

    public List<Habitacion> listarDisponibles() throws JDBCException {

        ArrayList<Habitacion> habitacionesDisponibles = new ArrayList<>();

        List<Habitacion> habitaciones = habitacionJDBC.listarTodos();

        for (Habitacion h : habitaciones){
            if (h.getEstado() == EstadoHabitacion.DISPONIBLE){
                habitacionesDisponibles.add(h);
            }
        }

        return habitacionesDisponibles;

    }

    public List<Habitacion> listarNoDisponibles() throws JDBCException {

        ArrayList<Habitacion> habitacionesNoDisponibles = new ArrayList<>();

        List<Habitacion> habitaciones = habitacionJDBC.listarTodos();

        for (Habitacion h : habitaciones){
            if (h.getEstado() != EstadoHabitacion.DISPONIBLE){
                habitacionesNoDisponibles.add(h);
            }
        }

        return habitacionesNoDisponibles;
    }

    @Override
    public void bajar(Integer id) throws VHabitacionInvalidaException, JDBCException {

        if (id == null){
            throw new VHabitacionInvalidaException("Debe ingresar un numero de habitacion.");
        }
        if (id <= 0){
            throw new VHabitacionInvalidaException("El numero de la habitacion tiene que ser mayor que cero");
        }
        if (habitacionJDBC.buscarPorID(id) == null){
            throw new VHabitacionInvalidaException("No existe una habitacion con este numero.");
        }

        // Mas adelante agregar una validacion para que no se elimen habitacion con reservas pendientes o confirmadas.

        habitacionJDBC.eliminar(id);

    }

    public void cambiarEstado(Integer numero, EstadoHabitacion nuevoEstado) throws VHabitacionInvalidaException, JDBCException {

        if (nuevoEstado == null) {
            throw new VHabitacionInvalidaException("Debe seleccionar un estado.");
        }

        if (nuevoEstado == EstadoHabitacion.OCUPADA) {
            throw new VHabitacionInvalidaException("El estado OCUPADA se asigna mediante el check-in.");
        }

        Habitacion habitacion = buscarPorID(numero);

        if (habitacion == null) {
            throw new VHabitacionInvalidaException("La habitación no existe.");
        }

        for (Ocupacion ocupacion : ocupacionJDBC.listarTodos()) {
            if (ocupacion.isActiva() && ocupacion.getHabitacionReservada().getNumero() == numero) {

                throw new VHabitacionInvalidaException("Debe realizar el check-out antes de cambiar el estado.");
            }
        }

        habitacion.setEstado(nuevoEstado);
        habitacionJDBC.actualizar(habitacion, numero);
    }
}
