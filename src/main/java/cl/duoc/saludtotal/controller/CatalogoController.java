package cl.duoc.saludtotal.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import cl.duoc.saludtotal.dto.ValorConsultaRequest;
import cl.duoc.saludtotal.model.Especialidad;
import cl.duoc.saludtotal.model.Medico;
import cl.duoc.saludtotal.service.CatalogoService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Catálogo: especialidades y médicos")
@RestController
@RequestMapping("/api/v1")
public class CatalogoController {

    private final CatalogoService servicio;

    public CatalogoController(CatalogoService servicio) {
        this.servicio = servicio;
    }

    @Operation(summary = "Listar las especialidades")
    @GetMapping("/especialidades")
    public List<Especialidad> especialidades() {
        return servicio.listarEspecialidades();
    }

    @Operation(summary = "Listar médicos activos (filtro opcional por especialidad)")
    @GetMapping("/medicos")
    public List<Medico> medicos(@RequestParam(required = false) Long especialidadId) {
        return servicio.listarMedicos(especialidadId);
    }

    @Operation(summary = "Obtener un médico por id")
    @GetMapping("/medicos/{id}")
    public Medico medico(@PathVariable Long id) {
        return servicio.obtenerMedico(id);
    }

    @Operation(summary = "Actualizar el valor de la consulta de un médico (ADMIN)")
    @PutMapping("/medicos/{id}/valor-consulta")
    public Medico actualizarValor(@PathVariable Long id, @Valid @RequestBody ValorConsultaRequest req) {
        return servicio.actualizarValorConsulta(id, req.valorConsulta());
    }
}
