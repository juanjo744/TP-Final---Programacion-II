package Models;

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

    public String getContrasenia() {
        return contrasenia;
    }

    public void setContrasenia(String contrasenia) {
        this.contrasenia = contrasenia;
    }

    public boolean isSesionActiva() {
        return sesionActiva;
    }

    public void setSesionActiva(boolean sesionActiva) {
        this.sesionActiva = sesionActiva;
    }

    public boolean iniciarSesion(String nombreUsuarioIngresado, String contraseniaIngresada){

        if (!nombreUsuarioIngresado.equals(nombreUsuario) || !contraseniaIngresada.equals(contrasenia)){

            return false;
        }

        setSesionActiva(true);
        return true;
    }

    public boolean cerrarSesion(){

        if (!isSesionActiva()){
            return false;
        }

        return true;
    }



}
