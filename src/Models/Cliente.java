package Models;

import Enums.Consumos;
import Enums.TipoUsuario;
import Exceptions.JDBCException;
import Exceptions.VReservaInvalidaException;
import Gestor.GestorReservas;

public class Cliente extends Usuario {

    private GestorReservas gestorReservas;
    private double gastosAdicionales;

    public Cliente(String nombreUsuario, String contrasenia, GestorReservas gestorReservas) {
        super(nombreUsuario, contrasenia, TipoUsuario.CLIENTE);
        this.gestorReservas = gestorReservas;
        gastosAdicionales = 0;
    }

    public double getGastosAdicionales() {
        return gastosAdicionales;
    }

    public void setGastosAdicionales(double gastosAdicionales) {
        this.gastosAdicionales = gastosAdicionales;
    }

    public void solicitarReserva(Reserva reserva) throws VReservaInvalidaException, JDBCException {

        if (reserva == null){
            throw new VReservaInvalidaException();
        }

        reserva.setTitular(this);

        gestorReservas.cargarElemento(reserva);

    }

    public boolean realizarConsumo(Consumos consumo){
        this.setGastosAdicionales(gastosAdicionales + consumo.getPrecio());

        return true;
    }
}