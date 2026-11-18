package Gestor;

import Enums.TipoUsuario;
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

        if (usuario == null) {
            throw new ValidacionException("El usuario no puede ser nulo.");
        }

        if (nombreUsuario == null || nombreUsuario.isBlank()) {
            throw new ValidacionException("Debe indicar el nombre del usuario.");
        }

        if (!nombreUsuario.equals(usuario.getNombreUsuario())) {
            throw new ValidacionException("No se puede cambiar el nombre de usuario.");
        }

        if (usuario.getTipo() == null) {
            throw new ValidacionException("Debe indicar un tipo de usuario.");
        }

        Usuario existente = usuarioJDBC.buscarPorID(nombreUsuario);

        if (existente == null) {
            throw new ValidacionException("El usuario no existe.");
        }

        if (existente.getTipo() == TipoUsuario.CLIENTE && usuario.getTipo() != TipoUsuario.CLIENTE) {

            throw new ValidacionException("No se puede cambiar el tipo de una cuenta de cliente.");
        }

        usuarioJDBC.actualizar(usuario, nombreUsuario);
    }

    public void actualizarPermisos(Usuario usuario, TipoUsuario nuevoTipo, Usuario admin)  throws ValidacionException, JDBCException
    {
        if (usuario == null) {
            throw new ValidacionException("El usuario no fue encontrado.");
        }
        if (admin == null) {
            throw new ValidacionException("Debe indicar el administrador que realiza la operacion.");
        }
        if (usuario.getNombreUsuario().equals(admin.getNombreUsuario())) {
            throw new ValidacionException("No puedes modificar tus propios permisos.");
        }

        if (nuevoTipo == null) {
            throw new ValidacionException("El tipo de usuario no puede ser nulo.");
        }

        if (usuario == null) {
            throw new ValidacionException("El usuario no puede ser nulo.");
        }

        Usuario usuarioEncontrado = usuarioJDBC.buscarPorID(usuario.getNombreUsuario());

        if (usuarioEncontrado == null) {
            throw new ValidacionException("No existe un usuario con ese nombre.");
        }

        if (usuario.getTipo() != usuarioEncontrado.getTipo()) {
            throw new ValidacionException("El cambio de tipo de usuario debe realizarse por separado.");
        }
        if (usuarioEncontrado.getTipo() == TipoUsuario.CLIENTE && nuevoTipo != TipoUsuario.CLIENTE) {

            throw new ValidacionException("No se puede cambiar el tipo de una cuenta de cliente.");
        }

        TipoUsuario tipoAnterior = usuario.getTipo();
        usuario.setTipo(nuevoTipo);

        try {
            actualizar(usuario, usuario.getNombreUsuario());
        } catch (ValidacionException | JDBCException ex) {
            usuario.setTipo(tipoAnterior);
            throw ex;
        }
    }

    @Override
    public List<Usuario> listar() throws JDBCException {
        return usuarioJDBC.listarTodos();
    }

    public String listarString(List<Usuario> usuarioList) throws JDBCException {
        StringBuilder sb = new StringBuilder("LISTA DE USUARIOS:\n");

        for (Usuario u : usuarioList)
        {
            sb.append(u.toString());
        }

        return sb.toString();
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
    public void cambiarContrasenia(String nombreUsuario, String nuevaContrasenia) throws ValidacionException, JDBCException {

        if (nombreUsuario == null || nombreUsuario.isBlank()) {
            throw new ValidacionException("Debe indicar un nombre de usuario.");
        }

        if (nuevaContrasenia == null || nuevaContrasenia.isBlank()) {
            throw new ValidacionException("Debe indicar una contraseña nueva.");
        }

        if (usuarioJDBC.buscarPorID(nombreUsuario) == null) {
            throw new ValidacionException("No existe un usuario con ese nombre.");
        }

        usuarioJDBC.actualizarContrasenia(nombreUsuario, nuevaContrasenia);
    }
}
