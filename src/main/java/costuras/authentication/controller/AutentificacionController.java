package costuras.authentication.controller;

import costuras.authentication.dto.AutentificacionResponse;
import costuras.authentication.dto.LoginRequest;
import costuras.authentication.dto.RegisterRequest;
import costuras.authentication.service.AutentificacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Endpoints para registro e inicio de sesión de usuarios")
public class AutentificacionController {

    private final AutentificacionService autentificacionService;

    @Operation(summary = "Registrar nuevo usuario",
               description = "Crea una cuenta de usuario con rol USER y devuelve confirmación.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Usuario registrado correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o username duplicado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@Valid @RequestBody RegisterRequest request) {
        autentificacionService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("mensaje", "Usuario registrado correctamente"));
    }

    @Operation(summary = "Iniciar sesión",
               description = "Autentica al usuario con email y contraseña, retorna un token JWT.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Login exitoso, token generado"),
        @ApiResponse(responseCode = "400", description = "Credenciales inválidas"),
        @ApiResponse(responseCode = "401", description = "Usuario no encontrado o contraseña incorrecta")
    })
    @PostMapping("/login")
    public ResponseEntity<AutentificacionResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(autentificacionService.login(request));
    }
}
