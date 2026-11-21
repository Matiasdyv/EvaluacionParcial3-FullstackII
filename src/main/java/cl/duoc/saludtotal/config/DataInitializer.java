package cl.duoc.saludtotal.config;

import cl.duoc.saludtotal.model.Especialidad;
import cl.duoc.saludtotal.model.Medico;
import cl.duoc.saludtotal.model.Rol;
import cl.duoc.saludtotal.model.Usuario;
import cl.duoc.saludtotal.repository.EspecialidadRepository;
import cl.duoc.saludtotal.repository.MedicoRepository;
import cl.duoc.saludtotal.repository.RolRepository;
import cl.duoc.saludtotal.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;

@Configuration
public class DataInitializer {

    private record MedicoInicial(String especialidad, String nombre, String horaInicio, String horaFin, int duracion) {}

    private static final List<MedicoInicial> MEDICOS = List.of(
            new MedicoInicial("Medicina General", "Dra. María Pérez", "08:00", "17:00", 20),
            new MedicoInicial("Pediatría", "Dr. Juan López", "09:00", "18:00", 25),
            new MedicoInicial("Ginecología", "Dra. Ana González", "08:00", "16:00", 30),
            new MedicoInicial("Traumatología", "Dr. Carlos Soto", "10:00", "19:00", 25),
            new MedicoInicial("Dermatología", "Dra. Elena Rojas", "09:00", "17:00", 20)
    );

    @Bean
    CommandLineRunner cargarDatosIniciales(RolRepository roles,
                                           EspecialidadRepository especialidades,
                                           MedicoRepository medicos,
                                           UsuarioRepository usuarios,
                                           PasswordEncoder encoder) {
        return args -> {
            if (roles.count() == 0) {
                roles.save(new Rol(null, "ADMIN"));
                roles.save(new Rol(null, "PACIENTE"));
            }

            if (especialidades.count() == 0) {
                for (MedicoInicial mi : MEDICOS) {
                    Especialidad e = especialidades.save(new Especialidad(null, mi.especialidad()));
                    Medico m = new Medico();
                    m.setNombre(mi.nombre());
                    m.setEspecialidad(e);
                    m.setActivo(true);
                    medicos.save(m);
                }
            }

            for (Medico m : medicos.findAll()) {
                if (m.getHoraInicio() == null) {
                    MEDICOS.stream().filter(mi -> mi.nombre().equals(m.getNombre())).findFirst().ifPresent(mi -> {
                        m.setHoraInicio(mi.horaInicio());
                        m.setHoraFin(mi.horaFin());
                        m.setDuracionMinutos(mi.duracion());
                        medicos.save(m);
                    });
                }
            }

            if (!usuarios.existsByEmail("admin@duoc.cl")) {
                Usuario admin = new Usuario();
                admin.setRut("11111111-1");
                admin.setNombre("Administrador");
                admin.setEmail("admin@duoc.cl");
                admin.setFechaNacimiento(LocalDate.of(1990, 1, 1));
                admin.setPassword(encoder.encode("admin123"));
                admin.setRol(roles.findByNombre("ADMIN").orElseThrow());
                admin.setActivo(true);
                usuarios.save(admin);
            }
        };
    }
}
