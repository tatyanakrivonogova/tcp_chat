package client;

import client.gui.ClientGUI;
import client.model.ClientModel;
import connection.Connection;
import message.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class SerializationClient implements TCPClient {
    private volatile boolean isConnected = false;
    private String name;
    Connection connection;
    ClientModel model;
    ClientGUI gui;
    @Override
    public void connect() {
        if (!isConnected) {
            while (true) {
                try {
                    //InetAddress IPAddress = InetAddress.getByName("localhost");
                    InetAddress IPAddress = InetAddress.getByName(gui.getAddress());
                    //int port = 2048;
                    Socket s = new Socket(IPAddress, gui.getPort());
                    connection = new Connection(s);
                    isConnected = true;
                    gui.showInfo("Connected successfully!");
                    break;
                }
                catch (IOException e) {
                    gui.showWarning("Impossible to connect to this port. Enter another port...");
                    break;
                }
            }
        } else {
            gui.showWarning("Connection has already existed!");
        }
    }

    @Override
    public void run() {
        model = new ClientModel();
        gui = new ClientGUI(this);
        while (true) {
            if (isConnected) {
                registrateNewUser();
                gui.setName(name);
                receiveMessage();
                isConnected = false;
            }
        }
    }

    @Override
    public void disconnect() {
        try {
            if (isConnected) {
                connection.sendMessage(new Message(MessageType.DISCONNECT_USER));
                isConnected = false;
                model.getUsers().clear();
                gui.updateUsers(model.getUsers());
            } else {
                gui.showWarning("You have already disconnected");
            }
        }
        catch (IOException e) {
            gui.showError("Error while disconnection");
            isConnected = false;
            gui.updateUsers(model.getUsers());
        }
    }

    @Override
    public boolean isConnected() {
        return isConnected;
    }

    @Override
    public void sendMessage(String msg) {
        try {
            connection.sendMessage(new Message(name, msg, MessageType.TEXT_MESSAGE));
        }
        catch (IOException e) {
            gui.showError("Error while sending text message");
            isConnected = false;
            gui.updateUsers(model.getUsers());
        }
    }

    @Override
    public void receiveMessage() {
        while (isConnected) {
            try {
                Message msg = connection.receiveMessage();
                if (msg.getType() == MessageType.TEXT_MESSAGE) {
                    gui.addMessage(msg.getSender(), msg.getText());
                } else if (msg.getType() == MessageType.ADD_USER) {
                    model.addUser(msg.getText());
                    gui.updateUsers(model.getUsers());
                    gui.showInfo("New user has connected to the chat");
                } else if (msg.getType() == MessageType.DELETE_USER) {
                    model.deleteUser(msg.getText());
                    gui.updateUsers(model.getUsers());
                    gui.showInfo("User " + msg.getText() + " has disconnected");
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                gui.showError("Error while receiving message");
                isConnected = false;
                gui.updateUsers(model.getUsers());
            }
        }
    }

    @Override
    public void registrateNewUser() {
        while (true) {
            try {
                Message msg = connection.receiveMessage();
                if (msg.getType() == MessageType.REQUEST_USER_NAME) {
                    name = gui.getUserName();
                    connection.sendMessage(new Message(name, name,  MessageType.REPLY_USER_NAME));
                }
                if (msg.getType() == MessageType.NAME_NOT_AVAILABLE) {
                    gui.showWarning("This name is not available. Enter another one...");
                    name = gui.getUserName();
                    connection.sendMessage(new Message(name, name,  MessageType.REPLY_USER_NAME));
                }
                if (msg.getType() == MessageType.NAME_ACCEPTED) {
                    //model.addUser(msg.getText());
                    gui.showInfo("Name is accepted!");
                    model.setUsers(msg.getUsers());
                    break;
                }
            }
            catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                gui.showError("Problems with connection");
                isConnected = false;
                gui.updateUsers(model.getUsers());
            }
        }
    }
}
