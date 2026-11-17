package Gestor;

import Enums.EstadoHabitacion;
import Exceptions.JDBCException;
import Exceptions.VHabitacionInvalidaException;
import Interfaces.IGestorGenerico;
import Models.Habitacion;
import Repositorio.HabitacionJDBC;

import java.util.ArrayList;
import java.util.List;

public class GestorHabitaciones implements IGestorGenerico<Habitacion, Integer> {

    private HabitacionJDBC habitacionJDBC;

    public GestorHabitaciones(HabitacionJDBC habitacionJDBC) {
        this.habitacionJDBC = habitacionJDBC;
    }

    @Override
    public void cargarElemento(Habitacion habitacion) throws VHabitacionInvalidaException {

        if (habitacion == null){
            throw new VHabitacionInvalidaException("La habitacion no puede ser nula.");
        }

        if (habitacion.getNumero() <= 0){
            throw new VHabitacionInvalidaException("El numero de la habitacion tiene que ser mayor que cero.");
        }

        if (habitacion.getCapacidad() <= 0){
            throw new VHabitacionInvalidaException("La capacidad tiene que ser mayor que cero.");
        }
        if (habitacion.getPrecioPorNoche() <= 0){
            throw new VHabitacionInvalidaException("El precio por noche tiene que ser mayor que cero.");
        }
        if (habitacion.getEstado() == null){
            throw new VHabitacionInvalidaException("El estado de las habitaciones es obligatorio.");
        }

        if (habitacionJDBC.buscarPorID(habitacion.getNumero()) != null){
            throw new VHabitacionInvalidaException("El numero ya esta registrado.");
        }

        try {
            habitacionJDBC.guardar(habitacion);
        } catch (JDBCException e) {
            throw new RuntimeException(e);
        }
    }



    @Override
    public Habitacion buscarPorID(Integer id) throws VHabitacionInvalidaException {

        if (id == null){
            throw new VHabitacionInvalidaException("Debe ingresar un numero de habitacion.");
        }
        if (id <= 0){
            throw new VHabitacionInvalidaException("El numero de la habitacion tiene que ser mayor que cero");
        }

        return habitacionJDBC.buscarPorID(id);
    }

    @Override
    public void actualizar(Habitacion habitacion, Integer id) throws VHabitacionInvalidaException {

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
        if (habitacion.getPrecioPorNoche() <= 0){
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

    @Override
    public List<Habitacion> listar() {

        return habitacionJDBC.listarTodos();
    }

    public List<Habitacion> listarDisponibles(){

        ArrayList<Habitacion> habitacionesDisponibles = new ArrayList<>();

        List<Habitacion> habitaciones = habitacionJDBC.listarTodos();

        for (Habitacion h : habitaciones){
            if (h.getEstado() == EstadoHabitacion.DISPONIBLE){
                habitacionesDisponibles.add(h);
            }
        }

        return habitacionesDisponibles;

    }

    public List<Habitacion> listarNoDisponibles(){

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
    public void bajar(Integer id) throws VHabitacionInvalidaException {

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
}
