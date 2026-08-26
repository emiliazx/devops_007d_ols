package costuras.authentication.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import costuras.authentication.dto.AutentificacionResponse;
import costuras.authentication.dto.LoginRequest;
import costuras.authentication.dto.RegisterRequest;
import costuras.authentication.excepciones.UsernameDuplicatedException;
import costuras.authentication.security.JwtService;
import costuras.authentication.service.AutentificacionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SuppressWarnings("null")
@WebMvcTest(AutentificacionController.class)
@AutoConfigureMockMvc(addFilters = false) // Desactiva la seguridad para aislar la prueba unitaria del controlador
class AutentificacionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AutentificacionService autentificacionService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Test
    void register_CaminoFeliz_Retorna201() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("juan123");
        request.setEmail("juan@mail.com");
        request.setPassword("pass123");

        AutentificacionResponse responseSimulada = AutentificacionResponse.builder()

        .token("aquí_va_el_token_jwt_generado")
        .build();
        when(autentificacionService.register(any(RegisterRequest.class))).thenReturn(responseSimulada);

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.mensaje").value("Usuario registrado correctamente"));
    }
        
    @Test
    void register_UsernameDuplicado_Retorna409() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("juan123");
        request.setEmail("juan@mail.com");
        request.setPassword("pass123");

        // Simulamos la excepción funcional de negocio de duplicados
        doThrow(new UsernameDuplicatedException("Username duplicado"))
                .when(autentificacionService).register(any(RegisterRequest.class));

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }
    
  
    @Test
    void login_CredencialesValidas_RetornaToken() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("juan@mail.com");
        request.setPassword("pass123");

        AutentificacionResponse response = AutentificacionResponse.builder()
                .token("token_jwt_simulado_12345")
                .build();

        when(autentificacionService.login(any(LoginRequest.class))).thenReturn(response);

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token_jwt_simulado_12345"));
    }
    
    @Test
    void login_CredencialesInvalidas_Retorna401() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("error@mail.com");
        request.setPassword("claveIncorrecta");

        // Simulamos un error de credenciales arrojando una excepción común
        when(autentificacionService.login(any(LoginRequest.class)))
                .thenThrow(new IllegalArgumentException("Credenciales inválidas"));

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized()); 
    } 
}