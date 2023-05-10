package message;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Set;

public class Message implements Serializable {
    private String timeMessage;
    private String senderMessage = "";
    private String textMessage = "";
    private final MessageType typeMessage;
    private Set<String> users = null;
    private ArrayList<Message> history = null;
    public Message(String time, String sender, String text, MessageType type) {
        timeMessage = time;
        senderMessage = sender;
        textMessage = text;
        typeMessage = type;
    }
    public Message(String time, String text, MessageType type) {
        timeMessage = time;
        //senderMessage = sender;
        textMessage = text;
        typeMessage = type;
    }
    public Message(MessageType type) {
        typeMessage = type;
    }
    public Message(MessageType type, Set<String> _users, ArrayList<Message> _history) {
        typeMessage = type;
        users = _users;
        history = _history;
    }
    public void setName(String name) { senderMessage = name; }
    public String getTime() { return timeMessage; }
    public String getSender() { return senderMessage; }
    public MessageType getType() {
        return typeMessage;
    }
    public String getText() {
        return textMessage;
    }
    public Set<String> getUsers() { return users; }
    public ArrayList<Message> getHistory() { return history; }

}
