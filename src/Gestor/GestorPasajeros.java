package Gestor;

import Interfaces.IGestorGenerico;
import Models.Pasajero;

import java.util.List;
import java.util.Map;

public class GestorPasajeros implements IGestorGenerico<Pasajero, String> {
    Map<String, Pasajero> pasajeroMap;

    public GestorPasajeros(Map<String, Pasajero> pasajeroMap) {
        this.pasajeroMap = pasajeroMap;
    }

    @Override
    public void cargarElemento(Pasajero elemento) {

    }

    @Override
    public Pasajero buscarPorID(String s) {
        return null;
    }

    @Override
    public void actualizar(Pasajero elemento, String s) {

    }

    @Override
    public List<Pasajero> listar() {
        return List.of();
    }

    @Override
    public void bajar(String s) {
    }
}
