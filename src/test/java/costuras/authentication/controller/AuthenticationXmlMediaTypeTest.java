package costuras.authentication.controller;

import costuras.authentication.security.JwtService;
import costuras.authentication.service.AutentificacionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AutentificacionController.class)
@AutoConfigureMockMvc
class AuthenticationXmlMediaTypeTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AutentificacionService autentificacionService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Test
    void register_conXml_noEsAceptado() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_XML)
                        .content("<register><username>juan</username></register>"))
                .andExpect(status().isUnsupportedMediaType());

        verifyNoInteractions(autentificacionService);
    }

    @Test
    void login_conXml_noEsAceptado() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_XML)
                        .content("<login><email>juan@mail.com</email></login>"))
                .andExpect(status().isUnsupportedMediaType());

        verifyNoInteractions(autentificacionService);
    }
}
