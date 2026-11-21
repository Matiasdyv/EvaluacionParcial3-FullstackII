package cl.duoc.saludtotal.controller;

import cl.duoc.saludtotal.dto.ValorConsultaRequest;
import cl.duoc.saludtotal.model.Especialidad;
import cl.duoc.saludtotal.model.Medico;
import cl.duoc.saludtotal.service.CatalogoService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class CatalogoController {

    private final CatalogoService servicio;

    public CatalogoController(CatalogoService servicio) {
        this.servicio = servicio;
    }

    @GetMapping("/especialidades")
    public List<Especialidad> especialidades() {
        return servicio.listarEspecialidades();
    }

    @GetMapping("/medicos")
    public List<Medico> medicos(@RequestParam(required = false) Long especialidadId) {
        return servicio.listarMedicos(especialidadId);
    }

    @GetMapping("/medicos/{id}")
    public Medico medico(@PathVariable Long id) {
        return servicio.obtenerMedico(id);
    }

    @PutMapping("/medicos/{id}/valor-consulta")
    public Medico actualizarValor(@PathVariable Long id, @Valid @RequestBody ValorConsultaRequest req) {
        return servicio.actualizarValorConsulta(id, req.valorConsulta());
    }
}
