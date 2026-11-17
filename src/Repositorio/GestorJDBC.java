package Repositorio;

import Exceptions.JDBCException;

public class GestorJDBC {
    private HabitacionJDBC habitacionJDBC;
    private UsuarioJDBC usuarioJDBC;
    private PasajeroJDBC pasajeroJDBC;
    private ReservaJDBC reservaJDBC;
    private OcupacionJDBC ocupacionJDBC;

    public GestorJDBC() {
        this.habitacionJDBC = new HabitacionJDBC();
        this.usuarioJDBC = new UsuarioJDBC();
        this.pasajeroJDBC = new PasajeroJDBC();
        this.reservaJDBC = new ReservaJDBC();
        this.ocupacionJDBC = new OcupacionJDBC();
    }

    public boolean cargarTablas() throws JDBCException
    {
        try {
            habitacionJDBC.crearTabla();
            usuarioJDBC.crearTabla();
            pasajeroJDBC.crearTabla();
            reservaJDBC.crearTabla();
            ocupacionJDBC.crearTabla();
        } catch (JDBCException ex) {
            ex.printStackTrace();
            throw ex;
        }

        return true;
    }
}