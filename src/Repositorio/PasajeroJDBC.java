package Repositorio;

import Interfaces.IHotelJDBC;
import Models.Pasajero;

import java.util.List;

public class PasajeroJDBC implements IHotelJDBC<Pasajero, String> {
    @Override
    public void guardar(Pasajero elemento) {

    }

    @Override
    public Pasajero buscarPorID(String s) {
        return null;
    }

    @Override
    public List<Pasajero> listarTodos() {
        return List.of();
    }

    @Override
    public void actualizar(Pasajero elemento, String s) {

    }

    @Override
    public void eliminar(String s) {

    }
}
