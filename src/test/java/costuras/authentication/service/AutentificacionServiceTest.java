package costuras.authentication.service;


    
    import costuras.authentication.dto.AutentificacionResponse;
    import costuras.authentication.dto.LoginRequest;
    import costuras.authentication.dto.RegisterRequest;
    import costuras.authentication.excepciones.UsernameDuplicatedException;
    import costuras.authentication.excepciones.EmailDuplicatedException;
    import costuras.authentication.model.Role;
    import costuras.authentication.model.User;
    import costuras.authentication.repository.AutentificacionRepo;
    import costuras.authentication.security.JwtService;
    import org.junit.jupiter.api.BeforeEach;
    import org.junit.jupiter.api.Test;
    import org.junit.jupiter.api.extension.ExtendWith;
    import org.mockito.InjectMocks;
    import org.mockito.Mock;
    import org.mockito.junit.jupiter.MockitoExtension;
    import org.springframework.security.authentication.AuthenticationManager;
    import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
    import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
    
    import static org.assertj.core.api.Assertions.assertThat;
    import static org.assertj.core.api.Assertions.assertThatThrownBy;
    import static org.mockito.ArgumentMatchers.any;
    import static org.mockito.Mockito.*;
    
    @ExtendWith(MockitoExtension.class)
    class AutentificacionServiceTest {
    
        @Mock
        private AutentificacionRepo autentificacionRepo;
    
        @Mock
        private JwtService jwtService;
    
        @Mock
        private PasswordEncoder passwordEncoder;
    
        @Mock
        private AuthenticationManager authenticationManager;
    
        @InjectMocks
        private AutentificacionService autentificacionService;
    
        private RegisterRequest registerRequest;
        private LoginRequest loginRequest;
    
        @BeforeEach
        void setUp() {
            registerRequest = RegisterRequest.builder()
                    .username("juanperez")
                    .email("juan@mail.com")
                    .password("password123")
                    .build();
    
            loginRequest = LoginRequest.builder()
                    .email("juan@mail.com")
                    .password("password123")
                    .build();
        }
    
        
    
        @Test
        void register_usernameDisponible_debeCrearUsuarioYRetornarToken() {
            when(autentificacionRepo.existsByUsername("juanperez")).thenReturn(false);
            when(passwordEncoder.encode("password123")).thenReturn("hashed");
            when(jwtService.getToken(any(User.class))).thenReturn("jwt-token-123");
    
            AutentificacionResponse resultado = autentificacionService.register(registerRequest);
    
            assertThat(resultado.getToken()).isEqualTo("jwt-token-123");
            verify(autentificacionRepo).save(argThat(u ->
                    u.getUsername().equals("juanperez") &&
                    u.getEmail().equals("juan@mail.com") &&
                    u.getPassword().equals("hashed") &&
                    u.getRole() == Role.USER
            ));
        }
    
        @Test
        void register_usernameDuplicado_debeLanzarExcepcion() {
            when(autentificacionRepo.existsByUsername("juanperez")).thenReturn(true);
    
            assertThatThrownBy(() -> autentificacionService.register(registerRequest))
                    .isInstanceOf(UsernameDuplicatedException.class)
                    .hasMessageContaining("ya está en uso");
    
            verify(autentificacionRepo, never()).save(any());
            verify(jwtService, never()).getToken(any());
        }

        @Test
        void register_emailDuplicado_debeLanzarExcepcion() {

            when(autentificacionRepo.existsByUsername("juanperez")).thenReturn(false);

            when(autentificacionRepo.existsByEmail("juan@mail.com")).thenReturn(true);

            assertThatThrownBy(() -> autentificacionService.register(registerRequest))
                    .isInstanceOf(EmailDuplicatedException.class)
                    .hasMessageContaining("correo electrónico ya está en uso");

            verify(autentificacionRepo, never()).save(any());
            verify(jwtService, never()).getToken(any());
        }
    
        // ---------- login ----------
    
        @Test
        void login_credencialesValidas_debeRetornarToken() {
            User user = User.builder()
                    .id(1).username("juanperez").email("juan@mail.com")
                    .password("hashed").role(Role.USER)
                    .build();
    
            when(autentificacionRepo.findByEmail("juan@mail.com")).thenReturn(Optional.of(user));
            when(jwtService.getToken(user)).thenReturn("jwt-token-456");
    
            AutentificacionResponse resultado = autentificacionService.login(loginRequest);
    
            assertThat(resultado.getToken()).isEqualTo("jwt-token-456");
            verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        }
    
        @Test
        void login_emailNoExiste_debeLanzarExcepcion() {
            when(autentificacionRepo.findByEmail("noexiste@mail.com")).thenReturn(Optional.empty());
            loginRequest.setEmail("noexiste@mail.com");
    
            assertThatThrownBy(() -> autentificacionService.login(loginRequest))
                    .isInstanceOf(UsernameNotFoundException.class)
                    .hasMessageContaining("Usuario no encontrado");
    
            verify(authenticationManager, never()).authenticate(any());
            verify(jwtService, never()).getToken(any());
        }
    
        @Test
        void login_credencialesInvalidas_debePropagarExcepcionDeAuthManager() {
            User user = User.builder()
                    .id(1).username("juanperez").email("juan@mail.com")
                    .password("hashed").role(Role.USER)
                    .build();
    
            when(autentificacionRepo.findByEmail("juan@mail.com")).thenReturn(Optional.of(user));
            when(authenticationManager.authenticate(any()))
                    .thenThrow(new org.springframework.security.authentication.BadCredentialsException("Credenciales inválidas"));
    
            assertThatThrownBy(() -> autentificacionService.login(loginRequest))
                    .isInstanceOf(org.springframework.security.authentication.BadCredentialsException.class);
    
            verify(jwtService, never()).getToken(any());
        }
    
        
    
        @Test
        void registerAdmin_usernameDisponible_debeCrearAdminYRetornarToken() {
            when(autentificacionRepo.existsByUsername("juanperez")).thenReturn(false);
            when(passwordEncoder.encode("password123")).thenReturn("hashed");
            when(jwtService.getToken(any(User.class))).thenReturn("jwt-token-admin");
    
            AutentificacionResponse resultado = autentificacionService.registerAdmin(registerRequest);
    
            assertThat(resultado.getToken()).isEqualTo("jwt-token-admin");
            verify(autentificacionRepo).save(argThat(u -> u.getRole() == Role.ADMIN));
        }
    
        @Test
        void registerAdmin_usernameDuplicado_debeLanzarExcepcion() {
            when(autentificacionRepo.existsByUsername("juanperez")).thenReturn(true);
    
            assertThatThrownBy(() -> autentificacionService.registerAdmin(registerRequest))
                    .isInstanceOf(UsernameDuplicatedException.class);
    
            verify(autentificacionRepo, never()).save(any());
        }

        @Test
        void registerAdmin_emailDuplicado_debeLanzarExcepcion() {

            when(autentificacionRepo.existsByUsername("juanperez")).thenReturn(false);

            when(autentificacionRepo.existsByEmail("juan@mail.com")).thenReturn(true);

            assertThatThrownBy(() ->
                    autentificacionService.registerAdmin(registerRequest))
                    .isInstanceOf(EmailDuplicatedException.class)
                    .hasMessageContaining("correo electrónico ya está en uso");

            verify(autentificacionRepo, never()).save(any());
            verify(jwtService, never()).getToken(any());
        }
    }



