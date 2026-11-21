package cl.duoc.saludtotal.controller;

import cl.duoc.saludtotal.dto.MensajeContactoRequest;
import cl.duoc.saludtotal.model.MensajeContacto;
import cl.duoc.saludtotal.service.MensajeContactoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/mensajes-contacto")
public class MensajeContactoController {

    private final MensajeContactoService servicio;

    public MensajeContactoController(MensajeContactoService servicio) {
        this.servicio = servicio;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MensajeContacto crear(@Valid @RequestBody MensajeContactoRequest req) {
        return servicio.crear(req);
    }

    @GetMapping
    public List<MensajeContacto> listar() {
        return servicio.listar();
    }

    @PutMapping("/{id}/responder")
    public MensajeContacto responder(@PathVariable Long id) {
        return servicio.marcarRespondido(id);
    }
}
