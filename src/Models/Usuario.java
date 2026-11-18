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

    public int obtenerHashContrasenia(String contraseniaNueva) {
        return contraseniaNueva.hashCode()*14;
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


    public TipoUsuario getTipo() {
        return tipo;
    }

    public void setTipo(TipoUsuario tipo) {
        this.tipo = tipo;
    }

    public void iniciarSesion() {
        this.sesionActiva = true;
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

    @Override
    public String toString() {
        return "Usuario{" +
                "nombreUsuario='" + nombreUsuario + '\'' +
                ", sesionActiva=" + sesionActiva +
                ", tipo=" + tipo +
                '}';
    }
}