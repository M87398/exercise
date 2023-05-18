package santander.exercise.processing;

public class IncorrectDataFormatException extends RuntimeException {
    public IncorrectDataFormatException(String message) {
        super(message);
    }
}
