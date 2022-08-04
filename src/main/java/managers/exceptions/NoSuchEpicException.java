package managers.exceptions;

public class NoSuchEpicException extends RuntimeException {
    public NoSuchEpicException(String message) {
        super(message);
    }
}
