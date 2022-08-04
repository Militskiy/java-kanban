package managers.exceptions;

public class LoadStateException extends RuntimeException {
    public LoadStateException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
