package costuras.authentication.excepciones;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AppExceptionHandler {


    
    @ExceptionHandler(EmailDuplicatedException.class)
    public ResponseEntity<Map<String, String>> handleEmailAlreadyInUse(EmailDuplicatedException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", ex.getMessage()));
    }

    
    @ExceptionHandler(EmailNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleEmailNotFound(EmailNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", ex.getMessage()));
    }

 
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(e -> {
            errors.put(e.getField(), e.getDefaultMessage());
        });
      
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }
    
       @ExceptionHandler(UsernameDuplicatedException.class)
public ResponseEntity<Map<String, String>> handleUsernameDuplicated(UsernameDuplicatedException ex) {
    return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(Map.of("error", ex.getMessage()));
}

@ExceptionHandler(RuntimeException.class)
public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException ex) {
    return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(Map.of("error", ex.getMessage()));
}


@ExceptionHandler(org.springframework.security.core.AuthenticationException.class)
public ResponseEntity<Map<String, String>> handleAuthenticationException(
        org.springframework.security.core.AuthenticationException ex) {
    return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(Map.of("error", "Credenciales incorrectas"));
}

}
