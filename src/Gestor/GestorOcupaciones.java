package Gestor;

import Exceptions.ValidacionException;
import Interfaces.IGestorGenerico;

import java.util.List;

public class GestorOcupaciones implements IGestorGenerico {
    @Override
    public void cargarElemento(Object elemento) throws ValidacionException {

    }

    @Override
    public Object buscarPorID(Object object) throws ValidacionException {
        return null;
    }

    @Override
    public void actualizar(Object elemento, Object object) throws ValidacionException {

    }

    @Override
    public List listar() {
        return List.of();
    }

    @Override
    public void bajar(Object object) throws ValidacionException {

    }
}
