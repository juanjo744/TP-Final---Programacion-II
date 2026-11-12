package Exceptions;

public class LoginException extends Exception{
    public LoginException() {
        super("Error, nombre de usuario o contraseña incorrectos.");
    }
}