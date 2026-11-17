package Models;

import Enums.TipoUsuario;
import Exceptions.JDBCException;
import Exceptions.ValidacionException;
import Gestor.GestorUsuarios;

public class Administrador extends Usuario {

    private GestorUsuarios gestorUsuarios;

    public Administrador(String nombreUsuario, String contrasenia, TipoUsuario tipo, GestorUsuarios gestorUsuarios) {
        super(nombreUsuario, contrasenia, tipo);
        this.gestorUsuarios = gestorUsuarios;
    }

    public void crearUsuario(Usuario usuario) throws ValidacionException, JDBCException {

        gestorUsuarios.cargarElemento(usuario);
    }

    public void crearBackup(){

    }


}
