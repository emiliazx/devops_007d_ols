package costuras.authentication.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data

public class ValidacionesRequest {
    
@NotBlank(message = "el token es obligatorio no puede estar vacio")
private String token;
@Email(message = "El corre debe ser un modelo valido")
private String email;

}
