package client.model;

import message.Message;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ClientModel {
    private final int historySize;
    Set<String> users = new HashSet<>();
    ArrayList<Message> chatMessages = new ArrayList<>();
    public ClientModel(int _historySize) {
        historySize = _historySize;
    }
    public void addMessage(Message msg) {
        if (chatMessages.size() == historySize) {
            chatMessages.remove(0);
        }
        chatMessages.add(msg);
    }
    public ArrayList<Message> getChatMessages() { return chatMessages; }
    public void setChatMessages(ArrayList<Message> messages) { chatMessages = messages; }
    public void addUser(String name) {
        users.add(name);
    }
    public void deleteUser(String name) {
        users.remove(name);
    }
    public Set<String> getUsers() {
        return users;
    }
    public void setUsers(Set<String> _users) {
        users = _users;
    }
}
