package Gestor;

import Exceptions.JDBCException;
import Exceptions.ValidacionException;
import Interfaces.IGestorGenerico;
import Models.Usuario;
import Repositorio.UsuarioJDBC;

import java.util.List;

public class GestorUsuarios implements IGestorGenerico<Usuario, String> {

    private UsuarioJDBC usuarioJDBC;

    public GestorUsuarios(UsuarioJDBC usuarioJDBC) {
        this.usuarioJDBC = usuarioJDBC;
    }

    @Override
    public void cargarElemento(Usuario usuario) throws ValidacionException, JDBCException {

        if (usuario == null) {
            throw new ValidacionException("El usuario no puede ser nulo.");
        }

        if (usuario.getNombreUsuario() == null
                || usuario.getNombreUsuario().isBlank()) {

            throw new ValidacionException("Debe indicar un nombre de usuario.");
        }

        if (usuario.getTipo() == null) {
            throw new ValidacionException("Debe indicar el tipo de usuario.");
        }

        if (!usuario.tieneContrasenia()) {
            throw new ValidacionException("Debe indicar una contraseña.");
        }

        if (usuarioJDBC.buscarPorID(usuario.getNombreUsuario()) != null) {
            throw new ValidacionException("El nombre de usuario ya está registrado.");
        }

        usuarioJDBC.guardar(usuario);
    }

    @Override
    public Usuario buscarPorID(String nombreUsuario) throws ValidacionException, JDBCException {

        if (nombreUsuario == null || nombreUsuario.isBlank()) {
            throw new ValidacionException("Debe indicar un nombre de usuario.");
        }

        return usuarioJDBC.buscarPorID(nombreUsuario);
    }

    @Override
    public void actualizar(Usuario usuario, String nombreUsuario) throws ValidacionException, JDBCException {

        if (nombreUsuario == null || nombreUsuario.isBlank()) {
            throw new ValidacionException("Debe indicar el nombre del usuario que desea actualizar.");
        }

        if (usuario == null) {
            throw new ValidacionException("El usuario no puede ser nulo.");
        }

        if (!nombreUsuario.equals(usuario.getNombreUsuario())) {
            throw new ValidacionException("No se puede cambiar el nombre de usuario.");
        }

        if (!usuario.tieneContrasenia()) {
            throw new ValidacionException("Debe indicar una contraseña.");
        }

        Usuario usuarioEncontrado = usuarioJDBC.buscarPorID(nombreUsuario);

        if (usuarioEncontrado == null) {
            throw new ValidacionException("No existe un usuario con ese nombre.");
        }

        if (usuario.getTipo() != usuarioEncontrado.getTipo()) {
            throw new ValidacionException("El cambio de tipo de usuario debe realizarse por separado.");
        }

        usuarioJDBC.actualizar(usuario, nombreUsuario);
    }

    @Override
    public List<Usuario> listar() throws JDBCException {
        return usuarioJDBC.listarTodos();
    }

    @Override
    public void bajar(String nombreUsuario) throws ValidacionException, JDBCException {

        if (nombreUsuario == null || nombreUsuario.isBlank()) {
            throw new ValidacionException("Debe indicar un nombre de usuario.");
        }

        Usuario usuarioEncontrado = usuarioJDBC.buscarPorID(nombreUsuario);

        if (usuarioEncontrado == null) {
            throw new ValidacionException("No existe un usuario con ese nombre.");
        }

        usuarioJDBC.eliminar(nombreUsuario);
    }
}
