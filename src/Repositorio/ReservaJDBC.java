package Repositorio;

import Interfaces.IHotelJDBC;
import Models.Reserva;

import java.util.List;

public class ReservaJDBC implements IHotelJDBC<Reserva, Integer> {


    @Override
    public void guardar(Reserva elemento) {

    }

    @Override
    public Reserva buscarPorID(Integer integer) {
        return null;
    }

    @Override
    public List<Reserva> listarTodos() {
        return List.of();
    }

    @Override
    public void actualizar(Reserva elemento, Integer integer) {

    }

    @Override
    public void eliminar(Integer integer) {

    }
}
