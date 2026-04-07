package websocket.messages;

public class ErrorMessage extends ServerMessage {
    private final String errorText;

    public ErrorMessage(String errorText) {
        super(ServerMessageType.ERROR);
        this.errorText = errorText;
    }

    public String getErrorText() {
        return errorText;
    }
}