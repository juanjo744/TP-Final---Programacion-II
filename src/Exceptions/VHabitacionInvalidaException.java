package Exceptions;


public class VHabitacionInvalidaException extends ValidacionException {
    public VHabitacionInvalidaException() {
        super("Los datos de la habitacion son invalidos.");
    }
    public VHabitacionInvalidaException(String mensaje){
        super(mensaje);
    }
}
