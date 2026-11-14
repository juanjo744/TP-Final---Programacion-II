package Repositorio;

import Interfaces.IHotelJDBC;
import Models.Habitacion;

import java.util.List;

public class HabitacionJDBC implements IHotelJDBC<Habitacion, Integer> {


    @Override
    public void guardar(Habitacion elemento) {

    }

    @Override
    public Habitacion buscarPorID(Integer integer) {
        return null;
    }

    @Override
    public List<Habitacion> listarTodos() {
        return List.of();
    }

    @Override
    public void actualizar(Habitacion elemento, Integer integer) {

    }

    @Override
    public void eliminar(Integer integer) {

    }
}
