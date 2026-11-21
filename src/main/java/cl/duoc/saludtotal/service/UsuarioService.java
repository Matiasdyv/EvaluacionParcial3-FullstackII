package cl.duoc.saludtotal.service;

import cl.duoc.saludtotal.dto.UsuarioResponse;
import cl.duoc.saludtotal.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarios;

    public UsuarioService(UsuarioRepository usuarios) {
        this.usuarios = usuarios;
    }

    public List<UsuarioResponse> listar() {
        return usuarios.findAll().stream().map(UsuarioResponse::de).toList();
    }
}
