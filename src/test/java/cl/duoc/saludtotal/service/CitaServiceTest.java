package cl.duoc.saludtotal.service;

import cl.duoc.saludtotal.dto.CitaRequest;
import cl.duoc.saludtotal.exception.ConflictoException;
import cl.duoc.saludtotal.exception.RecursoNoEncontradoException;
import cl.duoc.saludtotal.exception.ReglaNegocioException;
import cl.duoc.saludtotal.model.Cita;
import cl.duoc.saludtotal.model.EstadoCita;
import cl.duoc.saludtotal.model.Medico;
import cl.duoc.saludtotal.model.Usuario;
import cl.duoc.saludtotal.repository.CitaRepository;
import cl.duoc.saludtotal.repository.MedicoRepository;
import cl.duoc.saludtotal.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CitaServiceTest {

    private static final String EMAIL = "paciente@mail.com";
    private static final LocalDate FECHA = LocalDate.of(2099, 1, 1);

    @Mock CitaRepository citas;
    @Mock UsuarioRepository usuarios;
    @Mock MedicoRepository medicos;
    @InjectMocks CitaService servicio;

    private Usuario paciente;
    private Medico medico;

    @BeforeEach
    void preparar() {
        paciente = new Usuario();
        paciente.setId(1L);
        paciente.setEmail(EMAIL);

        medico = new Medico();
        medico.setId(1L);
        medico.setNombre("Dra. María Pérez");
        medico.setHoraInicio("08:00");
        medico.setHoraFin("17:00");
    }

    private CitaRequest solicitud(LocalTime hora) {
        return new CitaRequest(1L, FECHA, hora, "Control");
    }

    private Cita citaDe(String emailPaciente, EstadoCita estado) {
        Usuario dueno = new Usuario();
        dueno.setEmail(emailPaciente);
        Cita c = new Cita();
        c.setId(10L);
        c.setPaciente(dueno);
        c.setMedico(medico);
        c.setEstado(estado);
        return c;
    }

    // ---------- agendar ----------

    @Test
    void agendarCreaUnaCitaSolicitada() {
        when(usuarios.findByEmail(EMAIL)).thenReturn(Optional.of(paciente));
        when(medicos.findById(1L)).thenReturn(Optional.of(medico));
        when(citas.existsByMedicoIdAndFechaAndHoraAndEstadoNot(1L, FECHA, LocalTime.of(10, 0), EstadoCita.CANCELADA))
                .thenReturn(false);
        when(citas.save(any(Cita.class))).thenAnswer(i -> i.getArgument(0));

        Cita cita = servicio.agendar(EMAIL, solicitud(LocalTime.of(10, 0)));

        assertEquals(EstadoCita.SOLICITADA, cita.getEstado());
        assertEquals(paciente, cita.getPaciente());
        assertEquals(medico, cita.getMedico());
        assertEquals(LocalTime.of(10, 0), cita.getHora());
    }

    @Test
    void agendarRechazaUnaHoraAntesDelHorarioDelMedico() {
        when(usuarios.findByEmail(EMAIL)).thenReturn(Optional.of(paciente));
        when(medicos.findById(1L)).thenReturn(Optional.of(medico));

        assertThrows(ReglaNegocioException.class, () -> servicio.agendar(EMAIL, solicitud(LocalTime.of(3, 0))));
        verify(citas, never()).save(any());
    }

    @Test
    void agendarRechazaLaHoraExactaDeTerminoDelMedico() {
        when(usuarios.findByEmail(EMAIL)).thenReturn(Optional.of(paciente));
        when(medicos.findById(1L)).thenReturn(Optional.of(medico));

        assertThrows(ReglaNegocioException.class, () -> servicio.agendar(EMAIL, solicitud(LocalTime.of(17, 0))));
    }

    @Test
    void agendarRechazaUnHorarioYaReservado() {
        when(usuarios.findByEmail(EMAIL)).thenReturn(Optional.of(paciente));
        when(medicos.findById(1L)).thenReturn(Optional.of(medico));
        when(citas.existsByMedicoIdAndFechaAndHoraAndEstadoNot(1L, FECHA, LocalTime.of(10, 0), EstadoCita.CANCELADA))
                .thenReturn(true);

        assertThrows(ConflictoException.class, () -> servicio.agendar(EMAIL, solicitud(LocalTime.of(10, 0))));
        verify(citas, never()).save(any());
    }

    @Test
    void agendarFallaSiElMedicoNoExiste() {
        when(usuarios.findByEmail(EMAIL)).thenReturn(Optional.of(paciente));
        when(medicos.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> servicio.agendar(EMAIL, solicitud(LocalTime.of(10, 0))));
    }

    @Test
    void agendarFallaSiElPacienteNoExiste() {
        when(usuarios.findByEmail(EMAIL)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> servicio.agendar(EMAIL, solicitud(LocalTime.of(10, 0))));
    }

    // ---------- listar ----------

    @Test
    void listarDelPacienteDevuelveSoloSusCitas() {
        when(usuarios.findByEmail(EMAIL)).thenReturn(Optional.of(paciente));
        when(citas.findByPacienteIdOrderByFechaDescHoraDesc(1L)).thenReturn(List.of(citaDe(EMAIL, EstadoCita.SOLICITADA)));

        assertEquals(1, servicio.listarDelPaciente(EMAIL).size());
    }

    // ---------- confirmar / cancelar ----------

    @Test
    void confirmarPasaUnaCitaSolicitadaAConfirmada() {
        when(citas.findById(10L)).thenReturn(Optional.of(citaDe(EMAIL, EstadoCita.SOLICITADA)));
        when(citas.save(any(Cita.class))).thenAnswer(i -> i.getArgument(0));

        assertEquals(EstadoCita.CONFIRMADA, servicio.confirmarPropia(10L, EMAIL).getEstado());
    }

    @Test
    void confirmarRechazaUnaCitaQueNoEstaSolicitada() {
        when(citas.findById(10L)).thenReturn(Optional.of(citaDe(EMAIL, EstadoCita.ATENDIDA)));

        assertThrows(ReglaNegocioException.class, () -> servicio.confirmarPropia(10L, EMAIL));
    }

    @Test
    void confirmarNoPermiteTocarLaCitaDeOtroPaciente() {
        when(citas.findById(10L)).thenReturn(Optional.of(citaDe("otro@mail.com", EstadoCita.SOLICITADA)));

        assertThrows(RecursoNoEncontradoException.class, () -> servicio.confirmarPropia(10L, EMAIL));
        verify(citas, never()).save(any());
    }

    @Test
    void cancelarPasaUnaCitaConfirmadaACancelada() {
        when(citas.findById(10L)).thenReturn(Optional.of(citaDe(EMAIL, EstadoCita.CONFIRMADA)));
        when(citas.save(any(Cita.class))).thenAnswer(i -> i.getArgument(0));

        assertEquals(EstadoCita.CANCELADA, servicio.cancelarPropia(10L, EMAIL).getEstado());
    }

    @Test
    void cancelarRechazaUnaCitaYaAtendida() {
        when(citas.findById(10L)).thenReturn(Optional.of(citaDe(EMAIL, EstadoCita.ATENDIDA)));

        assertThrows(ReglaNegocioException.class, () -> servicio.cancelarPropia(10L, EMAIL));
    }

    // ---------- administrador ----------

    @Test
    void cambiarEstadoFallaSiLaCitaNoExiste() {
        when(citas.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> servicio.cambiarEstado(99L, EstadoCita.ATENDIDA));
    }
}
