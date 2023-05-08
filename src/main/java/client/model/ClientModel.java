package client.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ClientModel {
    Set<String> users = new HashSet<>();
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
