package Interfaces;

import java.util.List;

public interface IGestorGenerico<T, ID> {
    void cargarElemento(T elemento);
    T buscarPorID(ID id);
    void actualizar(T elemento, ID id);
    List<T> listar();
    void bajar(ID id);
}