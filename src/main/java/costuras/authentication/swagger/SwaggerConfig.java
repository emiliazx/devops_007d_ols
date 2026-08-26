package costuras.authentication.swagger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Authentication API")
                .version("1.0")
                .description("API para registro, login y autenticación de usuarios mediante JWT.")
                .contact(new Contact()
                    .name("Emilia Zamora")
                    .email("emi.zamora@costuras.com")));
    }

}
