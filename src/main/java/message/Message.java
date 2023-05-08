package message;


import java.io.Serializable;
import java.util.Set;

public class Message implements Serializable {
    private final String senderMessage;
    private final String textMessage;
    private final MessageType typeMessage;
    private Set<String> users = null;

    public Message(String sender, String text, MessageType type) {
        senderMessage = sender;
        textMessage = text;
        typeMessage = type;
    }

    public Message(MessageType type) {
        senderMessage = "";
        textMessage = "";
        typeMessage = type;
    }
    public Message(MessageType type, Set<String> _users) {
        senderMessage = "";
        textMessage = "";
        typeMessage = type;
        users = _users;
    }
    public String getSender() { return senderMessage; }
    public MessageType getType() {
        return typeMessage;
    }
    public String getText() {
        return textMessage;
    }
    public Set<String> getUsers() { return users; }

}
