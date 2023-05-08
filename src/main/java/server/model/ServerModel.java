package server.model;

import connection.Connection;

import java.util.HashMap;
import java.util.Map;

public class ServerModel {
    Map<String, Connection> serverUsers = new HashMap<>();
    public void setServerUsers(Map<String, Connection> _serverUsers) {
        serverUsers = _serverUsers;
    }
    public Map<String, Connection> getServerUsers() {
        return serverUsers;
    }
    public void addUser(String name, Connection connection) {
        serverUsers.put(name, connection);
    }
    public void deleteUser(String name) {
        serverUsers.remove(name);
    }
}
