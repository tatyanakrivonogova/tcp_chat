package message;

import connection.Connection;

import java.io.Serializable;

public class Message implements Serializable {
    private final String textMessage;
    private final MessageType typeMessage;

    public Message(String text, MessageType type) {
        textMessage = text;
        typeMessage = type;
    }

    public Message(MessageType type) {
        textMessage = "";
        typeMessage = type;
    }

    public MessageType getType() {
        return typeMessage;
    }
    public String getText() {
        return textMessage;
    }

}
