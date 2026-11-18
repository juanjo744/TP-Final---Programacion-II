package Models;

import Enums.Consumos;
import Enums.TipoUsuario;
import Exceptions.JDBCException;
import Exceptions.VOcupacionInvalidaException;
import Exceptions.VReservaInvalidaException;
import Gestor.GestorOcupaciones;
import Gestor.GestorReservas;

import java.math.BigDecimal;

public class Cliente extends Usuario {

    private GestorReservas gestorReservas;
    private double gastosAdicionales;
    private GestorOcupaciones gestorOcupaciones;

    public Cliente(String nombreUsuario, String contrasenia, GestorReservas gestorReservas, GestorOcupaciones gestorOcupaciones) {
        super(nombreUsuario, contrasenia, TipoUsuario.CLIENTE);
        this.gestorReservas = gestorReservas;
        this.gestorOcupaciones = gestorOcupaciones;
        gastosAdicionales = 0;
    }

    public void solicitarReserva(Reserva reserva) throws VReservaInvalidaException, JDBCException {

        if (reserva == null){
            throw new VReservaInvalidaException();
        }

        reserva.setTitular(this);

        gestorReservas.cargarElemento(reserva);

    }

    public void realizarConsumo(Integer ocupacionId, Consumos consumo) throws VOcupacionInvalidaException, JDBCException {

        gestorOcupaciones.registrarConsumo(ocupacionId, this.getNombreUsuario(), consumo);
    }

    public BigDecimal consultarGastos(Integer ocupacionId) throws VOcupacionInvalidaException, JDBCException {

        return gestorOcupaciones.obtenerGastos(ocupacionId, this.getNombreUsuario());
    }

    public BigDecimal consultarTotalEstadia(Integer ocupacionId) throws VOcupacionInvalidaException, JDBCException {

        return gestorOcupaciones.calcularTotalEstadia(ocupacionId, this.getNombreUsuario());
    }
}