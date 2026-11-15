package Exceptions;

public class ReservaInvalidaException extends ValidacionException {
    public ReservaInvalidaException() {
        super("Los datos de la reserva son invalidos.");
    }
    public ReservaInvalidaException(String mensaje){
        super(mensaje);
    }
}
