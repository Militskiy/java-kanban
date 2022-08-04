package managers.exceptions;

public class NoSuchEpicException extends Exception {
    private final String epicName;

    public NoSuchEpicException(String epic) {
        this.epicName = epic;
    }

    public String getDetailedMessage() {
        return "No such Epic exists: " + epicName;
    }
}
