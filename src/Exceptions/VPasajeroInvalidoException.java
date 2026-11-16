package Exceptions;

public class VPasajeroInvalidoException extends ValidacionException{
    public VPasajeroInvalidoException()
    {
        super("Los datos del pasajero son inválidos.");
    }

    public VPasajeroInvalidoException(String message) {
        super(message);
    }
}
