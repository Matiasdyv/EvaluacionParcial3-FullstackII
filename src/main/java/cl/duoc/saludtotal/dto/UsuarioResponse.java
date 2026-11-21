package cl.duoc.saludtotal.dto;

import cl.duoc.saludtotal.model.Usuario;

public record UsuarioResponse(
        Long id, String rut, String nombre, String apellidoPaterno, String apellidoMaterno,
        String email, String telefono, String rol, boolean activo
) {
    public static UsuarioResponse de(Usuario u) {
        return new UsuarioResponse(u.getId(), u.getRut(), u.getNombre(), u.getApellidoPaterno(),
                u.getApellidoMaterno(), u.getEmail(), u.getTelefono(), u.getRol().getNombre(), u.isActivo());
    }
}
