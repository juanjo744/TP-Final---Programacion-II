package Models;

import Exceptions.LoginException;

public abstract class Usuario {

    private String nombreUsuario;
    private String contrasenia;
    private boolean sesionActiva;

    public Usuario(String nombreUsuario, String contrasenia) {
        this.nombreUsuario = nombreUsuario;
        this.contrasenia = contrasenia;
        this.sesionActiva = false;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
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
}