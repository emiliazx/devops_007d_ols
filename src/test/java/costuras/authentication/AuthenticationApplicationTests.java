package costuras.authentication;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * EP3: los endpoints de observabilidad deben ser accesibles sin JWT
 * (Kubernetes y Prometheus no envian token), y el resto debe seguir protegido.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ActuatorObservabilityTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void liveness_esPublico() throws Exception {
        mockMvc.perform(get("/actuator/health/liveness")).andExpect(status().isOk());
    }

    @Test
    void readiness_esPublico() throws Exception {
        mockMvc.perform(get("/actuator/health/readiness")).andExpect(status().isOk());
    }

    @Test
    void prometheus_noRequiereJwt() throws Exception {
        int codigo = mockMvc.perform(get("/actuator/prometheus"))
                .andReturn().getResponse().getStatus();
        // En tests el exportador puede estar desactivado (404); lo que no debe ocurrir es 401/403.
        assertFalse(codigo == 401 || codigo == 403,
                "/actuator/prometheus no debe exigir autenticacion, codigo=" + codigo);
    }

    @Test
    void endpointNoPublico_sigueProtegido() throws Exception {
        mockMvc.perform(get("/info")).andExpect(status().is4xxClientError());
    }
}
