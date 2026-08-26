package costuras.authentication.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {


@NotBlank
@Email(message = "EL correo es invalido")
private String email;


@NotBlank
@Size(min = 7, message = "La contrasena debe tener al menos 7 caracteres")
private String password;

@NotBlank(message = "el nombre de usurio debe estar completo es obligatorio")
private String username;


}
