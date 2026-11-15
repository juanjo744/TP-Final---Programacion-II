package Interfaces;

import Exceptions.ValidacionException;

import java.util.List;

public interface IGestorGenerico<T, ID> {
    void cargarElemento(T elemento) throws ValidacionException;
    T buscarPorID(ID id) throws ValidacionException;
    void actualizar(T elemento, ID id) throws ValidacionException;
    List<T> listar();
    void bajar(ID id) throws ValidacionException;
}