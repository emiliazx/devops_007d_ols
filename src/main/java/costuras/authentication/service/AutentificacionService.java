package costuras.authentication.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import costuras.authentication.dto.AutentificacionResponse;
import costuras.authentication.dto.LoginRequest;
import costuras.authentication.dto.RegisterRequest;
import costuras.authentication.excepciones.UsernameDuplicatedException;
import costuras.authentication.excepciones.EmailDuplicatedException;
import costuras.authentication.model.Role;
import costuras.authentication.model.User;
import costuras.authentication.repository.AutentificacionRepo;
import costuras.authentication.security.JwtService;
import lombok.RequiredArgsConstructor;

@Service
@SuppressWarnings("null")
@RequiredArgsConstructor
public class AutentificacionService {

    private final AutentificacionRepo autentificacionRepo;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AutentificacionResponse register(RegisterRequest request) {
        if (autentificacionRepo.existsByUsername(request.getUsername())) {
            throw new UsernameDuplicatedException("El nombre de usuario ya está en uso");
        }

        if (autentificacionRepo.existsByEmail(request.getEmail())) {
            throw new EmailDuplicatedException("El correo electrónico ya está en uso");
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();
        autentificacionRepo.save(user);
        return AutentificacionResponse.builder().token(jwtService.getToken(user)).build();
    }

    public AutentificacionResponse login(LoginRequest request) {
        // Mejorado: Cambiado RuntimeException genérico por UsernameNotFoundException específico de seguridad
        User user = autentificacionRepo.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con el email provisto"));

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), request.getPassword())
        );

        String token = jwtService.getToken(user);
        return AutentificacionResponse.builder().token(token).build();
    }

    public AutentificacionResponse registerAdmin(RegisterRequest request) {
        if (autentificacionRepo.existsByUsername(request.getUsername())) {
            throw new UsernameDuplicatedException("El nombre de usuario ya está en uso");
        }

        if (autentificacionRepo.existsByEmail(request.getEmail())) {
            throw new EmailDuplicatedException("El correo electrónico ya está en uso");
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.ADMIN)
                .build();
        autentificacionRepo.save(user);
        return AutentificacionResponse.builder().token(jwtService.getToken(user)).build();
    }
}
