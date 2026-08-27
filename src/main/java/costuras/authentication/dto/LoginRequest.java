package costuras.authentication.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {



    @NotBlank(message = "el email es obligatorio")
    @Email(message = "el correo no tiene un formato valido")
    private String email;

   @NotBlank(message = "la contraseña es obligatoria")
    private String password;

 


}
