package Repositorio;

import Interfaces.IHotelJDBC;

import java.util.List;

public class UsuarioJDBC implements IHotelJDBC {
    @Override
    public void guardar(Object elemento) {

    }

    @Override
    public Object buscarPorID(Object object) {
        return null;
    }

    @Override
    public List listarTodos() {
        return List.of();
    }

    @Override
    public void actualizar(Object elemento, Object object) {

    }

    @Override
    public void eliminar(Object object) {

    }
}
