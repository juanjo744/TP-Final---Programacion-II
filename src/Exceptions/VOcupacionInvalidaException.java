package Exceptions;

public class VOcupacionInvalidaException extends ValidacionException{
    public VOcupacionInvalidaException()
    {
        super("Los datos de la ocupación son inválidos.");
    }

    public VOcupacionInvalidaException(String message) {
        super(message);
    }
}
