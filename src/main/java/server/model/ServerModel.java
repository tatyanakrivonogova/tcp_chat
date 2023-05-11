package server.model;

import connection.Connection;
import message.Message;
import server.AbstractServer;

import java.util.*;

public class ServerModel {
    private final ArrayList<AbstractServer.ServerThread> serverThreads = new ArrayList<>();
    private final Map<String, Connection> serverUsers = new HashMap<>();
    private final ArrayList<Message> history = new ArrayList<>();
    public Map<String, Connection> getServerUsers() {
        return serverUsers;
    }
    public ArrayList<AbstractServer.ServerThread> getServerThreads() { return serverThreads; }
    public String getName(Connection connection) {
        for (Map.Entry<String, Connection> user : serverUsers.entrySet()) {
            if (user.getValue() == connection) return user.getKey();
        }
        return "";
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
    public void addThread(AbstractServer.ServerThread thread) { serverThreads.add(thread); }
    public void deleteUser(String name) {
        serverUsers.remove(name);
        serverThreads.removeIf(t -> t.getThreadName().equals(name));
    }
    public ArrayList<Message> getHistory() { return history; }
    public void addHistory(Message msg) {
        if (history.size() == 5) history.remove(0);
        history.add(msg);
    }
}
