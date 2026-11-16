package Gestor;

import Exceptions.VOcupacionInvalidaException;
import Exceptions.ValidacionException;
import Interfaces.IGestorGenerico;
import Models.Ocupacion;
import Repositorio.HabitacionJDBC;
import Repositorio.OcupacionJDBC;

import java.util.List;

public class GestorOcupaciones implements IGestorGenerico<Ocupacion, Integer> {
    private OcupacionJDBC ocupacionJDBC;

    public GestorOcupaciones(OcupacionJDBC ocupacionJDBC) {
        this.ocupacionJDBC = ocupacionJDBC;
    }

    @Override
    public void cargarElemento(Ocupacion ocupacion) throws VOcupacionInvalidaException {
        if (ocupacion == null)
            throw new VOcupacionInvalidaException("Debe ingresar una ocupación válida.");

        if (ocupacion.getHabitacionReservada() == null)
            throw new VOcupacionInvalidaException("La ocupacion debe tener una habitación asociada.");

        if (ocupacion.getPasajeros().size() <= 0)
            throw new VOcupacionInvalidaException("La ocupación debe tener al menos un pasajero asociado.");

        if(ocupacion.getFechaCheckIn() == null)
            throw new VOcupacionInvalidaException("La ocupación no existe al no poder confirmarse.");

        ocupacionJDBC.guardar(ocupacion);
    }

    @Override
    public Ocupacion buscarPorID(Integer ID) throws VOcupacionInvalidaException {
        if (ID == null || ID <= 0)
            throw new VOcupacionInvalidaException("Debe ingresar un ID válido.");

        Ocupacion ocupacionEncontrada = ocupacionJDBC.buscarPorID(ID);

        if (ocupacionEncontrada == null)
            throw new VOcupacionInvalidaException("La ocupación no fue encontrada.");

        return ocupacionJDBC.buscarPorID(ID);
    }

    @Override
    public void actualizar(Ocupacion ocupacion, Integer ID) throws VOcupacionInvalidaException {
        if (ocupacion == null)
            throw new VOcupacionInvalidaException("Debe ingresar una ocupación válida.");

        if (ocupacion.getHabitacionReservada() == null)
            throw new VOcupacionInvalidaException("La ocupacion debe tener una habitación asociada.");

        if (ocupacion.getPasajeros().size() <= 0)
            throw new VOcupacionInvalidaException("La ocupación debe tener al menos un pasajero asociado.");

        if(ocupacion.getFechaCheckIn() == null)
            throw new VOcupacionInvalidaException("La ocupación no existe al no poder confirmarse.");

        if (ID == null || ID <= 0)
            throw new VOcupacionInvalidaException("Debe ingresar un ID válido.");

        ocupacionJDBC.actualizar(ocupacion, ID);
    }

    @Override
    public List<Ocupacion> listar() {
        return List.of();
    }

    @Override
    public void bajar(Integer ID) throws VOcupacionInvalidaException {
        if (ID == null || ID <= 0)
            throw new VOcupacionInvalidaException("Debe ingresar un ID válido.");

        Ocupacion ocupacionEncontrada = ocupacionJDBC.buscarPorID(ID);

        if (ocupacionEncontrada == null)
            throw new VOcupacionInvalidaException("La ocupación no fue encontrada.");

        ocupacionJDBC.eliminar(ID);
    }
}
