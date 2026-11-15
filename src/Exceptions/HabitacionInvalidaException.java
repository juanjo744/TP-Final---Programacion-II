package Exceptions;


public class HabitacionInvalidaException extends ValidacionException {
    public HabitacionInvalidaException() {
        super("Los datos de la habitacion son invalidos.");
    }
    public HabitacionInvalidaException(String mensaje){
        super(mensaje);
    }
}
