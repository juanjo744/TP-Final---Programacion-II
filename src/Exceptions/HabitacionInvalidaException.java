package Exceptions;


public class HabitacionInvalidaException extends Exception {
    public HabitacionInvalidaException() {
        super("Los datos de la habitacion son invalidos.");
    }
    public HabitacionInvalidaException(String mensaje){
        super(mensaje);
    }
}
