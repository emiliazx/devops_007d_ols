package costuras.authentication.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class InfoController {

    @Value("${spring.application.name}")
    private String appName;

    @GetMapping("/info")
    public Map<String, Object> info() {
        return Map.of(
                "aplicacion", appName,
                "version", "2.0.0",
                "descripcion", "Microservicio de autenticacion"
        );
    }
}