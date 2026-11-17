package com.bigjohn.comun;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

/** Registro y consulta de usuarios (compradores). */
public class UsuarioServicio {
    private static final Pattern CORREO = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    private final Repositorio<Usuario> usuarios = new RepositorioMemoria<>(Usuario::getId);
    private int siguienteId;

    public UsuarioServicio(int primerId) { this.siguienteId = primerId; }

    /** Nombre y documento son obligatorios; el documento no puede repetirse. */
    public Usuario registrar(String nombre, String documento, String telefono, String correo, String clave) {
        nombre = limpiar(nombre);
        documento = limpiar(documento);
        telefono = limpiar(telefono);
        correo = limpiar(correo);
        clave = clave == null ? "" : clave;

        if (nombre.isEmpty()) throw new IllegalArgumentException("El nombre es obligatorio.");
        if (documento.isEmpty()) throw new IllegalArgumentException("El documento es obligatorio.");
        if (!correo.isEmpty() && !CORREO.matcher(correo).matches()) {
            throw new IllegalArgumentException("El correo no tiene un formato válido.");
        }
        if (buscarPorDocumento(documento).isPresent()) {
            throw new IllegalArgumentException("Ya existe un usuario con el documento " + documento + ".");
        }
        Usuario u = new Usuario(siguienteId++, nombre, documento, telefono, correo, clave);
        usuarios.guardar(u);
        return u;
    }

    public Optional<Usuario> buscarPorDocumento(String documento) {
        String d = limpiar(documento);
        return usuarios.listar().stream().filter(u -> u.getDocumento().equalsIgnoreCase(d)).findFirst();
    }

    public List<Usuario> listar() { return usuarios.listar(); }

    private static String limpiar(String s) { return s == null ? "" : s.trim(); }
}
