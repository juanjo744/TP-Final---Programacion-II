package Exceptions;

public class VReservaInvalidaException extends ValidacionException {
    public VReservaInvalidaException() {
        super("Los datos de la reserva son invalidos.");
    }
    public VReservaInvalidaException(String mensaje){
        super(mensaje);
    }
}
