
package costuras.authentication.service;

import costuras.authentication.model.Role;
import costuras.authentication.model.User;
import costuras.authentication.repository.AutentificacionRepo;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

@Slf4j
@Configuration
@SuppressWarnings("null")
@Profile("!test")
public class AdminLoader {

    @Bean
    public CommandLineRunner initAdmin(
            AutentificacionRepo repo,
            PasswordEncoder passwordEncoder,

            @Value("${ADMIN_ENABLED:false}") boolean adminEnabled,
            @Value("${ADMIN_USERNAME:}") String username,
            @Value("${ADMIN_EMAIL:}") String email,
            @Value("${ADMIN_PASSWORD:}") String password) {

        return args -> {

            // Permite deshabilitar la creacion automatica del administrador
            if (!adminEnabled) {
                log.info("Creacion automatica de administrador deshabilitada.");
                return;
            }

            // Comprobar que todas las credenciales esten configuradas
            if (username.isBlank() || email.isBlank() || password.isBlank()) {
                throw new IllegalStateException(
                    "Faltan variables de entorno para crear el administrador."
                );
            }

            // Requerir una contraseña de al menos 12 caracteres
            if (password.length() < 12) {
                throw new IllegalStateException(
                    "La contraseña del administrador debe tener al menos 12 caracteres."
                );
            }

            // Evitar crear usuarios duplicados
            if (repo.existsByUsername(username) || repo.existsByEmail(email)) {
                log.info("No se crea el administrador: usuario o correo ya registrado.");
                return;
            }

            // Crear administrador utilizando las variables de entorno
            User admin = User.builder()
                    .username(username)
                    .email(email)
                    .password(passwordEncoder.encode(password))
                    .role(Role.ADMIN)
                    .build();

            repo.save(admin);

            // No mostrar credenciales en los logs
            log.info("Administrador inicial creado correctamente.");
        };
    }
}