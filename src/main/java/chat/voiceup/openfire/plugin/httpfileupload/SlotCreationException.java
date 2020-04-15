package chat.voiceup.openfire.plugin.httpfileupload;

public class SlotCreationException extends RuntimeException {

    private int statusCode;

    public SlotCreationException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
