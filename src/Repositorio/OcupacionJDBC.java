package Repositorio;

import Interfaces.IHotelJDBC;
import Models.Ocupacion;

import java.util.List;

public class OcupacionJDBC implements IHotelJDBC<Ocupacion, Integer> {
    @Override
    public void guardar(Ocupacion elemento) {

    }

    @Override
    public Ocupacion buscarPorID(Integer integer) {
        return null;
    }

    @Override
    public List<Ocupacion> listarTodos() {
        return List.of();
    }

    @Override
    public void actualizar(Ocupacion elemento, Integer integer) {

    }

    @Override
    public void eliminar(Integer integer) {

    }
}
