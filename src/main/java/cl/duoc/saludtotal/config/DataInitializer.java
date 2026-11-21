package cl.duoc.saludtotal.config;

import cl.duoc.saludtotal.model.Especialidad;
import cl.duoc.saludtotal.model.Medico;
import cl.duoc.saludtotal.model.Rol;
import cl.duoc.saludtotal.repository.EspecialidadRepository;
import cl.duoc.saludtotal.repository.MedicoRepository;
import cl.duoc.saludtotal.repository.RolRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner cargarDatosIniciales(RolRepository roles,
                                           EspecialidadRepository especialidades,
                                           MedicoRepository medicos) {
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
