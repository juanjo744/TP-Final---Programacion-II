package Exceptions;

public class JDBCException extends Exception{
    public JDBCException(String message) {
        super(message);
    }

    public JDBCException(String message, Throwable cause) {
        super(message, cause);
    }
}
