package cl.duoc.saludtotal.service;

import cl.duoc.saludtotal.dto.MensajeContactoRequest;
import cl.duoc.saludtotal.exception.RecursoNoEncontradoException;
import cl.duoc.saludtotal.model.EstadoMensaje;
import cl.duoc.saludtotal.model.MensajeContacto;
import cl.duoc.saludtotal.repository.MensajeContactoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MensajeContactoService {

    private final MensajeContactoRepository repo;

    public MensajeContactoService(MensajeContactoRepository repo) {
        this.repo = repo;
    }

    public MensajeContacto crear(MensajeContactoRequest req) {
        MensajeContacto m = new MensajeContacto();
        m.setNombre(req.nombre());
        m.setEmail(req.email());
        m.setTelefono(req.telefono());
        m.setMotivo(req.motivo());
        m.setMensaje(req.mensaje());
        return repo.save(m);
    }

    public List<MensajeContacto> listar() {
        return repo.findAll();
    }

    public MensajeContacto marcarRespondido(Long id) {
        MensajeContacto m = repo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Mensaje no encontrado: " + id));
        m.setEstado(EstadoMensaje.RESPONDIDO);
        return repo.save(m);
    }
}
