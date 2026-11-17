package Models;

import Enums.TipoUsuario;
import Exceptions.LoginException;

public abstract class Usuario {

    private String nombreUsuario;
    private String contrasenia;
    private boolean sesionActiva;
    private TipoUsuario tipo;

    public Usuario(String nombreUsuario, String contrasenia, TipoUsuario tipo) {
        this.nombreUsuario = nombreUsuario;
        this.contrasenia = contrasenia;
        this.sesionActiva = false;
        this.tipo = tipo;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public int obtenerHashContrasenia() {
        return contrasenia.hashCode()*14;
    }

    public void setContrasenia(String contrasenia) {
        this.contrasenia = contrasenia;
    }

    public boolean isSesionActiva() {
        return sesionActiva;
    }

    private void setSesionActiva(boolean sesionActiva) {
        this.sesionActiva = sesionActiva;
    }

    public boolean verificarUsuario(int hashCode)
    {
        return hashCode == this.obtenerHashContrasenia();
    }

    public TipoUsuario getTipo() {
        return tipo;
    }

    public void setTipo(TipoUsuario tipo) {
        this.tipo = tipo;
    }

    public void iniciarSesion(String contraseniaIngresada) throws LoginException {

        if (!contraseniaIngresada.equals(contrasenia)) {
            throw new LoginException();

        }
        this.setSesionActiva(true);
    }

    public boolean cerrarSesion(){

        if (!isSesionActiva()){
            return false;
        }

        setSesionActiva(false);
        return true;
    }

    public boolean tieneContrasenia() {
        return contrasenia != null && !contrasenia.isBlank();
    }
}