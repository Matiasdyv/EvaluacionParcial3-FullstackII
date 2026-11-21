package cl.duoc.saludtotal.dto;

public record AuthResponse(String token, Long id, String rut, String nombre, String email, String rol) {}
