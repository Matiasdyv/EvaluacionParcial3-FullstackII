package cl.duoc.saludtotal.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import cl.duoc.saludtotal.dto.MensajeContactoRequest;
import cl.duoc.saludtotal.model.MensajeContacto;
import cl.duoc.saludtotal.service.MensajeContactoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Mensajes de contacto")
@RestController
@RequestMapping("/api/v1/mensajes-contacto")
public class MensajeContactoController {

    private final MensajeContactoService servicio;

    public MensajeContactoController(MensajeContactoService servicio) {
        this.servicio = servicio;
    }

    @Operation(summary = "Enviar un mensaje de contacto (público)")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MensajeContacto crear(@Valid @RequestBody MensajeContactoRequest req) {
        return servicio.crear(req);
    }

    @Operation(summary = "Listar los mensajes recibidos (ADMIN)")
    @GetMapping
    public List<MensajeContacto> listar() {
        return servicio.listar();
    }

    @Operation(summary = "Marcar un mensaje como respondido (ADMIN)")
    @PutMapping("/{id}/responder")
    public MensajeContacto responder(@PathVariable Long id) {
        return servicio.marcarRespondido(id);
    }
}
