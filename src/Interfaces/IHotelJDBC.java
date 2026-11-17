package Interfaces;

import Exceptions.JDBCException;

import java.sql.SQLException;
import java.util.List;

public interface IHotelJDBC<T, ID> {
    void crearTabla() throws JDBCException;
    void guardar(T elemento) throws JDBCException;
    T buscarPorID(ID id) throws JDBCException;
    List<T> listarTodos();
    void actualizar(T elemento, ID id);
    void eliminar(ID id);
}