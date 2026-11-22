package cl.duoc.saludtotal.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import cl.duoc.saludtotal.dto.CitaRequest;
import cl.duoc.saludtotal.dto.CitaResponse;
import cl.duoc.saludtotal.dto.EstadoCitaRequest;
import cl.duoc.saludtotal.service.CitaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Citas médicas")
@RestController
@RequestMapping("/api/v1/citas")
public class CitaController {

    private final CitaService servicio;

    public CitaController(CitaService servicio) {
        this.servicio = servicio;
    }

    @Operation(summary = "Agendar una cita para el usuario autenticado")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CitaResponse agendar(@Valid @RequestBody CitaRequest req, Authentication auth) {
        return CitaResponse.de(servicio.agendar(auth.getName(), req));
    }

    @Operation(summary = "Listar todas las citas (ADMIN)")
    @GetMapping
    public List<CitaResponse> listarTodas() {
        return servicio.listarTodas().stream().map(CitaResponse::de).toList();
    }

    @Operation(summary = "Listar las citas del usuario autenticado")
    @GetMapping("/mias")
    public List<CitaResponse> misCitas(Authentication auth) {
        return servicio.listarDelPaciente(auth.getName()).stream().map(CitaResponse::de).toList();
    }

    @Operation(summary = "Cambiar el estado de una cita (ADMIN)")
    @PutMapping("/{id}/estado")
    public CitaResponse cambiarEstado(@PathVariable Long id, @Valid @RequestBody EstadoCitaRequest req) {
        return CitaResponse.de(servicio.cambiarEstado(id, req.estado()));
    }

    @Operation(summary = "Confirmar una cita propia")
    @PutMapping("/{id}/confirmar")
    public CitaResponse confirmar(@PathVariable Long id, Authentication auth) {
        return CitaResponse.de(servicio.confirmarPropia(id, auth.getName()));
    }

    @Operation(summary = "Cancelar una cita propia")
    @PutMapping("/{id}/cancelar")
    public CitaResponse cancelar(@PathVariable Long id, Authentication auth) {
        return CitaResponse.de(servicio.cancelarPropia(id, auth.getName()));
    }
}
