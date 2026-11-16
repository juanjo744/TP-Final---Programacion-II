package Gestor;

import Exceptions.VPasajeroInvalidoException;
import Exceptions.VReservaInvalidaException;
import Interfaces.IGestorGenerico;
import Models.Habitacion;
import Models.Pasajero;
import Repositorio.PasajeroJDBC;

import java.util.List;
import java.util.Map;

public class GestorPasajeros implements IGestorGenerico<Pasajero, String> {

    private PasajeroJDBC pasajeroJDBC;

    public GestorPasajeros(PasajeroJDBC pasajeroJDBC) {
        this.pasajeroJDBC = pasajeroJDBC;
    }

    @Override
    public void cargarElemento(Pasajero pasajero) throws VPasajeroInvalidoException {
        if (pasajero == null){
            throw new VPasajeroInvalidoException("Debe ingresar un pasajero válido.");
        }
        if (pasajero.getDNI() == null){
            throw new VPasajeroInvalidoException("El pasajero debe estar identificado por su número de documento.");
        }
        if (pasajero.getNombre() == null){
            throw new VPasajeroInvalidoException("El pasajero debe tener un nombre asociado.");
        }
        if (pasajero.getOrigen() == null || pasajero.getDomicilio() == null){
            throw new VPasajeroInvalidoException("El origen del pasajero debe estar registrado.");
        }

        Pasajero pasajeroEncontrado = pasajeroJDBC.buscarPorID(pasajero.getDNI());

        if (pasajeroEncontrado == null){
            throw new VPasajeroInvalidoException("El pasajero no fue encontrado");
        }

        // PENDIENTE:

        pasajeroJDBC.guardar(pasajero);
    }

    @Override
    public Pasajero buscarPorID(String DNI) throws VPasajeroInvalidoException{
        if (DNI == null)
            throw new VPasajeroInvalidoException("Debe ingresar un número de documento");

        if (DNI.length() != 8)
            throw new VPasajeroInvalidoException("Debe ingresar un DNI válido.");

        return pasajeroJDBC.buscarPorID(DNI);
    }

    @Override
    public void actualizar(Pasajero pasajero, String DNI) throws VPasajeroInvalidoException{
        if (pasajero == null){
            throw new VPasajeroInvalidoException("El Pasajero no puede ser nulo.");
        }
        if (pasajero.getDNI() == null){
            throw new VPasajeroInvalidoException("El pasajero debe estar identificado por su número de documento.");
        }
        if (pasajero.getNombre() == null){
            throw new VPasajeroInvalidoException("El pasajero debe tener un nombre asociado.");
        }
        if (pasajero.getOrigen() == null || pasajero.getDomicilio() == null){
            throw new VPasajeroInvalidoException("El origen del pasajero debe estar registrado.");
        }

        Pasajero pasajeroEncontrado = pasajeroJDBC.buscarPorID(pasajero.getDNI());

        if (pasajeroEncontrado == null){
            throw new VPasajeroInvalidoException("El pasajero no fue encontrado");
        }

        if (DNI == null)
            throw new VPasajeroInvalidoException("Debe ingresar un número de documento");

        if (DNI.length() != 8)
            throw new VPasajeroInvalidoException("Debe ingresar un DNI válido.");

        pasajeroJDBC.actualizar(pasajero, DNI);
    }

    @Override
    public List<Pasajero> listar() {
        return List.of();
    }

    @Override
    public void bajar(String DNI) throws VPasajeroInvalidoException{
        if (DNI == null)
            throw new VPasajeroInvalidoException("Debe ingresar un número de documento");

        if (DNI.length() != 8)
            throw new VPasajeroInvalidoException("Debe ingresar un DNI válido.");

        Pasajero pasajeroEncontrado = pasajeroJDBC.buscarPorID(DNI);

        if (pasajeroEncontrado == null){
            throw new VPasajeroInvalidoException("El pasajero no fue encontrado");
        }

        pasajeroJDBC.eliminar(DNI);
    }
}
