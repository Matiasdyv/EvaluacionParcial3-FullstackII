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

@Configuration
public class DataInitializer {

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
                crear(especialidades, medicos, "Medicina General", "Dra. María Pérez");
                crear(especialidades, medicos, "Pediatría", "Dr. Juan López");
                crear(especialidades, medicos, "Ginecología", "Dra. Ana González");
                crear(especialidades, medicos, "Traumatología", "Dr. Carlos Soto");
                crear(especialidades, medicos, "Dermatología", "Dra. Elena Rojas");
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

    private void crear(EspecialidadRepository er, MedicoRepository mr, String especialidad, String medico) {
        Especialidad e = er.save(new Especialidad(null, especialidad));
        Medico m = new Medico();
        m.setNombre(medico);
        m.setEspecialidad(e);
        m.setActivo(true);
        mr.save(m);
    }
}
