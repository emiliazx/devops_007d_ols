package costuras.authentication.service;

import costuras.authentication.model.Role;
import costuras.authentication.model.User;
import costuras.authentication.repository.AutentificacionRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile; // 👈 Asegúrate de importar esto
import org.springframework.security.crypto.password.PasswordEncoder;

@Slf4j
@Configuration
@SuppressWarnings("null")
@Profile("!test") // 👈 ¡CLAVE! Evita que este inicializador corra e interfiera durante los Tests Unitarios
public class AdminLoader {

    @Bean
    public CommandLineRunner initAdmin(AutentificacionRepo repo, PasswordEncoder passwordEncoder) {
        return args -> {
            if (repo.existsByUsername("mariaLuz")) {
                log.info("Admin ya existe, no se crea.");
                return;
            }

            User admin = User.builder()
                    .username("mariaLuz")
                    .email("mari@gmail.com")
                    .password(passwordEncoder.encode("123"))
                    .role(Role.ADMIN)
                    .build();

            repo.save(admin);
            log.info("Admin creado con éxito: mari@gmail.com / 123");
        };
    }
}
