package Models;

import Exceptions.JDBCException;
import Exceptions.VReservaInvalidaException;
import Gestor.GestorReservas;

public class Cliente extends Usuario {

    private GestorReservas gestorReservas;

    public Cliente(String nombreUsuario, String contrasenia, GestorReservas gestorReservas) {
        super(nombreUsuario, contrasenia);
        this.gestorReservas = gestorReservas;
    }

    public void solicitarReserva(Reserva reserva) throws VReservaInvalidaException, JDBCException {

        if (reserva == null){
            throw new VReservaInvalidaException();
        }

        reserva.setTitular(this);

        gestorReservas.cargarElemento(reserva);

    }

    public void realizarConsumo(){

    }
    public void montoAPagar(){

        
    }




}
