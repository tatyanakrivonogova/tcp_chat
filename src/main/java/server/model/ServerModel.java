package server.model;

import connection.Connection;
import message.Message;

import java.util.*;

public class ServerModel {
    private Map<String, Connection> serverUsers = new HashMap<>();
    private final ArrayList<Message> history = new ArrayList<>();
    public void setServerUsers(Map<String, Connection> _serverUsers) {
        serverUsers = _serverUsers;
    }
    public Map<String, Connection> getServerUsers() {
        return serverUsers;
    }
    public Set<String> getSetOfUsers() {
        Set<String> users = new HashSet<>();
        for (Map.Entry<String, Connection> user : serverUsers.entrySet()) {
            users.add(user.getKey());
        }
        return users;
    }
    public void addUser(String name, Connection connection) {
        serverUsers.put(name, connection);
    }
    public void deleteUser(String name) {
        serverUsers.remove(name);
    }
    public ArrayList<Message> getHistory() { return history; }
    public void addHistory(Message msg) {
        if (history.size() == 5) history.remove(0);
        history.add(msg);
    }
}