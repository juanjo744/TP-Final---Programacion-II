package Interfaces;

import Exceptions.JDBCException;
import Exceptions.ValidacionException;

import java.util.List;

public interface IGestorGenerico<T, ID> {
    void cargarElemento(T elemento) throws ValidacionException, JDBCException;
    T buscarPorID(ID id) throws ValidacionException, JDBCException;
    void actualizar(T elemento, ID id) throws ValidacionException, JDBCException;
    List<T> listar() throws JDBCException;
    void bajar(ID id) throws ValidacionException, JDBCException;
}