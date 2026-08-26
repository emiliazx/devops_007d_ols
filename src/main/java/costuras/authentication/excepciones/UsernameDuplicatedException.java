package costuras.authentication.excepciones;

public class UsernameDuplicatedException extends RuntimeException {
    
    public UsernameDuplicatedException(String message) {
        super(message);
    }
}

