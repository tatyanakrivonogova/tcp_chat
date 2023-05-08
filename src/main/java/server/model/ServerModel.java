package server.model;

import connection.Connection;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ServerModel {
    Map<String, Connection> serverUsers = new HashMap<>();
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
}
