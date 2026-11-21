package cl.duoc.saludtotal.controller;

import cl.duoc.saludtotal.dto.CitaRequest;
import cl.duoc.saludtotal.dto.CitaResponse;
import cl.duoc.saludtotal.dto.EstadoCitaRequest;
import cl.duoc.saludtotal.service.CitaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
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
    public CitaResponse agendar(@Valid @RequestBody CitaRequest req) {
        return CitaResponse.de(servicio.agendar(req));
    }

    /** Sin parámetro: todas las citas (admin). Con ?pacienteId=: solo las de ese paciente. */
    @GetMapping
    public List<CitaResponse> listar(@RequestParam(required = false) Long pacienteId) {
        var lista = pacienteId == null ? servicio.listarTodas() : servicio.listarPorPaciente(pacienteId);
        return lista.stream().map(CitaResponse::de).toList();
    }

    @PutMapping("/{id}/estado")
    public CitaResponse cambiarEstado(@PathVariable Long id, @Valid @RequestBody EstadoCitaRequest req) {
        return CitaResponse.de(servicio.cambiarEstado(id, req.estado()));
    }
}
