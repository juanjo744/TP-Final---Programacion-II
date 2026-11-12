package Interfaces;

import java.util.List;

public interface IHotelJDBC<T, ID> {
    void guardar(T elemento);
    T buscarPorID(ID id);
    List<T> listarTodos();
    void actualizar(T elemento, ID id);
    void eliminar(ID id);
}