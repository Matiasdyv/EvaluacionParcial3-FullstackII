package cl.duoc.saludtotal.controller;

import cl.duoc.saludtotal.dto.CitaRequest;
import cl.duoc.saludtotal.dto.CitaResponse;
import cl.duoc.saludtotal.dto.EstadoCitaRequest;
import cl.duoc.saludtotal.service.CitaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/citas")
public class CitaController {

    private final CitaService servicio;

    public CitaController(CitaService servicio) {
        this.servicio = servicio;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CitaResponse agendar(@Valid @RequestBody CitaRequest req, Authentication auth) {
        return CitaResponse.de(servicio.agendar(auth.getName(), req));
    }

    @GetMapping
    public List<CitaResponse> listarTodas() {
        return servicio.listarTodas().stream().map(CitaResponse::de).toList();
    }

    @GetMapping("/mias")
    public List<CitaResponse> misCitas(Authentication auth) {
        return servicio.listarDelPaciente(auth.getName()).stream().map(CitaResponse::de).toList();
    }

    @PutMapping("/{id}/estado")
    public CitaResponse cambiarEstado(@PathVariable Long id, @Valid @RequestBody EstadoCitaRequest req) {
        return CitaResponse.de(servicio.cambiarEstado(id, req.estado()));
    }

    @PutMapping("/{id}/confirmar")
    public CitaResponse confirmar(@PathVariable Long id, Authentication auth) {
        return CitaResponse.de(servicio.confirmarPropia(id, auth.getName()));
    }

    @PutMapping("/{id}/cancelar")
    public CitaResponse cancelar(@PathVariable Long id, Authentication auth) {
        return CitaResponse.de(servicio.cancelarPropia(id, auth.getName()));
    }
}
