package costuras.authentication.excepciones;

public class EmailDuplicatedException extends RuntimeException {
    public EmailDuplicatedException(String message){
        super(message);
    }
}
