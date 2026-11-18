package Gestor;

import Exceptions.JDBCException;
import Interfaces.IGestorGenerico;
import Interfaces.IHotelJDBC;

import java.util.List;

public abstract class GestorGenerico <T, ID> implements IGestorGenerico<T, ID> {
    private final IHotelJDBC<T, ID> repositorio;

    public GestorGenerico(IHotelJDBC<T, ID> repositorio) {
        this.repositorio = repositorio;
    }

    @Override
    public List<T> listar() throws JDBCException {
        return repositorio.listarTodos();
    }
}
