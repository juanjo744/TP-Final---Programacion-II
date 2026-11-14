package Interfaces;

import Exceptions.HabitacionInvalidaException;

import java.util.List;

public interface IGestorGenerico<T, ID> {
    void cargarElemento(T elemento) throws HabitacionInvalidaException;
    T buscarPorID(ID id) throws HabitacionInvalidaException;
    void actualizar(T elemento, ID id) throws HabitacionInvalidaException;
    List<T> listar();
    void bajar(ID id) throws HabitacionInvalidaException;
}