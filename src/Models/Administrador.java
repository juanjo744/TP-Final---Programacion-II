package Models;

import Connection.DBBackup;
import Enums.TipoUsuario;
import Exceptions.JDBCException;
import Exceptions.ValidacionException;
import Gestor.GestorUsuarios;

import java.io.IOException;

public class Administrador extends Usuario {

    private GestorUsuarios gestorUsuarios;

    public Administrador(String nombreUsuario, String contrasenia, GestorUsuarios gestorUsuarios) {
        super(nombreUsuario, contrasenia, TipoUsuario.ADMINISTRADOR);
        this.gestorUsuarios = gestorUsuarios;
    }

    public void crearUsuario(Usuario usuario) throws ValidacionException, JDBCException {
        gestorUsuarios.cargarElemento(usuario);
    }

    public void asignarPermisos(Usuario usuario, TipoUsuario nuevoTipo) throws ValidacionException, JDBCException
    {
        gestorUsuarios.actualizarPermisos(usuario, nuevoTipo);
    }

    public void crearBackup() throws IOException, InterruptedException {
        DBBackup dbBackup = new DBBackup();

        dbBackup.crearBackup();
    }
}