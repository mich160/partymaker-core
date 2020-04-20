package view.exceptions;

public class NotImplementedException extends RuntimeException {
    public NotImplementedException() {
        super("This feature is not implemented yet!");
    }
}
